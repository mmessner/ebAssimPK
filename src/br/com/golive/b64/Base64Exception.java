/*
 * Created on 02/10/2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.golive.b64;

/**
 * @author Carlos Cambra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Base64Exception extends Exception{
	public Base64Exception(){
	}
	
	public Base64Exception(String message){
		super(message);
	}
	public Base64Exception(String message, Throwable cause){
		super(message,cause);
	}
	public Base64Exception(Throwable cause){
		super(cause);
	}
	
}
