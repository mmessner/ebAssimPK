package br.com.golive.sap.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.util.Util;

import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Table;

public class ReceivedFile {
	
	private static Logger log = LogUtil.getLogInstance(ReceivedFile.class.getName());

	private String fileID;

	private String userID;
	
	private String ecpf;

	private JCO.Table fileContent;

	private InputStream inStream;

	private ByteArrayOutputStream signedStream;

	private byte[] zipData;

	private byte[] b64Data;

	public ReceivedFile(String fileID, String userID, Table fileContent) {
		this.fileID = fileID;
		this.userID = userID;
		this.fileContent = fileContent;
	}
	
	public OutputStream createSignedStream() {
		log.warn("Inicio signedStream ");
		signedStream = new ByteArrayOutputStream();
		log.warn("Inicio signedStream OK");
		return signedStream;
	}
	
	public byte[] getSignedStream(){
		return signedStream.toByteArray();
	}

	public byte[] getZipData() {
		return zipData;
	}

	public void setZipData(byte[] zipData) {
		this.zipData = zipData;
	}
		
	public void setB64Data(byte[] data) {
		b64Data = data;
        String fileName = "file/fileid_" + fileID + "_userid_" + userID + "_b64_01.txt";
        writeB64File(fileName);
	}
	
	public InputStream inStream() throws InvalidRequestException {
		String method = "inStream";
		log.debug("Inicio metodo " + method);

		StringBuffer buf = new StringBuffer();
		try {
			for (int i = 0; i < fileContent.getNumRows(); i++) {
				if ((fileContent.getString("LINE") != null)
						&& (fileContent.getString("LINE").length() > 0)) {
					buf.append(fileContent.getString("LINE"));
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new InvalidRequestException(
					"Erro ao processar tabela contendo arquivo recebido.");
		}

		log.debug("Tamanho final do Buffer: " + buf.toString().length());
		log.debug("Fim metodo " + method);

		return new ByteArrayInputStream(buf.toString().getBytes());
	}
	
	public boolean fillTable(JCO.Table tbl){
		tbl.clear();
		
		int maxLen = 30000;
		
		// TODO - Trocar para suportar arquivos grandes
		String s = new String(b64Data);
		
		if(s.length()<=maxLen){
			tbl.appendRow();
			tbl.setValue(s, "LINE");
			return true;
		}
		
		String tmp;
		int start = 0;
		int end = maxLen;
		boolean finished = false;
		
		while(!finished){	
			tmp = s.substring(start,end);
			tbl.appendRow();
			tbl.setValue(tmp, "LINE");
			start+=maxLen;
			end+=maxLen;
			
			if(end>s.length()){
				end = s.length();
			}
			
			if(start>=s.length())
				finished=true;
		}
		return true;
	}

	private boolean writeB64File(String fileName){
		if(b64Data==null) return false;
		
		File f = new File(fileName);
		if(f.exists()){
			boolean finished = false;
			int i = 1;
			while(!finished){
				String strReplace = fileName.substring(fileName.indexOf(".")-2,fileName.indexOf("."));
				fileName = fileName.replace(strReplace, Util.completaString(new Integer(i).toString(),'0', 2, true));
				i++;
				f = new File(fileName);
				if(!f.exists()) finished = true;
			}
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(fileName));
			fos.write(b64Data);
			return true;
		} catch (Exception e) {
			log.error("Erro ao gravar arquivo convertido para Base 64. " + e);
			return false;
		} finally{
			if(fos!=null) try { fos.close(); } catch (IOException e) { }
		}
	}
	
	public static void main(String[] args) {
		int maxLen = 3;
		
		// TODO - Trocar para suportar arquivos grandes
		String s = new String("abc   d");
		
		if(s.length()<=maxLen){
			System.out.println("Nova linha: " + s);
			System.exit(1);
		}
		
		String tmp;
		int start = 0;
		int end = maxLen;
		boolean finished = false;
		
		while(!finished){	
			tmp = s.substring(start,end);
			System.out.println("Nova linha: " + tmp);
			start+=maxLen;
			end+=maxLen;
			
			if(end>s.length()){
				end = s.length();
			}
			
			if(start>=s.length())
				finished=true;
		}
	}

	public String getFileID() {
		return fileID;
	}

	public String getUserID() {
		return userID;
	}
}