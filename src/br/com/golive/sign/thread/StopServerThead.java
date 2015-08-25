package br.com.golive.sign.thread;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;

public class StopServerThead implements Runnable{

	private static Logger log = LogUtil.getLogInstance(StopServerThead.class.getName());
	
	@Override
	public void run() {
		log.debug("Inicio Thread StopServer");
		try {
			Thread.currentThread().sleep(2000); // 2s
			log.debug("Finalizando aplicação!");
			System.exit(0); // Stop JVM
		} catch (InterruptedException e) {
			// TODO - Tratar
			e.printStackTrace();
		}
		log.debug("Fim Thread StopServer");
	}

}
