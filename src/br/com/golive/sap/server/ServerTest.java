package br.com.golive.sap.server;

import java.util.Properties;

import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Function;

public class ServerTest extends JCO.Server{
	public ServerTest(Properties sapConfig) {
		super(sapConfig);
	}
	
	@Override
	protected void handleRequest(Function function) throws Exception {
		// do nothing
	}
}
