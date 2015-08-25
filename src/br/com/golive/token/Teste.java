package br.com.golive.token;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import br.com.golive.config.AppConfigUtil;

public class Teste {
	public static void main(String[] args) throws Exception {
		Properties conf = AppConfigUtil.getConfig();
		
		FileOutputStream out = new FileOutputStream(new File("d:\\testeConf.txt"));
		
		conf.store(out, " Automatically stored!");
		
		System.out.println("fim");
	}
}
