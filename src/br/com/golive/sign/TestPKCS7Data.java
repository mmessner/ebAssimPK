package br.com.golive.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;

import org.apache.log4j.Logger;

import br.com.golive.b64.Base64Exception;
import br.com.golive.b64.Base64Tool;
import br.com.golive.gzip.GZIPTool;
import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.core.Keystores;
import br.com.golive.util.Util;

public class TestPKCS7Data {
	private static Logger log = LogUtil.getLogInstance(TestPKCS7Data.class.getName());
	
	private static XMLData xmlData;
	private static String cert;
	private static InputStream is;
	private static OutputStream os;
	private static String PATH = "D:\\projetos\\BRT\\eCPF\\xmlTeste\\";
	private static String TXT = ".txt";
	private static String ZIP = "_zip";
	private static String B64 = "_b64";
	private static String inXML;
	private static String outXML;
	
	private static GZIPTool zip = null;
	
	private static Base64Tool b64 = new Base64Tool();
	
	public static void main(String[] args) {
		TestPKCS7Data test = new TestPKCS7Data();
		
//		test.testeSign_Alias_brasiltelecon();
//		test.testeSign_Alias_golive();
		
//		test.verify_file_with_TWO_signatures();
		
//		test.teste_Sign_twowice();
		
//		test.teste_Sign_twice_together();
		
//		test.teste_Sign_Again();
		
		// teste multiple signers V3
//		test.teste_Sign_Verify_Multiple_times_KS_MSWindows();
		
		// teste multiple sign sign V3, zip, encode B64
		test.teste_Sign_Zip_EncB64_Multiple_times_KS_MSWindows();
		
//		test.test_verify_sign_zip_b64();
	}
	
	public TestPKCS7Data() {
		// TODO Auto-generated constructor stub
	}

	public static void test_verify_sign_zip_b64(){
		log.debug("Inicio teste Verify. (SIGN, ZIP, B64)");
		
		String fileName = "D:\\workspace.europa\\TokenReader\\rfc00232_02916.trc";
		
		// carrega arquivo
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(fin==null){
			log.error("Erro ao carregar arquivo! InputStream nulo!");
			System.exit(1);
		}
		
		// Decode
		byte[] decoded = decode(fin);
		
		// Uncompress
		byte[] uncompressed = uncompress(decoded);
		
		// Verify
		verify(uncompressed);
		
		log.debug("Fim teste Verify. (SIGN, ZIP, B64)");
	}
	
	public static void teste_Sign_Verify_Multiple_times_KS_Default(){
		TestPKCS7Data test = new TestPKCS7Data();
		
		// assinatura 1
		cert = "brasiltelecom";
		inXML = "teste_cnab.txt";
		outXML = "teste_cnab_signed1.txt";
		test.testeSign(Keystores.DEFAULT,cert, inXML, outXML);
		
		// assinatura 2
		cert = "brasiltelecom";
		inXML = "teste_cnab_signed1.txt";
		outXML = "teste_cnab_signed2.txt";
		test.testeSign(Keystores.DEFAULT,cert, inXML, outXML);

		// assinatura 3
		cert = "golive";
		inXML = "teste_cnab_signed2.txt";
		outXML = "teste_cnab_signed3.txt";
		test.testeSign(Keystores.DEFAULT,cert, inXML, outXML);		

		// assinatura 4
		cert = "golive";
		inXML = "teste_cnab_signed3.txt";
		outXML = "teste_cnab_signed4.txt";
		test.testeSign(Keystores.DEFAULT,cert, inXML, outXML);		
		
		
		inXML = "teste_cnab_signed4.txt";
		verify_file(inXML);
	}
	
	public static void teste_Sign_Verify_Multiple_times_KS_MSWindows(){
		TestPKCS7Data test = new TestPKCS7Data();
		
		// assinatura 1
		cert = "BrasilTelecomV3";
		inXML = "teste_cnab.txt";
		outXML = "teste_cnab_signed1.txt";
		test.testeSign(Keystores.MSWINDOWS,cert, inXML, outXML);
		
		// assinatura 2
		cert = "golivev3";
		inXML = outXML;
		outXML = "teste_cnab_signed2.txt";
		test.testeSign(Keystores.MSWINDOWS,cert, inXML, outXML);

		// assinatura 3
		cert = "golivev3";
		inXML = outXML;
		outXML = "teste_cnab_signed3.txt";
		test.testeSign(Keystores.MSWINDOWS,cert, inXML, outXML);		

		// assinatura 4
		cert = "BrasilTelecomV3";
		inXML = outXML;
		outXML = "teste_cnab_signed4.txt";
		test.testeSign(Keystores.MSWINDOWS,cert, inXML, outXML);		
		
		verify_file(outXML);
	}

