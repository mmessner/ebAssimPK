package br.com.golive.tray;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import tokenreader.TokenReaderApp;

public class IconeTrayClickListener implements MouseListener{

	private TokenReaderApp app = null;

	public IconeTrayClickListener(TokenReaderApp app){
		this.app = app;
	}

	public void mouseClicked(MouseEvent e) {
		if (e != null){
			app.show(app.getMainView());
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}


}
