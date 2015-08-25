/*
 * Created on 30/09/2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.com.golive.gzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.util.Util;

/**
 * @author Carlos Cambra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GZIPTool {
	
	private Logger loc = LogUtil.getLogInstance(GZIPTool.class.getName());

	private byte[] xml;

	/**
	 * Contructor
	 * 
	 * @param in
	 * @throws IOException
	 */
	public GZIPTool(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			
			Util.inToOut(in,out);
			xml = out.toByteArray();
		} catch (IOException e) {
			throw new IOException("Invalid input data.");
		}
	}

	/**
	 * Contructor
	 * 
	 * @param in
	 * @throws IOException
	 */
	public GZIPTool(byte[] in) throws IOException {
		xml = in;
	}

	public byte[] compress() throws ZipException {
		//byte[] newInput = new String(input,"ISO-8859-1").getBytes();

		ByteArrayOutputStream baos;
		GZIPOutputStream gzos;

		try {
			if(xml==null){
				throw new ZipException("Invalid input file!");
			}
			baos = new ByteArrayOutputStream();
			gzos = new GZIPOutputStream(baos);
			gzos.write(xml);
			gzos.flush();
			gzos.finish();
			gzos.close();
			
		} catch (IOException e) {
			loc.error("Error during compression. " + e);
			throw new ZipException("Error during compression. See log.");
		}
		
		return baos.toByteArray();
	}

	public byte[] uncompress() {
		GZIPInputStream in;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		int len;

		InputStream stream = new ByteArrayInputStream(xml);
		try {
			in = new GZIPInputStream(stream);
			while ((len = in.read(buf)) > 0) {
				//sb.append(buf);
				baos.write(buf, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	// teste unitario
	public static void main(String[] args) {
		OutputStream out = System.out;

		byte[] input = "teste de compactação".getBytes();
		byte[] output;

		ByteArrayInputStream in = new ByteArrayInputStream(input);
		GZIPTool tool;
		try {
			tool = new GZIPTool(input);
			System.out.println(".............................................");
			System.out.println("inicial string: ");
			out.write(input);
			
			System.out.println(
				"\n.............................................");
			System.out.println("compressed: ");
			output = tool.compress();
			//out.write(output);
			
			String sout = new String(output);
			System.out.println(sout);
			
			System.out.println(
				"\n.............................................");
			System.out.println("uncompressed: ");
			out.write(tool.uncompress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
