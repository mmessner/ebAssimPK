package br.com.golive.sign.ks.core;

public class KeystoreException extends Exception{
	public KeystoreException() {
		super();
	}

	public KeystoreException(String msg) {
		super(msg);
	}

	public KeystoreException(Exception cause) {
		super(cause.getMessage(),cause);
	}

	public KeystoreException(String msg, Exception cause) {
		super(msg, cause);
	}
}
