package br.com.golive.sap.server;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;
import br.com.golive.util.ServerErrorUtil;

import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Server;

public class ServerExceptionListener implements JCO.ServerExceptionListener, JCO.ServerErrorListener {
	private static Logger log = LogUtil.getLogInstance(ServerExceptionListener.class.getName());

	public void serverExceptionOccurred(Server server, Exception e) {
		String msg = "Erro ao se conectar com SAP. " + e + e.getMessage();
		ServerErrorUtil.storeError(server, e.getMessage());
		log.error(msg);
	}

	public void serverErrorOccurred(Server server, Error er) {
		String msg = "Erro ao se conectar com SAP. " + er + er.getMessage();
		ServerErrorUtil.storeError(server, er.getMessage());
		log.error(msg);
	}
}
