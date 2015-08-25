package br.com.golive.config;

import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.config.ConfigUtil;
import br.com.golive.log.LogUtil;

public class AppConfigUtil extends ConfigUtil{
	private static Logger log = LogUtil.getLogInstance(AppConfigUtil.class.getName());
	
	private static final String CONFIG_FILE = "config/config.properties";
	
	private static Properties config;
	private static boolean reload = false;
	
	public synchronized static Properties getConfig() throws Exception{
		if((config==null) || reload){
			config = loadConfig(CONFIG_FILE);
			reload = false;
			return config;
		}
		else  return (config);
	}

	public static void setReload(boolean reload) {
		AppConfigUtil.reload = reload;
	}
	
	public synchronized static boolean store() throws Exception{
		return store(config, CONFIG_FILE);
	}
	
	public static boolean getBooleanValueDefault(String key, boolean defaultValue){
		try{
			String value = getConfig().getProperty(key);
			return new Boolean(value).booleanValue();
		}catch(Exception e){
			return defaultValue;
		}
	}
}
