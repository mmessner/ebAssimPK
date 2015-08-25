package br.com.golive.sign.ks.core;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.DefaultKeyStore;
import br.com.golive.sign.ks.WSKeyStore;

public class KeystoreFactory {
	
	private static Logger log = LogUtil.getLogInstance(KeystoreFactory.class.getName());	
	
	private KeystoreFactory(){}
	
	public static Keystore getKeystoreIstance(Keystores type) throws KeystoreException {
		log.debug("Inicio metodo getKeystoreIstance");
		
		if(type==null){
			throw new KeystoreException("It's necessary inform keystore type! Ex: MSWINDOWS or DEFAULT ");
		}

		switch(type){
			case MSWINDOWS:
				return new WSKeyStore();
			default:
				return new DefaultKeyStore();
		}
	}
}
