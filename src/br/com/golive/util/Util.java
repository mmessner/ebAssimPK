package br.com.golive.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.sap.server.InvalidRequestException;
import br.com.golive.token.TokenReader;

import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.ParameterList;
import com.sap.mw.jco.JCO.Structure;
import com.sap.mw.jco.JCO.Table;

public class Util {
	
	private static Logger log = LogUtil.getLogInstance(Util.class.getName());
	
	private static final int blockSize = 0x4b000;
	
	public static final String[] CHAR_REMOVER = { ".", "-", " ", ":", ")", "(",
			"_" };

	private static final Locale ptBr = new Locale("pt", "BR");

	public static BigInteger getBigInteger(Table table, String itemName) {
		try {
			return (BigInteger.valueOf(Long
					.parseLong(table.getString(itemName))));
		} catch (Exception ex) {
			return (BigInteger.valueOf(0));
		}
	}

	public static Integer getInteger(Table table, String itemName) {
		try {
			return (Integer.valueOf(table.getString(itemName)));
		} catch (Exception ex) {
			return (Integer.valueOf(0));
		}
	}

	public static Integer getInteger(Structure struct, String itemName) {
		try {
			return (Integer.valueOf(struct.getString(itemName)));
		} catch (Exception ex) {
			return (Integer.valueOf(0));
		}
	}

