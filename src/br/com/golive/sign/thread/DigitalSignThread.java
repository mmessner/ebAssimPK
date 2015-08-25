package br.com.golive.sign.thread;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.config.AppConfigUtil;
import br.com.golive.config.ConfigUtil;
import br.com.golive.log.LogUtil;
import br.com.golive.sap.rfc.ReceiveDataRFC;
import br.com.golive.sign.XMLData;
import br.com.golive.sign.XMLSignatureException;
import br.com.golive.sign.ks.core.Keystores;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

/**
 * Sign xml and send result to SAP. 
 * In case of error send it answer to SAP.
 * 
 * @author Carlos Cambra
 *
 */
public class DigitalSignThread implements Runnable{

	private static Logger log = LogUtil.getLogInstance(DigitalSignThread.class.getName());	
	
	private final String CHARSET = "ISO-8859-1";
	
	private Properties SAPConf;
	
	private String keystore;
	
	private Keystores ENkeystore;
	
	private String cert;
	
	private String xml;
	
	private XMLData xmlData;
	
	private OutputStream os;
	
	private String errorDescription;
	
	private boolean signed = false;;
	
	public DigitalSignThread(Properties SAPConf, String xml) {
		this.xml = xml;
		this.SAPConf = SAPConf;
	}
	
	@Override
	public void run() {
		log.info("Inicio da Thread de assinatura.");
		
		readConfig();
		
		// used to store signed xml
		os = new ByteArrayOutputStream();
		
		try {
			xmlData = new XMLData(new ByteArrayInputStream(xml.getBytes()));
			signed = xmlData.sign(ENkeystore,cert,os);
		} catch (XMLSignatureException e) {
			errorDescription = "Erro ao assinar XML. Erro: " + e + e.getMessage();
			log.error(errorDescription);
		}
		
		// call SAP
		sendResultToSAP();
		
		log.info("Fim da Thread de assinatura.");
	}
	
	private void readConfig(){
		log.debug("Inicio leitura configurações de certificado.");
		try {
			Properties conf = AppConfigUtil.getConfig();
			
			this.keystore = conf.getProperty("token.keystore.type");
			this.cert = conf.getProperty("token.cert.alias");
			
			log.debug("keystore=" + this.keystore);
			log.debug("certficate=" + this.cert);
			
			// TODO - Corrigir isso!
			if(keystore.equals("MSWINDOWS")){
				ENkeystore = Keystores.MSWINDOWS;
			} else if(keystore.equals("DEFAULT")){
				ENkeystore = Keystores.DEFAULT;
			}
			
			log.debug("ENKeystore=" + ENkeystore);
		} catch (Exception e) {
			log.error(e);
			Thread.currentThread().stop();
		}
		
		log.debug("Fim leitura configurações de certificado.");
	}
	
	private void sendResultToSAP(){
		log.debug("Inicio envio do retorno ao SAP");
		String ret = null;
		try{
			JCO.Client client = JCO.createClient(SAPConf);
			
			JCO.Function func = createFunction(client);
			setParameters(func);
			
			// execute function
			client.execute(func);
			
			// TODO - verificar se o parametro sera SATUS
			JCO.ParameterList output = func.getExportParameterList();
			ret = output.getString("STATUS");
		}
		catch(Exception e){
			log.error("Erro ao enviar retorno ao SAP. Erro: " + e + e.getMessage());
		}
			
		log.debug("STATUS DO ENVIO DO RETORNO AO SAP: " + ret);
		
		log.debug("Fim envio do retorno ao SAP");
	}
	
	private JCO.Function createFunction(JCO.Client client){
		client.connect();
		
		IRepository repository = JCO.createRepository("REP", client);
		JCO.Function func = ReceiveDataRFC.createFunction(repository);
		
		return func;
	}
	
	private void setParameters(JCO.Function func){
		log.debug("Inicio setando parametros do retorno");
		
		// TODO - Setar todos os parametros
		//JCO.Table tbl = func.getTableParameterList().getTable("XML");
		
		log.debug("debug de todos os parametros");
		// TODO - preenche tabela SAP
		
		log.debug("Fim setando parametros do retorno");
	}
}
