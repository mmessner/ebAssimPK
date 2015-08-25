package br.com.golive.sap.server;

import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.b64.Base64Tool;
import br.com.golive.config.AppConfigUtil;
import br.com.golive.gzip.GZIPTool;
import br.com.golive.log.LogUtil;
import br.com.golive.sign.PKCS7Data;
import br.com.golive.sign.XMLSignatureException;
import br.com.golive.sign.ks.core.Keystores;
import br.com.golive.token.TokenReader;
import br.com.golive.util.Util;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Function;
import com.sap.mw.jco.JCO.ParameterList;

public class Server extends JCO.Server{
	private static Logger log = LogUtil.getLogInstance(Server.class.getName());
	
	private Properties sapConf;
	private Properties appConf;
	private ReceivedFile receivedFile;
	private Keystores ksType;
	private String certAlias;
	// private Boolean writeFile;
	
	private static final String STATUS_SUCCESS = "01";
	private static final String STATUS_ERRO = "02";
	private static final String STATUS_CERT_NOT_FOUND = "03";
	
	public Server(IRepository rep, Properties appConfig, Properties sapConfig) {
		super(sapConfig, rep);
		this.sapConf = sapConfig;
		this.appConf = appConfig;
		this.ksType = Keystores.getksType(appConf.getProperty("token.keystore.type"));
		this.certAlias = appConf.getProperty("token.cert.alias");
		// this.writeFile = AppConfigUtil.getBooleanValueDefault("file.output", Boolean.FALSE);
	}
	
	@Override
	protected void handleRequest(Function function) {
		log.warn("Inicio do processamento do arquivo!");
		String userid = new String();
		
		try {
			
			receivedFile = parseReceivedFile(function);
						
			if(receivedFile==null) 
				throw new NullPointerException("Arquivo recebido nulo.");
			
		    userid = receivedFile.getUserID();
		    //log.warn("Salva Arquivo (" + writeFile + ")");
			log.warn("Identificacao Procurador (" + userid + ")");
			log.warn("ksType " + ksType);
			log.warn("certAlias " + certAlias);
			
			if ( userid.equalsIgnoreCase("U1"))
				{
				 log.warn("Compress Arquivo");
				 compress(receivedFile);
				 log.warn("Arquivo Comprimito");
				
                 /* V070110-01				
				 log.warn("Inicio Assinatura (p1) do arquivo ");
			     sign_p1(receivedFile,ksType,certAlias);
			     log.warn("Fim Assinatura (p1) do arquivo ");;
                 */			    
			    }
			else	
			    {
				 log.warn("Inicio Assinatura (p2) do arquivo ");
				 sign(receivedFile,ksType,certAlias);
				 log.warn("Fim Assinatura (p2) do arquivo ");

				 log.warn("Base64 Arquivo");
				 encode(receivedFile);
			    }
			
			fillRFCReturnParameters(function);
		}catch (CertificateException e) {
			fillRFCErrorParameters(function, STATUS_CERT_NOT_FOUND, e.getMessage()); 
		} catch (InvalidRequestException e) {
			fillRFCErrorParameters(function, STATUS_ERRO, e.getMessage());
		}
		
		TokenReader.stopServer();
		
		log.warn("Fim do processamento do arquivo!");
	}
	