	public static BigInteger getBigIntegerNull(Table table, String itemName) {
		try {
			if (getBigInteger(table, itemName).intValue() > 0)
				return (getBigInteger(table, itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static Integer getIntegerNull(Table table, String itemName) {
		try {
			if (getInteger(table, itemName).intValue() > 0)
				return (getInteger(table, itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static Integer getIntegerNull(Structure struct, String itemName) {
		try {
			if (getBigInteger(struct, itemName).intValue() > 0)
				return (getInteger(struct, itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static BigInteger getBigInteger(Structure struct, String itemName) {
		try {
			return (BigInteger.valueOf(Long.parseLong(struct
					.getString(itemName))));
		} catch (Exception ex) {
			return (BigInteger.valueOf(0));
		}
	}

	public static BigInteger getBigIntegerNull(Structure struct, String itemName) {
		try {
			if (getBigInteger(struct, itemName).intValue() > 0)
				return (getBigInteger(struct, itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static BigDecimal getBigDecimal(String itemName) {
		try {
			return (BigDecimal.valueOf(Double.parseDouble(getString(itemName))));
		} catch (Exception ex) {
			return (BigDecimal.valueOf(0));
		}
	}

	public static BigDecimal getBigDecimal(Table table, String itemName) {
		try {
			return (BigDecimal.valueOf(Double.parseDouble(table
					.getString(itemName))));
		} catch (Exception ex) {
			return (BigDecimal.valueOf(0));
		}
	}

	public static BigDecimal getBigDecimalNull(Table table, String itemName) {
		try {
			if (getBigDecimal(table, itemName).floatValue() > 0)
				return (getBigDecimal(table, itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static BigDecimal getBigDecimal(Structure struct, String itemName) {
		try {
			return (BigDecimal.valueOf(Double.parseDouble(struct
					.getString(itemName))));
		} catch (Exception ex) {
			return (BigDecimal.valueOf(0));
		}
	}

	public static BigDecimal getBigDecimalNull(Structure struct, String itemName) {
		try {
			if (getBigDecimal(struct, itemName).floatValue() > 0)
				return getBigDecimal(struct, itemName);
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static short getShort(Table table, String itemName) {
		try {
			return (Short.parseShort(table.getString(itemName)));
		} catch (Exception ex) {
			return (0);
		}
	}

	public static short getShort(Structure struct, String itemName) {
		try {
			return (Short.parseShort(struct.getString(itemName)));
		} catch (Exception ex) {
			return (0);
		}
	}

	public static Short getShortNull(Structure struct, String itemName) {
		try {
			if (getShort(struct, itemName) > 0)
				return (Short.valueOf(getString(struct, itemName)));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static String getString(String itemName) {
		try {
			return (itemName);
		} catch (Exception ex) {
			return ("");
		}
	}

	public static String getString(Table table, String itemName) {
		try {
			return (table.getString(itemName));
		} catch (Exception ex) {
			return ("");
		}
	}

	public static String getString(ParameterList paramList, String itemName) {
		try {
			return (paramList.getString(itemName));
		} catch (Exception ex) {
			return ("");
		}
	}

	public static String getStringNull(Table table, String itemName) {
		try {
			if (getString(table, itemName).trim().length() > 0)
				return (table.getString(itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static String getString(Structure struct, String itemName) {
		try {
			return (struct.getString(itemName));
		} catch (Exception ex) {
			ex.printStackTrace();
			return ("");
		}
	}

	public static String getStringNull(Structure struct, String itemName) {
		try {
			if (getString(struct, itemName).trim().length() > 0)
				return (getString(struct, itemName));
			else
				return (null);
		} catch (Exception ex) {
			return (null);
		}
	}

	public static String getStringLog(Structure struct, String itemName) {
		try {
			return (itemName + ": " + struct.getString(itemName) + ";");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ("");
		}
	}

	public static String getStringLog(Table table, String itemName) {
		try {
			return (itemName + ": " + table.getString(itemName) + ";");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ("");
		}
	}

	public static String getStringDefault(Structure struct, String itemName,
			String valorDefault) {
		if (getStringNull(struct, itemName) != null)
			return (getStringNull(struct, itemName));
		else
			return (valorDefault);
	}

	public static String getStringDefault(Table table, String itemName,
			String valorDefault) {
		if (getStringNull(table, itemName) != null)
			return (getStringNull(table, itemName));
		else
			return (valorDefault);
	}

	public static Date getDate(String itemName) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		try {
			String strData = null;
			String auxData = getString(itemName).replace("-", "");

			strData = auxData.substring(6, 8) + "/" + auxData.substring(4, 6)
					+ "/" + auxData.substring(0, 4);

			return (df.parse(strData));
		} catch (Exception ex) {
			return null;
		}
	}

	public static Date getSDate(String itemName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(itemName);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getSDate(Table table, String itemName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(itemName);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getDate(Structure struct, String itemName) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		try {
			String strData = null;
			String auxData = struct.getString(itemName).replace("-", "");

			strData = auxData.substring(6, 8) + "/" + auxData.substring(4, 6)
					+ "/" + auxData.substring(0, 4);

			return (df.parse(strData));
		} catch (Exception ex) {
			return null;
		}
	}

	public static Date getDate(Table table, String itemName) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		try {
			String strData = null;
			String auxData = table.getString(itemName).replace("-", "");

			strData = auxData.substring(6, 8) + "/" + auxData.substring(4, 6)
					+ "/" + auxData.substring(0, 4);

			return (df.parse(strData));
		} catch (Exception ex) {
			return null;
		}
	}

	public static String completaString(String strOriginal, char _char,
			int size, boolean esquerda) {
		if (strOriginal != null) {
			while (strOriginal.length() < size) {
				if (esquerda)
					strOriginal = _char + strOriginal;
				else
					strOriginal = strOriginal + _char;
			}
		}

		return (strOriginal);
	}

	public static String limpaString(String strOriginal, String[] charRemover) {
		if ((strOriginal != null)
				&& ((charRemover != null) && (charRemover.length > 0))) {
			for (int i = 0; i < charRemover.length; i++) {
				strOriginal = strOriginal.replace(charRemover[i], "");
			}
		}

		return (strOriginal);
	}

	public static String getLogKey(JCO.Structure structNota) {
		String nrCnpj = Util.getStringDefault(structNota, "C_CNPJ", "");
		String docnum = Util.getStringDefault(structNota, "DOCNUM", "");
		return nrCnpj.concat(docnum);
	}

	public static String getDataLog() {
		SimpleDateFormat df = new SimpleDateFormat("HHmmssyyyyMMdd");

		return (df.format(new Date(System.currentTimeMillis())));
	}

	public static Locale getLocale() {
		return ptBr;
	}
	
	/**
	 * Copy data from InputStream to byte[]
	 * 
	 * @param is
	 * @param out
	 * @throws IOException 
	 * @throws IOException
	 */
	public static byte[] inToByte(InputStream in) throws IOException{
		ByteArrayOutputStream os = null;
		try{
			ByteBuffer bBuff = ByteBuffer.allocate(blockSize);
			bBuff.clear();
			byte[] buff = new byte[blockSize];
			
			os = new ByteArrayOutputStream();
			
			long total = 0L;
			int b;
			
			while((b = in.read(buff)) != -1){
				os.write(buff, 0, b);
				bBuff.clear();
				total += b;
			}
			buff=null;
			bBuff = null;
		}catch(IOException e){
			throw new IOException(e);
		}finally{
			try { in.close(); os.close(); } catch (Exception e) {}	
		}
		
		return os.toByteArray();
	}
	
	public static void byteToOut(byte[] in, OutputStream out) throws IOException{
		
		ByteArrayInputStream bin = null;
		try{
			ByteBuffer bBuff = ByteBuffer.allocate(blockSize);
			bBuff.clear();
			byte[] buff = new byte[blockSize];
			
			bin = new ByteArrayInputStream(in);
			
			long total = 0L;
			int b;
			
			while((b = bin.read(buff)) != -1){
				out.write(buff, 0, b);
				bBuff.clear();
				total += b;
			}
			buff=null;
			bBuff = null;
		}catch(IOException e){
			throw new IOException(e);
		}finally{
			try { bin.close(); out.close(); } catch (Exception e) {}	
		}
	}	
	
	public static void byteToOut(byte[] in, OutputStream out, boolean noError) throws IOException{
		try{
			byteToOut(in, out);
		}catch(IOException e){
			if(!noError) throw new IOException(e);
		}
	}
	
	/**
	 * Copy data from InputStream to OutputStream
	 * 
	 * @param is
	 * @param out
	 * @throws IOException
	 */
	public static void inToOut(InputStream in, OutputStream out) throws IOException{
		
		ByteBuffer bBuff = ByteBuffer.allocate(blockSize);
		bBuff.clear();
		byte[] buff = new byte[blockSize];
	
		long total = 0L;
		int b;

		while((b = in.read(buff)) != -1){
			out.write(buff, 0, b);
			bBuff.clear();
			total += b;
		}
		buff=null;
		bBuff = null;
		in.close();	
	}
	
	/**
	 * Copy data from InputStream to Table, each Line contain n characters
	 * 
	 * @param is
	 * @param out
	 * @throws IOException
	 */
	public static void inToTable(InputStream in, long len) throws IOException{
		String method = "inToTable";
		log.debug("Inicio do metodo " + method);
		
		ByteBuffer bBuff = ByteBuffer.allocate(blockSize);
		bBuff.clear();
		byte[] buff = new byte[blockSize];
	
		long total = 0L;
		int b;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		while((b = in.read(buff)) != -1){
			out.write(buff, 0, b);
			bBuff.clear();
			total += b;
		}
		buff=null;
		bBuff = null;
		in.close();	

		log.debug("Fim do metodo " + method);	
	}	
	
	public static void printTable(JCO.Table tbl){
		log.debug("Inicio impressão dados da tabela");
		int len = 0;
		if(tbl!=null){
			log.debug("Nome da tabela: " + tbl.getName());
			log.debug("Numero de linhas: " + tbl.getNumRows());
			log.debug("Conteúdo da tabela:");
			try{
				String linha;
				for(int i=0;i<tbl.getNumRows();i++){
					linha = Util.getString(tbl, "LINE");
					log.debug("LINHA " + i + " (Len " + linha.length() +") = " + linha); 
					len+=linha.length(); 
					tbl.nextRow();
				}
				log.debug("Soma do comprimento das linhas (Len " + len + ")");
			}catch(Exception e){}
		}else{
			log.debug("Tabela nula!");
		}
		log.debug("Fim impressão dados da tabela");
	}
	
	public static String formatDate(Date date){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getDefault());	
			return sdf.format(date);
		}catch(Exception e){
			return "";
		}
	}
}

