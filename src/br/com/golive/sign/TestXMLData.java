package br.com.golive.sign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.core.Keystores;


public class TestXMLData {
	// log
	private static Logger log = LogUtil.getLogInstance(TestXMLData.class.getName());
	
	private static XMLData xmlData;
	private static String cert;
	private static InputStream is;
	private static OutputStream os;
	private static String PATH = "D:\\projetos\\eCPF\\xml_signed_klabin\\";
	private static String inXML;
	private static String outXML;
	
	public static void main(String[] args) throws Exception{

		// Testes obtendo certificado de keystore de testes do projeto
		testeXMLData_Sign_KS_Default_CERT_brasiltelecom();
		testeXMLData_Verify_KS_Default_CERT_brasiltelecom();
		testeXMLData_Verify_KS_Default_CERT_brasiltelecon2();
		
        // Testes obtendo certificado do windows		
//		testeXMLData_Sign_KS_MSWindows_CERT_brasiltelecom();
//		testeXMLData_Verify_KS_MSWindows_CERT_brasiltelecom();		
		
//		testeXMLData_Sign();
//		testeXMLData_Verify();
		
//		testeXMLData_Sign_DifinedKeyStore_KS_MSWindows();
//		testeXMLData_Verify_DifinedKeyStore_MSWindows();
		


//		testeXMLData2_Verificacao_Assinatura();
	}
	
	// 1
	private static void testeXMLData_Sign() throws Exception{
		InputStream is = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed.xml"));
		OutputStream os = new FileOutputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml"));
		
		String alias = "Brasil Telecon";
		XMLData xmlData;
		
		xmlData = new XMLData(is);
		xmlData.sign(alias, os);
	}
	
	// 2
	private static void testeXMLData_Verify() throws Exception{
		InputStream in = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml"));
		
		String alias = "Brasil Telecon";
		XMLData xmlData;
		
		xmlData = new XMLData(in);
		
		if(xmlData.verify(in, alias)){
			System.out.println("A assinatura é valida!");
		}else{
			System.out.println("A assinatura NÃO é valida!");
		}
	}

	// 3
	private static void testeXMLData_Sign_KS_MSWindows() throws Exception{
		InputStream is = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed.xml"));
		OutputStream os = new FileOutputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml"));
		
		String alias = "Brasil Telecon";
		XMLData xmlData;
		
		xmlData = new XMLData(is);
		xmlData.sign(Keystores.MSWINDOWS,alias, os);
	}

	// 4
	private static void testeXMLData_Verify_KS_MSWindows() throws Exception{
		InputStream in = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml"));
		
		String alias = "Brasil Telecon";
		XMLData xmlData;
		
		xmlData = new XMLData(in);
		
		if(xmlData.verify(Keystores.MSWINDOWS, alias,in)){
			System.out.println("A assinatura é valida!");
		}else{
			System.out.println("A assinatura NÃO é valida!");
		}
	}	
	
