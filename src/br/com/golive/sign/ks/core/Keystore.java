package br.com.golive.sign.ks.core;
       
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public interface Keystore {
	
	public PublicKey getPublicKey();

	public PrivateKey getPrivKey();
	
	public Certificate getCert();
	
	public CertStore getCertStore();
	
	public boolean load(String alias) throws KeyStoreException, CertificateException;
}