	/**
	 * Validade data sent by SAP and create Business Object representing Received File
	 * 
	 * @param function
	 * @return
	 * @throws InvalidRequestException
	 */
	private ReceivedFile parseReceivedFile(Function function) throws InvalidRequestException{
		String method = "parseReceivedFile";
		log.debug("Inicio do metodo " + method);
		
		String userID = "";
		String fileID = "";
		String ecpf   = "";
		JCO.Table tblArquivo = null;
		receivedFile = null;
		try{
			ParameterList input = function.getImportParameterList();
			userID = Util.getString(input,"I_IDENT"); // USER ID
			fileID = Util.getString(input,"I_TSNAM"); // FILE NAME
			ecpf   = Util.getString(input,"I_ECPF"); // ECPF
			
			this.certAlias = ecpf;
				
			input = function.getTableParameterList();
			tblArquivo = input.getTable("T_ARQUIVO");
		}catch(Exception e){
			log.error("Erro ao processar dados recebidos. " + e);
			throw new InvalidRequestException("Erro ao processar dados recebidos. Erro:" + e);
		}
		
		if(userID==null)
			throw new InvalidRequestException("Erro: identificação do usuário inválida. UserId=" + userID);

		log.debug("Identificação do usuário: " + userID);
		
		if(fileID==null)
			throw new InvalidRequestException("Erro: identificação do arquivo inválida. fileID=" + fileID);	
		
		log.debug("Identificação do arquivo: " + fileID);
		
		//if(tblArquivo==null || tblArquivo.isEmpty()){
		//	throw new InvalidRequestException("Erro: tabela contendo arquivo vazia ou não enviada.");
		//}
		
		if(log.isDebugEnabled()){
			Util.printTable(tblArquivo); // log received file table
		}

		log.debug("Fim do metodo " + method);
		
		return new ReceivedFile(fileID,userID,tblArquivo);
	}
	
	private void fillRFCReturnParameters(JCO.Function function) throws InvalidRequestException{
		JCO.Table tblArquivo = null;
		try{
			ParameterList param = function.getExportParameterList();
			param.setValue(STATUS_SUCCESS, "E_STATUS");
			String desc = "Arquivo [" + receivedFile.getFileID() + "]";
//			desc += "assinado com sucesso pelo usuário [" + receivedFile.getUserID() + " ].";
			desc += " assinado com sucesso.";
			param.setValue(desc, "E_DESCRICAO");
			param = function.getTableParameterList();
			tblArquivo = param.getTable("T_ARQUIVO");
			receivedFile.fillTable(tblArquivo);
			log.warn(desc);
		}catch(Exception e){
			log.error(e);
			throw new InvalidRequestException("Erro ao preencher tabela de retorno do arquivo.");
		}
	}

	

	private void sign_p1(ReceivedFile recFile, Keystores type, String alias) throws InvalidRequestException, CertificateException{
		try{
			PKCS7Data data = new PKCS7Data(recFile.inStream());
			data.sign(type, alias, recFile.createSignedStream());
			recFile.setZipData(recFile.getSignedStream());
			recFile.setB64Data(recFile.getZipData());
		}catch (CertificateException e) {
			log.error("CertificateException " + e);
			throw new CertificateException(e);
		} catch (XMLSignatureException e) {
			log.error("XMLSignatureException " +e);
			throw new InvalidRequestException(e);
		}
	}

	
	private void sign(ReceivedFile recFile, Keystores type, String alias) throws InvalidRequestException, CertificateException{
		try{
			log.warn("Inicio PKCS7Data ");
			PKCS7Data data = new PKCS7Data(recFile.inStream());
			log.warn("Inicio PKCS7Data OK ");
			data.sign(type, alias, recFile.createSignedStream());
			log.warn("data.sign OK ");
		}catch (CertificateException e) {
			log.error("CertificateException " + e);
			throw new CertificateException(e);
		} catch (XMLSignatureException e) {
			log.error("XMLSignatureException " +e);
			throw new InvalidRequestException(e);
		}
	}
	
	private void compress(ReceivedFile recFile) throws InvalidRequestException{
		try{
			GZIPTool zip = new GZIPTool(recFile.getSignedStream());
			recFile.setZipData(zip.compress());
		}catch(Exception e){
			log.error(e);
			throw new InvalidRequestException("Erro ao compactar arquivo. Detalhes no log.");
		}
	}
	
	private void encode(ReceivedFile recFile) throws InvalidRequestException{
		try{
			Base64Tool b64 = new Base64Tool();
			recFile.setB64Data(b64.encode(recFile.getZipData()).getBytes());
		}catch(Exception e){
			log.error(e);
			throw new InvalidRequestException("Erro ao converter arquivo para Base 64. Detalhes no log.");
		}
	}	
	
	private void fillRFCErrorParameters(JCO.Function function, String status, String description){		
		try{
			ParameterList param = function.getExportParameterList();
			param.setValue(status, "E_STATUS");
			param.setValue(description, "E_DESCRICAO");
		}catch(Exception ex){
			log.error(ex);
		}
	}
}
