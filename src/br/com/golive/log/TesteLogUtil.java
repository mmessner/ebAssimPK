package br.com.golive.log;

import org.apache.log4j.Logger;

public class TesteLogUtil {
	
	static Logger log = LogUtil.getLogInstance(TesteLogUtil.class.getName());
	
	public static void main(String[] args) {
		System.out.println("Inicio do teste!");
		
		testeNovoLog();
		
		System.out.println("Fim do teste!");
	}
	
	private static void testeNovoLog(){
		log.error("Minha nova menssagem de erro");
	}
}
