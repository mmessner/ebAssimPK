package br.com.golive.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sap.mw.jco.JCO;

import br.com.golive.log.LogUtil;

public class ServerErrorUtil {
	private static Logger log = LogUtil.getLogInstance(ServerErrorUtil.class.getName());
	
	private static Map<String,String> servErrors = new ConcurrentHashMap();
	
	public static void storeError(JCO.Server server, String error){
		log.debug("Inicio storeError()");
		
		String key = createKey(server);
		
		if(!servErrors.containsKey(key))
			servErrors.put(key, error);
		else{
			 String err = (String) servErrors.get(key);
			 err = error;
		}
		
		printServErrors();
		
		log.debug("Fim storeError()");
	}
	
	private static void printServErrors(){
		log.debug("#########################");
		log.debug("NUMERO DE ERROS: " + servErrors.size());
		log.debug("Log server errors");
		
		if(servErrors.size()>0){
			for(String key: servErrors.keySet()){
				log.debug("Key: " + key);
				log.debug("Value: " + servErrors.get(key));
			}
		}
		
		log.debug("#########################");
	}
	
	private static String createKey(JCO.Server server){
		if(server!=null)
			return server.getGWHost()+"-"+server.getGWServ()+"-"+server.getProgID();
		else
			return "";
	}
	
	public static String getLastServerError(JCO.Server server){
		String key = createKey(server);
		
		if(key!=null)
			return servErrors.get(key);
		else
			return null;
	}
	
}
