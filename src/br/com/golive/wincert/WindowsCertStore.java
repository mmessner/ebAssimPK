package br.com.golive.wincert;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class WindowsCertStore {
	
	public void encripty(String input, String alias){
        try {   	
			KeyStore ks = KeyStore.getInstance("Windows-MY");
			// Note: When a security manager is installed, 
			// the following call requires SecurityPermission 
			// "authProvider.SunMSCAPI".
			ks.load(null, null); 

			byte[] data = input.getBytes();

			PrivateKey privKey = (PrivateKey) ks.getKey(alias, null);
			Certificate cert = ks.getCertificate(alias);

			Provider p = ks.getProvider();
			Signature sig = Signature.getInstance("SHA1withRSA", p);
			sig.initSign(privKey);
			sig.update(data);
			byte[] signature = sig.sign();
			System.out.println("\tGenerated signature...");
			sig.initVerify(cert);
			sig.update(data);
			if (sig.verify(signature)) {
			   System.out.println("\tSignature verified!");
			}
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("fim do teste!");
	}

	public void readMSKeystore(){
		System.out.println("Lendo certificados windows:");
		
		System.setProperty("javax.net.ssl.keyStoreProvider","SunMSCAPI");
		System.setProperty("javax.net.ssl.keyStoreType","Windows-MY");
		System.setProperty("javax.net.ssl.trustStoreProvider","SunMSCAPI");
		System.setProperty("javax.net.ssl.trustStoreType","Windows-ROOT");

		KeyStore ks = null;
		try {
			// carrega key store
			ks = KeyStore.getInstance("Windows-MY","SunMSCAPI");
			ks.load(null);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ks==null){
			System.out.println("keystore null");
			System.exit(0);
		}
			
		// le entradas
		Enumeration<String> as1;
		int i=1;
		try {
			as1 = ks.aliases();
			while(as1.hasMoreElements()){
				String alias = as1.nextElement();
				System.out.println(i++ + " - " + alias);
				try {
					PrivateKey privKey = (PrivateKey) ks.getKey(alias, null);
					System.out.println(privKey);
				} catch (UnrecoverableKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("fim");
	}
	
	public void teste(){
		try {
			KeyStore ks = KeyStore.getInstance("Windows-MY");
			ks.load(null, null);
			
			//Security.getProvider("SunMSCAPI").setProperty("Signature.NONEwithRSA", "sun.mscapi.RSASignature$SHA1");
			
			String aliass;
			Enumeration<String> aliasses = ks.aliases();
			
			while(aliasses.hasMoreElements()){
				aliass = aliasses.nextElement();
				System.out.println(aliass);
			}
			
			System.out.println("fim da leitura da ws keystore");
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void main(String[] args) {
		WindowsCertStore wcs = new WindowsCertStore();
		
		wcs.readProviders();
		
		wcs.readMSKeystore();
		
		// encripty
//		String data = "<xml>";
//		String certAlias = "Klabin S.A.";
//		wcs.encripty(data,certAlias);		
	}
	
	private void readProviders(){
		Provider[] prov = Security.getProviders();
		
		System.out.println("Lendo Providers...");
		for(int i=0;i<prov.length;i++){
			System.out.println(i + ") Name: " + prov[i].getName());
		}
	}
}
