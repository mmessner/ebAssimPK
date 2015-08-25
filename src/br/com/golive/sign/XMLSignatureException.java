package br.com.golive.sign;

public class XMLSignatureException extends Exception{
	public XMLSignatureException() {
		super();
	}

	public XMLSignatureException(String msg) {
		super(msg);
	}

	public XMLSignatureException(Exception cause) {
		super(cause.getMessage(),cause);
	}

	public XMLSignatureException(String msg, Exception cause) {
		super(msg, cause);
	}
}
