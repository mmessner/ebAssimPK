package br.com.golive.tray;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class ExitClickListener implements ActionListener {

	private static final String MSG_FECHAR = "Finalizar NFe Reader." + "  \n Tem certeza?";

	private static final String TITULO_JANELA = "NFe Reader";
	
	public void actionPerformed(ActionEvent e) {
		int resposta = JOptionPane.showConfirmDialog(null, MSG_FECHAR,TITULO_JANELA,0);

		if (resposta == JOptionPane.YES_OPTION) {
			System.exit(0);
		}

	}

}
