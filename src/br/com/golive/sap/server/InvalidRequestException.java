package br.com.golive.sap.server;

public class InvalidRequestException extends Exception{
	public InvalidRequestException() {
		super();
	}

	public InvalidRequestException(String msg) {
		super(msg);
	}

	public InvalidRequestException(Exception cause) {
		super(cause.getMessage(),cause);
	}

	public InvalidRequestException(String msg, Exception cause) {
		super(msg, cause);
	}
}
