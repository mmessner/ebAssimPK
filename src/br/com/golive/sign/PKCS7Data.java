package br.com.golive.sign;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.WSKeyStore;
import br.com.golive.sign.ks.core.Keystore;
import br.com.golive.sign.ks.core.KeystoreException;
import br.com.golive.sign.ks.core.KeystoreFactory;
import br.com.golive.sign.ks.core.Keystores;
import br.com.golive.util.Util;

/**
 * Sign and Verify data
 * 
 * @author Carlos Cambra
 *
 */
public class PKCS7Data {
	private static Logger log = LogUtil.getLogInstance(PKCS7Data.class.getName());
	
	private InputStream in;

	private Keystore ks;
	
	private byte[] data;
	
	public PKCS7Data(InputStream in) {
		this.in = in;
	}	
	
	/**
	 * Sign first time
	 * 
	 * @param type
	 * @param alias
	 * @param os
	 * @return
	 * @throws XMLSignatureException
	 * @throws CertificateException 
	 */
	public boolean sign(Keystores type,String alias, OutputStream os) throws XMLSignatureException, CertificateException{
		log.debug("Inicio assinatura PKCS#7. [Metodo: BC]");
		
		loadKeyStore(alias, type);
		
		data = loadData(in);
		
		boolean isSigned = checkDocumentAlreadySigned(data);
		
		// create BC signer
		CMSSignedDataGenerator generator = null;
		CMSProcessable content = null;
		try{
			generator = new CMSSignedDataGenerator();
			
			// get original data to be signed; without signed content			
			// add information from previous signatures
			if(isSigned){
				CMSSignedData s = new CMSSignedData(data);
				data = (byte[])s.getSignedContent().getContent();
				generator.addSigners(s.getSignerInfos()); 
				generator.addCertificatesAndCRLs(s.getCertificatesAndCRLs("Collection", "BC"));
			}
			
			// add new signer
			generator.addSigner( ks.getPrivKey(), 
					  		     (X509Certificate) ks.getCert(),
					  			 CMSSignedDataGenerator.DIGEST_SHA1);
			
			generator.addCertificatesAndCRLs(ks.getCertStore());
			content = new CMSProcessableByteArray(data);
		}
		catch(Exception e){
			log.error(e);
			throw new XMLSignatureException("Erro criar Gerador de assinatura do BC. Detalhes no log.");
		}
		
		// sign message !!!
		CMSSignedData signedData;
		try {						
			// TODO - MUDAR PARA KEYSTORE MS
			signedData = generator.generate(content, true, "SunMSCAPI");
//			signedData = generator.generate(content, true, "BC");
			data = signedData.getEncoded();
		} catch (Exception e) {
			log.error(e);
			throw new XMLSignatureException("Erro ao assinar mensagem. Detalhe no log.");
		}		
		
		// fill OutputStream with signed data !
		try {
			Util.byteToOut(data, os);
		} catch (Exception e) {
			log.error(e);
			throw new XMLSignatureException("Erro ao copiar dados assinados para saida! Detalhes no log!");
		}
		log.debug("Fim assinatura PKCS#7. [Metodo: BC]");
		return true;
	}


	/**
	 *  Sign again 
	 * 
	 * @param type
	 * @param alias
	 * @param os
	 * @return
	 * @throws XMLSignatureException
	 * @throws CertificateException 
	 */
	public boolean signAgain(Keystores type,String alias, OutputStream os) throws XMLSignatureException, CertificateException{
		log.debug("Inicio assinatura PKCS#7. [Metodo: BC]");
		
		Security.addProvider(new BouncyCastleProvider());
				
		// obtain keystore implementation and load keys!
		// Keytore type passed as parameter!
		try {
			ks = KeystoreFactory.getKeystoreIstance(type);
			ks.load(alias);
		} catch (KeystoreException e) {
			throw new XMLSignatureException(e);
		} catch (KeyStoreException e) {
			throw new XMLSignatureException(e);
		} catch (CertificateException e) {
			throw new CertificateException(e);
		}		

		// get original content to be signed
		CMSSignedData s = null;
		CMSProcessable signedContent = null;
		byte[] data = null;
		try {
			s = new CMSSignedData(in);
			signedContent = s.getSignedContent();
			data = (byte[])signedContent.getContent();
		} catch (CMSException e) {
			log.error(e);
			throw new XMLSignatureException("Erro ao obter conteudo");
		}

		// create BC signer
		CMSSignedDataGenerator generator = null;
		CMSProcessable content = null;
		try{
			generator = new CMSSignedDataGenerator();
			
			// add cert found in received file
			generator.addSigners(s.getSignerInfos()); 
			generator.addCertificatesAndCRLs(s.getCertificatesAndCRLs("Collection", "BC"));
			
			generator.addSigner( ks.getPrivKey(), 
					  		     (X509Certificate) ks.getCert(),
					  			 CMSSignedDataGenerator.DIGEST_SHA1);
			
			generator.addCertificatesAndCRLs(ks.getCertStore());
			content = new CMSProcessableByteArray(data); // original data
		}
		catch(Exception e){
			log.error(e);
			throw new XMLSignatureException("Erro criar Gerador de assinatura do BC. Detalhes no log.");
		}
		
		// sign message !!!
		CMSSignedData signedData;
		try {
			signedData = generator.generate(content, true, "BC");
			data = signedData.getEncoded();
		} catch (Exception e) {
			log.error(e);
			throw new XMLSignatureException("Erro ao assinar mensagem. Detalhe no log.");
		}		
		
		// fill OutputStream with signed data !
		try {
			Util.byteToOut(data, os);
		} catch (Exception e) {
			log.error(e);
			throw new XMLSignatureException("Erro ao copiar dados assinados para saida! Detalhes no log!");
		}
		log.debug("Fim assinatura PKCS#7. [Metodo: BC]");
		return true;
	}
		
