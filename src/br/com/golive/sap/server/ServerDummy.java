package br.com.golive.sap.server;

import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.golive.config.AppConfigUtil;
import br.com.golive.config.SAPConfigUtil;
import br.com.golive.log.LogUtil;
import br.com.golive.sign.thread.DigitalSignThread;

import com.sap.mw.jco.JCO.Function;

public class ServerDummy {
	private static Logger log = LogUtil.getLogInstance(ServerDummy.class.getName());
	
	private Thread thread;
	private Properties conf;
	
	public static void main(String[] args) {
		ServerDummy server = new ServerDummy();
		try {
			server.handleRequest(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ServerDummy() {
		try {
			conf = SAPConfigUtil.getConfig("dev");
		} catch (Exception e) {
			log.error(e);
			System.exit(1);
		}
	}
	
	public void handleRequest(Function function) throws Exception {
		String method = "handleRequest(Function function)";
		log.info("Inicio do metodo " + method);
		
		String xml = getXML(function);
		startProcessDigitalSign(conf,xml);
		
		log.info("Fim do metodo " + method);
	}
	
	private String getXML(Function function){
		// TODO - get XML from SAP
		return "<xml>xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</xml>";
	}
	
	private void startProcessDigitalSign(Properties conf, String xml){
		thread = new Thread(new DigitalSignThread(conf,xml));
		thread.start();
	}

}
