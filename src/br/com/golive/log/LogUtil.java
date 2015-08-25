package br.com.golive.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {
	
	private static final String LOG_PROPERTIES_FILE = "config/log4j.properties";
	
	private LogUtil(){};
	
	public static Logger getLogInstance(String clazz){
		// Create log
		Logger logger = Logger.getLogger(LogUtil.class.getName());
		
		// Load configuration file
		PropertyConfigurator.configure(LOG_PROPERTIES_FILE);
		
		return logger;
	}		
}