	/**
	 * Verify signatures
	 * 
	 * @param type
	 * @param alias
	 * @param in
	 * @return
	 * @throws XMLSignatureException
	 */
	public boolean verify(Keystores type, String alias, InputStream in)
			throws XMLSignatureException {
		
		log.debug("Inicio verificação PKCS#7. [Metodo: BC]");
		
		Security.addProvider(new BouncyCastleProvider());
		
		boolean verified = false;
		
		byte[] signedBytes;
		try {
			signedBytes = Util.inToByte(in);
		} catch (IOException e) {
			log.error(e);
			throw new XMLSignatureException(e);
		}
		
		CMSSignedData s = null;
		CertStore certs = null;
		SignerInformationStore signers = null;
		int result = 1;
		try {
			s = new CMSSignedData(signedBytes);
			certs = s.getCertificatesAndCRLs("Collection", "BC");
			signers = s.getSignerInfos();		    
			log.debug("Numero de assinaturas encontradas: [" + signers.size() + "]");			
			int c = 1;
			for (Iterator i = signers.getSigners().iterator(); i.hasNext(); ) {
			  log.debug("Verificando assinatura do assinador " + c++);
			  SignerInformation signer = (SignerInformation) i.next();
			  
			  log.debug("Signer ID:" + signer.getSID());
			  
			  Collection<? extends Certificate> certCollection = certs.getCertificates(signer.getSID());
			  
			  log.debug("Numero de certificados: [" + certCollection.size() + "]");
			  
			  if (!certCollection.isEmpty()) {
			    X509Certificate cert = (X509Certificate) certCollection.iterator().next();
			    if (!signer.verify(cert.getPublicKey(), "BC")) {
			    	result = result * 0;
			    }
			    log.debug("Assinatura: " + true);
			  }else{
				  log.debug("Não é possivel validar esta assinatura. Sem cerificado!");
				  log.debug("Assinatura: false");
			  }
			}
		} catch (Exception e) {
			log.error(e);
			throw new XMLSignatureException(e);
		}
		
		verified = (result==1) ? true : false;
		
		log.debug("RESULTADO FINAL: " + verified);
		log.debug("Fim verificação PKCS#7. [Metodo: BC]");
		
		return verified;
	}

	public static void main(String[] args) {
		try {
			new PKCS7Data(null).loadKeyStore("brasiltelecom", Keystores.MSWINDOWS);
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		}
	}
	
	private void loadKeyStore(String alias, Keystores type) throws XMLSignatureException, CertificateException{
		Security.addProvider(new BouncyCastleProvider());
		
		// obtain keystore implementation and load keys!
		// Keytore type passed as parameter!
		try {
			ks = KeystoreFactory.getKeystoreIstance(type);
			ks.load(alias);
		} catch (KeystoreException e) {
			throw new XMLSignatureException(e);
		} catch (CertificateException e) {
			throw new CertificateException(e);
		} catch (KeyStoreException e) {
			throw new XMLSignatureException(e);
		}
	}
	
	private boolean checkDocumentAlreadySigned(byte[] data) throws XMLSignatureException{
		log.debug("Inicio da checagem do arquivo");
		try {
			CMSSignedData s = new CMSSignedData(data);
			SignerInformationStore signers = s.getSignerInfos();
			if(signers!=null && signers.size()>0){
				log.debug("Encontrados " + signers.size() + " assinadores.");
				return true;
			}
		} catch (CMSException e) {}
		log.debug("Arquivo ainda não assinado!");
		log.debug("Fim da checagem do arquivo");
		return false;
	}
	
	private byte[] loadData(InputStream in) throws XMLSignatureException{
		try {
			return Util.inToByte(in);
		} catch (IOException e) {
			log.error(e);
			throw new XMLSignatureException("Erro ao converter stream de entrada. Detalhes no log.");
		}
	}
}
