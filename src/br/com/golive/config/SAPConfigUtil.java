package br.com.golive.config;

import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;

public class SAPConfigUtil extends ConfigUtil{
	private static Logger log = LogUtil.getLogInstance(SAPConfigUtil.class.getName());
	
	private static final String CONFIG_FILE_DEV = "config/sapdev.properties";
	private static final String CONFIG_FILE_QAS = "config/sapqas.properties";
	private static final String CONFIG_FILE_PRD = "config/sapprd.properties";
	
	private static Properties config;
	private static boolean reload = false;
	
	public synchronized static Properties getConfig(String env) throws Exception{
		String fileName = getFileName(env);
		
		if(fileName==null){
			throw new NullPointerException("Nome do arquivo de configuração SAP não informado! Filename=" + fileName);
		}
		
		if((config==null) || reload){ 
			reload = false;
			return loadConfig(fileName);
		}
		else return (config);
	}
	
	private static String getFileName(String env){
		if(env.equals("qas")){
			return CONFIG_FILE_QAS;
		}else if(env.equals("prd")){
			return CONFIG_FILE_PRD;
		}else{
			return CONFIG_FILE_DEV;
		}
	}

	public static void setReload(boolean reload) {
		SAPConfigUtil.reload = reload;
	}
	
	public synchronized static boolean store(String env, Properties config) throws Exception{
		store(config,getFileName(env));
		return true;
	}
}
