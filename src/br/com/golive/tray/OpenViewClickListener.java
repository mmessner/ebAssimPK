package br.com.golive.tray;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tokenreader.TokenReaderApp;
import tokenreader.TokenReaderView;

public class OpenViewClickListener implements ActionListener{

	TokenReaderApp app = null;

	public OpenViewClickListener(TokenReaderApp app){
		this.app = app;
	}

	public void actionPerformed(ActionEvent e) {
		if (e != null){
			app.show(app.getMainView());
		}
	}

}
