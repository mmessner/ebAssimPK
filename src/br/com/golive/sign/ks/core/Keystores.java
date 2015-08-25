package br.com.golive.sign.ks.core;

/**
 * Tipos de keystore
 * 
 * @author Carlos Cambra
 * 
 */
public enum Keystores {
	MSWINDOWS, DEFAULT;

	public static Keystores getksType(String strType){
		if(strType.equals("MSWINDOWS")){
			return Keystores.MSWINDOWS;
		}else{
			return Keystores.DEFAULT;
		}
	}
}