	/**
	 * Sign and Verify. RSA V3. (SIGNED, ZIP, B64)
	 * 
	 */
	public static void teste_Sign_Zip_EncB64_Multiple_times_KS_MSWindows(){
		log.debug("Inicio teste Sign and Verify. (SIGN, ZIP, B64)");
		
		TestPKCS7Data test = new TestPKCS7Data();
		
		cert = "BrasilTelecomV3";
		inXML = "teste_cnab";
		outXML = "teste_cnab_signed1";
		
		os = new ByteArrayOutputStream();
		
		// sign 1
		test.testeSign(Keystores.MSWINDOWS,cert, inXML+TXT, os);
		
		// write sign 1
		writeToFile(PATH+outXML+TXT, (ByteArrayOutputStream)os);
		
		os = new ByteArrayOutputStream();
		
//		// assinatura 2
		cert = "golivev3";
		inXML = outXML;
		outXML = "teste_cnab_signed2";
		test.testeSign(Keystores.MSWINDOWS,cert, inXML+TXT, os);

		// write sign 2
		writeToFile(PATH+outXML+TXT, (ByteArrayOutputStream)os);		
		
//		// assinatura 3
//		cert = "golivev3";
//		inXML = outXML;
//		outXML = "teste_cnab_signed3.txt";
//		test.testeSign(Keystores.MSWINDOWS,cert, inXML, outXML);		
//
//		// assinatura 4
//		cert = "BrasilTelecomV3";
//		inXML = outXML;
//		outXML = "teste_cnab_signed4.txt";
//		test.testeSign(Keystores.MSWINDOWS,cert, inXML, outXML);		

//		// Compress
//		byte[] ziped = compress((ByteArrayOutputStream)os);
//		
//		if(ziped==null){
//			log.error("Erro, conteudo zipado nulo!");
//			System.exit(1);
//		}
//
//		// Write Compress
//		writeToFile(PATH+outXML+ZIP+TXT,new String(ziped));
//		
//		// Encode
//		String encoded = encode(ziped);

		// Encode
		String encoded = encode(((ByteArrayOutputStream)os).toByteArray());		
		
		if(encoded==null || encoded.trim().length()<1){
			log.error("Erro, conteudo convertido para base 64 vazio ou nulo!");
			System.exit(1);
		}
		
		// Write Encode
		writeToFile(PATH+outXML+B64+TXT,encoded);
		
		// VALIDANDO ARQUIVO
//		String fileName = outXML+B64+TXT;
		
		String fileName = "brt_cnab_signed_zip_b64_ecpf_sap.txt";
		
		// carrega arquivo
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(new File(PATH+fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(fin==null){
			log.error("Erro ao carregar arquivo! InputStream nulo!");
			System.exit(1);
		}
		
		// Decode
		byte[] decoded = decode(fin);
		
		// Uncompress
		byte[] uncompressed = uncompress(decoded);
		
		// Verify
		verify(uncompressed);
		
		log.debug("Fim teste Sign and Verify. (SIGN, ZIP, B64)");
	}	
	
	public static void testeSign(Keystores ksType, String cert, String inXML, String outXML){
		log.info("Inicio teste assinatura digital!");
		
		try {
			log.debug("Lendo arquivo entrada: " + inXML);
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		try {
			log.debug("Lendo arquivo saida: " + outXML);
			os = new FileOutputStream(PATH+outXML);
		} catch (FileNotFoundException e2) {
			log.error("Erro ao abrir arquivo saida, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		try {
			data.sign(ksType, cert, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		} catch (CertificateException e) {
			log.error(e);
		}
		
		log.info("Fim teste assinatura digital!");
	}	
	
	public static void testeSign(Keystores ksType, String cert, String inXML, OutputStream os){
		log.info("Inicio teste assinatura digital!");
		
		try {
			log.debug("Lendo arquivo entrada: " + inXML);
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		try {
			data.sign(ksType, cert, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		} catch (CertificateException e) {
			log.error(e);
		}
		
		log.info("Fim teste assinatura digital!");
	}	
	
	// TEST UTILS
	public static void writeToFile(String fileName, ByteArrayOutputStream out){
		is = new ByteArrayInputStream(out.toByteArray());
		
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(fileName);
			Util.inToOut(is, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {os.close();} catch (IOException e) {}
		}		
	}

	public static void writeToFile(String fileName, String signedOut){
		is = new ByteArrayInputStream(signedOut.getBytes());
		
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(fileName);
			Util.inToOut(is, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {os.close();} catch (IOException e) {}
		}
	}	

	
	// Wrapper to ZipTool Compress
	public static byte[] compress(ByteArrayOutputStream os){
		// Compress
		byte[] ziped = null;
		try {
			zip = new GZIPTool(new ByteArrayInputStream(os.toByteArray()));
			ziped = zip.compress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ziped;
	}
	
	// Wrapper to ZipTool Uncompress
	public static byte[] uncompress(byte[] out){
		// Compress
		byte[] unziped = null;
		try {
			zip = new GZIPTool(new ByteArrayInputStream(out));
			unziped = zip.uncompress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return unziped;
	}	
	
	// Wrapper to B64 Tool Encode
	public static String encode(byte[] ziped){
		// Encode
		String encoded = null;
		try {
			encoded = b64.encode(ziped);
		} catch (Base64Exception e) {
			e.printStackTrace();
		}
		
		return encoded;
	}

	// Wrapper to B64 Tool Decode
	public static byte[] decode(InputStream in){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Util.inToOut(in, out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Encode
		byte[] decoded = null;
		try {
			decoded = b64.decode(new String(out.toByteArray()));
		} catch (Base64Exception e) {
			e.printStackTrace();
		}
		
		return decoded;
	}	
	
	public static void verify(byte[] in){
		log.info("Inicio teste verificação assinatura pkcs7!");
		
		is = new ByteArrayInputStream(in);
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		boolean verified = false;
		try {
			verified = data.verify(Keystores.DEFAULT,null,is);
		} catch (XMLSignatureException e) {
			log.error(e);
		}
		
		if(verified){
			log.debug("A assinatura é valida!");
		}else{
			log.debug("A assinatura NÃO é valida!");
		}
		
		log.info("Fim teste verificação assinatura pkcs7!");
	}	
	
	public static void testeSign_Alias_brasiltelecon(){
		log.info("Inicio teste assinatura digital!");
		
		// parameters
		cert = "brasiltelecom";
		
		inXML = "teste_cnab.txt";
		outXML = "teste_cnab_signed1.txt";
		
		try {
			log.debug("Lendo arquivo entrada: " + inXML);
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		try {
			log.debug("Lendo arquivo saida: " + outXML);
			os = new FileOutputStream(PATH+outXML);
		} catch (FileNotFoundException e2) {
			log.error("Erro ao abrir arquivo saida, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		try {
			data.sign(Keystores.DEFAULT, cert, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		} catch (CertificateException e) {
			log.error(e);
		}
		
		log.info("Fim teste assinatura digital!");
	}
	
	public static void testeSign_Alias_golive(){
		log.info("Inicio teste assinatura digital!");
		
		// parameters
		cert = "golive";
		
		inXML = "teste_cnab_signed1.txt";
		outXML = "teste_cnab_signed2.txt";
		
		// carrega arquivo entrada
		try {
			log.debug("Lendo arquivo entrada: " + inXML);
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		// carrega arquivo saida
		try {
			log.debug("Lendo arquivo saida: " + outXML);
			os = new FileOutputStream(PATH+outXML);
		} catch (FileNotFoundException e2) {
			log.error("Erro ao abrir arquivo saida, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		try {
			data.sign(Keystores.DEFAULT, cert, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		} catch (CertificateException e) {
			log.error(e);
		}
		
		log.info("Fim teste assinatura digital!");
	}	
	
	public static void verify_file(String inXML){
		log.info("Inicio teste verificação assinatura pkcs7!");
		
		try {
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		boolean verified = false;
		try {
			verified = data.verify(Keystores.DEFAULT,null,is);
		} catch (XMLSignatureException e) {
			log.error(e);
		}
		
		if(verified){
			log.debug("A assinatura é valida!");
		}else{
			log.debug("A assinatura NÃO é valida!");
		}
		
		log.info("Fim teste verificação assinatura pkcs7!");
	}	
	
	public static void verify_file_with_TWO_signatures(){
		String method="verify_file_with_TWO_signatures()";
		log.info("Inicio metodo " + method);
		
		inXML = "teste_signed2.txt";
		
		try {
			log.debug("Lendo arquivo: " + PATH+inXML);
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		boolean verified = false;
		try {
			verified = data.verify(Keystores.DEFAULT,null,is);
		} catch (XMLSignatureException e) {
			log.error(e);
		}
		
		if(verified){
			log.debug("A assinatura é valida!");
		}else{
			log.debug("A assinatura NÃO é valida!");
		}
		
		log.info("Fim do metodo " + method);
	}	


	public static void teste_Sign_Again(){
		String method = "teste_Sign_Again()";
		log.info("Inicio do metodo: " + method);
		
		// parameters
		cert = "brasiltelecom";
		String cert2 = "golive";
				
		inXML = "teste_signed.txt";
		outXML = "teste_signed_again.txt";
		
		try {
			log.debug("Lendo arquivo entrada: " + inXML);
			is = new FileInputStream(new File(PATH+inXML));
		} catch (FileNotFoundException e1) {
			log.error("Erro ao abrir arquivo entrada, nome: " + inXML);
		}
		
		try {
			log.debug("Lendo arquivo saida: " + outXML);
			os = new FileOutputStream(PATH+outXML);
		} catch (FileNotFoundException e2) {
			log.error("Erro ao abrir arquivo saida, nome: " + inXML);
		}
		
		// sign data
		PKCS7Data data = new PKCS7Data(is);
		try {
			data.signAgain(Keystores.DEFAULT, cert2, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		} catch (CertificateException e) {
			log.error(e);
		}
		
		log.info("Fim do metodo: " + method);
	}	
	
}
