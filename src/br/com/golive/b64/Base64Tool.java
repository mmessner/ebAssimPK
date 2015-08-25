/*
 * Created on 30/09/2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.golive.b64;

import java.io.IOException;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Carlos Cambra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Base64Tool {

	/**
	 * Encode base 64
	 * 
	 * @param input
	 * @return
	 * @throws Base64Exception
	 */
	public String encode(byte[] input) throws Base64Exception{
		String enc = "";
	  
		if(input==null)throw new Base64Exception("Erro during encoding. Invalid input data: " + input);
	  
		try {
			BASE64Encoder b64 = new BASE64Encoder();
			enc = b64.encodeBuffer(input);
		} catch (RuntimeException e) {
			throw new Base64Exception("Erro during encoding."  + e.getMessage());
		}
	  
	  return enc;
	}
	
	/**
	 * Decode base 64
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public byte[] decode(String input) throws Base64Exception{
		byte[] dec;
	  
	  	if(input==null||input.length()==0)throw new Base64Exception("Erro during dencoding. Invalid input data: " + input);
	  
		try {
			BASE64Decoder b64 = new BASE64Decoder();
			dec = b64.decodeBuffer(input);
		} catch (IOException e) {
			throw new Base64Exception("Erro during dencoding. " + e.getMessage());
		}
	  
	  return dec;
	}
	
	public static void main(String[] args) {
		Base64Tool tool = new Base64Tool();
		OutputStream output = System.out;
		
		byte[] input = "teste simples".getBytes();
		
		try {
//			// inicial string
//			System.out.print("STRING ORIGINAL: ");
//			output.write(input);
//			// string encoded
//			System.out.print("\n................................................");
//			System.out.println("\nENCODE base 64: ");
//			System.out.println(tool.encode(input));
//			// string decoded	
//			System.out.print("................................................");
//			System.out.println("\nDECODE base 64: ");
//			output.write(tool.decode(tool.encode(input)));
			
			output.write(tool.decode("UEsDBBQACAAIAEVjezkAAAAAAAAAAAAAAAAMAAAAdW50aXRsZWQueG1sfVNNT8MwDL0j8R/KqadSul04ZJ0KCA2JL7HyA0ySlUhtXZJu2s/HbZOybhlSDs57efazrbDlviqDndRGYb0Ik+ubMJA1R6HqYhF+5o/RbbhMLy9YJWsDhazeoWiRgCBgHL4kh/J7uBKghKxbtVEcBOq3RmrggGm2ztYPdFjs533i+1LRTaazZJ7M5kdKR/qEd0Dm01PNgPsUL7Yxl5W0p/LjR/8lGkqdSXDeR64azPTPVu06/0fyQ9KKBbSwQg0fkmCjukk66x7qRGQaNC2kh48tFPTLjafbZRx14zdufXnbnnBWDJM+CNigrqB19rvDYoeNjzjS3LcCc7knmF1F0fPT6yoLkiiiolPWFoqnldiGuoHDjEIV2Iv7YCSENFz3U6PpjPFIo1a0SOJsMBIGy+2gcpEz8le5qzYMksXTL/ULUEsHCKcRG8ZBAQAAigMAAFBLAQIUABQACAAIAEVjezmnERvGQQEAAIoDAAAMAAAAAAAAAAAAAAAAAAAAAAB1bnRpdGxlZC54bWxQSwUGAAAAAAEAAQA6AAAAewEAAAAA"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Base64Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
