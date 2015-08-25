package br.com.golive.token;

import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import tokenreader.TokenReaderApp;
import br.com.golive.config.AppConfigUtil;
import br.com.golive.config.SAPConfigUtil;
import br.com.golive.log.LogUtil;
import br.com.golive.sap.rfc.SendDataRFC;
import br.com.golive.sap.rfc.Z22F_CNAB;
import br.com.golive.sap.rfc.ZARQUIVO;
import br.com.golive.sap.server.Server;
import br.com.golive.sap.server.ServerExceptionListener;
import br.com.golive.sign.thread.StopServerThead;
import br.com.golive.util.Util;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Attributes;

public class TokenReader {
	private static Logger log = LogUtil.getLogInstance(TokenReader.class.getName());
	
	private static IRepository rep;
	private static Properties config;
	private static Properties sapConfig = null;
	
	static{
		startup();
	}
	
	public static void main(String[] args) {
		log.info("Golive Token Reader Iniciado...");
		setLocaleDefault();
		startServer();
		startMonitor();
		log.info("Golive Token Reader Inicializado com sucesso...");
	}
	
	private static void setLocaleDefault(){
		Locale.setDefault(Util.getLocale());
	}
	
	private static void startServer(){
		String method = "startServer()";
		log.debug("Inicio metodo " + method);
		
		JCO.addServerErrorListener(new ServerExceptionListener());
		JCO.addServerExceptionListener(new ServerExceptionListener());
		
		// start server
		Server server = new Server(rep,config,sapConfig);
		server.start();
		
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Attributes at = server.getAttributes();
		
//		System.out.println(at.getKernelRelease());
		
//		System.out.println(server.getConnectionHandle());
		
//		String[][] pi = server.getPropertyInfo();
//		
//		System.out.println(pi.length);
//		System.out.println(pi[4][1]);
//		
//		System.out.println();
		
		log.debug("Fim metodo " + method);
	}

	private static void startup(){
		config = loadAppConfig();
		sapConfig = loadSAPConfig();
		createRespository();
	}
	
	private static Properties loadAppConfig(){
		Properties config = null;
		try {
			config = AppConfigUtil.getConfig();
		} catch (Exception e) {
			exit(e);
		}
		return config;
	}	
	
	private static Properties loadSAPConfig(){
		Properties sapConfig = null;
		try {
			String env = config.getProperty("sap.environment", "dev");
			sapConfig = SAPConfigUtil.getConfig(env);
		} catch (Exception e) {
			exit(e);
		}
		return sapConfig;
	}	
	
	private static Properties loadConfig2(){
		// log configurations		
		Properties config = null;
		try {
			String env = config.getProperty("sap.environment", "dev");
			config =  SAPConfigUtil.getConfig(env);
		} catch (Exception e) {
			exit(e);
		}
		
		return config;
	}
	
	private static void exit(Exception e){
		log.error(e);
		log.error("Aplicação finalizada após erro!");
		System.exit(1);
	}
	
	private static void createRespository(){
		// cria repositorio
		rep = JCO.createRepository("rep", "pool");
		
		rep.addStructureDefinitionToCache(new ZARQUIVO("T_ARQUIVO").createStructure());
//		rep.addStructureDefinitionToCache(new Z22F_CNAB("T_CNAB").createStructure());
		
		String fName = config.getProperty("sap.rfc.envio.cnab");
		rep.addFunctionInterfaceToCache(new SendDataRFC(fName).createFunction());
	}
	
	private static void startMonitor(){
		TokenReaderApp.start();
	}
	
	public static void stopServer(){
		if(AppConfigUtil.getBooleanValueDefault("token.exit.after.process", Boolean.FALSE)){
			Thread t = new Thread(new StopServerThead());
			t.start();	
		}
	}
}