	// 5
	private static void testeXMLData_Sign_KS_Default_CERT_brasiltelecom(){
		log.info("Inicio teste assinatura digital!");
		
		// parameters
		cert = "brasiltelecom";
		
		inXML = "nfe_klabin_sign_input.xml";
		outXML = "xml_original_to_be_signed_SIGNED.xml";
		
		log.info("Arquivo de entrada " + inXML);
		log.info("Arquivo de saida " + outXML);
		
		// read xmls
		try {
			is = new FileInputStream(new File(PATH+inXML));
			os = new FileOutputStream(new File(PATH+outXML));
		} catch (FileNotFoundException e) {
			log.error("Erro ao abrir arquivos!");
		}
		
		try {
			// Sign
			xmlData = new XMLData(is);
			xmlData.sign(Keystores.DEFAULT,cert, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		}
		
		// close 
		if(os!=null) try {os.close();} catch (IOException e) {}
		
		log.info("Fim teste assinatura digital!");
	}		
	
	// 6
	private static boolean testeXMLData_Verify_KS_Default_CERT_brasiltelecom(){
		String method = "testeXMLData_Verify_KS_Default_CERT_brasiltelecom()";
		log.info("Inicio do metodo " + method);
		
		inXML = "D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml";
		String alias = "brasiltelecom";
		
		InputStream in = null;
		try {
			in = new FileInputStream(new File(inXML));
		} catch (FileNotFoundException e) {
			log.error("Erro ao abrir arquivo " + inXML);
			return false;
		}
		
		xmlData = new XMLData(in);
		
		try {
			xmlData.verify(Keystores.DEFAULT, alias,in);
		} catch (XMLSignatureException e) {
			log.error(e);
			return false;
		}
		
		if(true){
			log.info("A assinatura é valida!");
		}else{
			log.info("A assinatura NÃO é valida!");
		}
		
		log.info("Fim do metodo " + method);
		return true;
	}
	
	private static void testeXMLData_Sign_KS_MSWindows_CERT_brasiltelecom(){
		String method = "testeXMLData_Sign_KS_MSWindows_CERT_brasiltelecom";
		log.info("Inicio teste " + method);
		
		// parameters
		cert = "brasiltelecom";
		
		inXML = "nfe_klabin_sign_input.xml";
		outXML = "xml_original_to_be_signed_SIGNED.xml";
		
		log.info("Arquivo de entrada " + inXML);
		log.info("Arquivo de saida " + outXML);
		
		// read xmls
		try {
			is = new FileInputStream(new File(PATH+inXML));
			os = new FileOutputStream(new File(PATH+outXML));
		} catch (FileNotFoundException e) {
			log.error("Erro ao abrir arquivos!");
		}
		
		try {
			// Sign
			xmlData = new XMLData(is);
			xmlData.sign(Keystores.MSWINDOWS,cert, os);
		} catch (XMLSignatureException e) {
			log.error(e);
		}
		
		// close 
		if(os!=null) try {os.close();} catch (IOException e) {}
		
		log.info("Fim teste " + method);
	}		
	
	// 6
	private static boolean testeXMLData_Verify_KS_MSWindows_CERT_brasiltelecom(){
		String method = "testeXMLData_Verify_KS_MSWindows_CERT_brasiltelecom()";
		log.info("Inicio do teste " + method);
		
		inXML = "D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml";
		String alias = "brasiltelecom";
		
		log.info("Lendo XML: " + inXML);
		log.info("Certificado: " + alias);
		
		InputStream in = null;
		try {
			in = new FileInputStream(new File(inXML));
		} catch (FileNotFoundException e) {
			log.error("Erro ao abrir arquivo " + inXML);
			return false;
		}
		
		xmlData = new XMLData(in);
		
		try {
			xmlData.verify(Keystores.MSWINDOWS, alias,in);
		} catch (XMLSignatureException e) {
			log.error(e);
			return false;
		}
		
		if(true){
			log.info("A assinatura é valida!");
		}else{
			log.info("A assinatura NÃO é valida!");
		}
		
		if(in!=null) try {in.close(); } catch (IOException e) {}
		
		log.info("Fim do teste " + method);
		return true;
	}	

	// 7
	private static boolean testeXMLData_Verify_KS_Default_CERT_brasiltelecon2() throws Exception{
		String method = "testeXMLData_Verify_KS_Default_CERT_brasiltelecon2()";
		log.info("Inicio do metodo " + method);
		
		inXML = "D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml";
		String alias = "brasiltelecon2";
		
		InputStream in = null;
		try {
			in = new FileInputStream(new File(inXML));
		} catch (FileNotFoundException e) {
			log.error("Erro ao abrir arquivo " + inXML);
			return false;
		}
		
		xmlData = new XMLData(in);
		boolean isValid;
		try {
			isValid = xmlData.verify(Keystores.DEFAULT, alias,in);
		} catch (XMLSignatureException e) {
			log.error(e);
			return false;
		}
		
		if(isValid){
			log.info("A assinatura é valida!");
		}else{
			log.info("A assinatura NÃO é valida!");
		}
		
		if(in!=null) try {in.close(); } catch (IOException e) {}
		
		log.info("Fim do metodo " + method);
		return true;

	}	
	
	// 8
	private static void testeXMLData2_Verificacao_Assinatura() throws Exception{
		InputStream is = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed.xml"));
		OutputStream os = new FileOutputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml"));
		
		String alias = "Brasil Telecon";
		XMLData2 xmlData;
		
		xmlData = new XMLData2(is);
		xmlData.sign(alias, os);
		
		// verificando assinatura
		System.out.println("Iniciando verificação 01");		
		is = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED_A.xml"));
		
		boolean isValid = xmlData.verify(is,alias);
		if(isValid){
			System.out.println("A assinatura é valida!");
		}else{
			System.out.println("A assinatura NÃO é valida!");
		}

		System.out.println("Iniciando verificação 02");
		is = new FileInputStream(new File("D:\\projetos\\eCPF\\xml_signed_klabin\\xml_original_to_be_signed_SIGNED.xml"));
		
		isValid = xmlData.verify(is,alias);
		if(isValid){
			System.out.println("A assinatura é valida!");
		}else{
			System.out.println("A assinatura NÃO é valida!");
		}	
	}
}
