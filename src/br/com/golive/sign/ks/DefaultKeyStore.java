package br.com.golive.sign.ks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.core.Keystore;

/**
 *  Local KeyStore. Used for test purposes only.
 * 
 * @author Carlos Cambra
 * 
 */
public class DefaultKeyStore implements Keystore {

	private static Logger log = LogUtil.getLogInstance(DefaultKeyStore.class.getName());
	
	private Certificate cert;
	
	private CertStore certStore; 
	
	private PublicKey dsaPublicKey;

	private PrivateKey privKey;

	private boolean deamon = false;

	private static final String KSFILENAME = "D:\\workspace.europa\\TokenReader\\Keystore\\novakeystore.jks";
	
	private static final char[] PASSWORD = new char[]{'c','h','a','n','g','e','i','t'};

	public DefaultKeyStore() {}

	public PublicKey getPublicKey() {
		return dsaPublicKey;
	}

	public void setPublicKey(PublicKey dsaPublicKey) {
		this.dsaPublicKey = dsaPublicKey;
	}

	public PrivateKey getPrivKey() {
		return privKey;
	}

	public void setPrivKey(PrivateKey privKey) {
		this.privKey = privKey;
	}

	private boolean isDeamon() {
		return deamon;
	}

	public void setDeamon(boolean deamon) {
		this.deamon = deamon;
	}

	public Certificate getCert() {
		return cert;
	}
	
	public boolean load(String alias) throws KeyStoreException, CertificateException {
		String method = "load";
		log.debug("Inicio metodo " + method);
		
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			
			// carrega key store
			log.debug("Carregando KeyStore do arquivo " + KSFILENAME);
			InputStream is = new FileInputStream(new File(KSFILENAME));
			ks.load(is, new char[]{});
		}catch(FileNotFoundException e){
			throw new KeyStoreException("Erro ao carregar KeyStore. Arquivo " + KSFILENAME + " não encontrado!");
		} catch (KeyStoreException e) {
			throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
		} catch (CertificateException e) {
			throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
		} catch (IOException e) {
			throw new KeyStoreException("Erro ao ler arquivo " + KSFILENAME +"!");
		}

		if (ks == null) {
			throw new KeyStoreException("Erro ao carregar KeyStore. KeyStore nula.");
		}

		// create CertStore		
		try {
			ArrayList<Certificate> list = new ArrayList<Certificate>();
			Certificate[] certificates = ks.getCertificateChain(alias);
			for (int i = 0, length = certificates == null ? 0 : certificates.length; i < length; i++) {
				list.add(certificates[i]);
			}
			this.certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(list), "BC");
		} catch (InvalidAlgorithmParameterException e) {
			throw new KeyStoreException("Erro ao criar CertStore para alias [" + alias + "]. Descrição: " + e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException("Erro ao criar CertStore para alias [" + alias + "]. Descrição: " + e);
		} catch (NoSuchProviderException e) {
			throw new KeyStoreException("Erro ao criar CertStore para alias [" + alias + "]. Descrição: " + e);
		}

		// get certificate from keystore
		cert = null;
		try {
			log.debug("Obtendo certificado [" + alias + "]");
			cert = ks.getCertificate(alias);
			if (cert == null){
				printAliasses(ks.aliases());
				throw new KeyStoreException("Centificado [" + alias + "] não encontrado!");				
			}
		} catch (KeyStoreException e) {
			throw new CertificateException(e.getMessage());
			
		}

		// get private key from keystore
		try {
			privKey = (PrivateKey) ks.getKey(alias, PASSWORD);
			
			if(privKey==null){
				printAliasses(ks.aliases());
				throw new KeyStoreException("Erro PrivateKey não encontrada no certificado [" + alias + "]");
			}
		} catch (UnrecoverableKeyException e) {
			throw new KeyStoreException("Erro ao obter private key" + e + e.getMessage());
		}  
		catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException("Erro ao obter private key" + e + e.getMessage());
		}		
		
		// get public key from certificate
		dsaPublicKey = cert.getPublicKey();

		if(dsaPublicKey==null)
			throw new KeyStoreException("Erro PublicKey não encontrada no certificado [" + alias + "]");		
		
		// Deamon apenas para teste - Gera Keys
		if (isDeamon()) {
			log.debug("Private ou Public key nulas! Criando chaves DAEMON!");
			// Create a DSA KeyPair
			KeyPairGenerator kpg;
			try {
				kpg = KeyPairGenerator.getInstance("DSA");
				kpg.initialize(1024);
				KeyPair kp = kpg.generateKeyPair();
				privKey = kp.getPrivate();
				dsaPublicKey = kp.getPublic();
				log.debug("Chaves criadas com sucesso!");
			} catch (NoSuchAlgorithmException e) {
				log.error("Erro ao criar chaves!" + e + e.getMessage());
			}
		}
		
		log.debug("Fim metodo " + method);
		return true;
	}
	
	private void printAliasses(Enumeration<String> a){
		System.out.println("Alias disponiveis:");
		if(!a.hasMoreElements()){
			System.out.println("Não ha certiticados!!");
		}
		
		while(a.hasMoreElements()){
			System.out.println("alias: " + a.nextElement());
		}
	}

	public CertStore getCertStore() {
		return certStore;
	}
}
