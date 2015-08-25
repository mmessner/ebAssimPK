package br.com.golive.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;

public class ConfigUtil {
	private static Logger log = LogUtil.getLogInstance(ConfigUtil.class.getName());
	
	protected static Properties loadConfig(String fileName) throws Exception{
		log.debug("Lendo arquivo de configuração: " + fileName);
		
		Properties config = new Properties();
		InputStream inFile = null;
		try {
			inFile = new FileInputStream(new File(fileName).getAbsoluteFile());
			config.load(inFile);
			return (config);
		} catch (Exception e) {
			log.error(e);
			throw new Exception("Erro obtendo arquivo de propriedades: " + fileName);
		} finally{
			if(inFile!=null)
				inFile.close();
		}
	}
	/**
	 * Write configuration file.
	 * 
	 * @param config
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	protected static boolean store(Properties config, String fileName) throws Exception{
		log.debug("Gravando arquivo de configuração: " + fileName);
		
		OutputStream outFile = null;
		try {
			outFile = new FileOutputStream(new File(fileName).getAbsoluteFile());
			config.store(outFile,"");
			
			log.debug("Arquivo de configuração: " + fileName + ". Gravado com exito!");
			return true;
		} catch (Exception e) {
			log.error(e);
			throw new Exception("Erro gravando arquivo de propriedades: " + fileName);
		} finally{ if(outFile!=null) try { outFile.close(); } catch (IOException e) { }
		}
	}	
}
