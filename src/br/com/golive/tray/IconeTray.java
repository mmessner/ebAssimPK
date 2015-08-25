package br.com.golive.tray;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.File;

import tokenreader.TokenReaderApp;
import tokenreader.TokenReaderView;

public class IconeTray {

	private TrayIcon icone;
	private PopupMenu popup;

	private void iniciaMenu()
	{
		popup = new PopupMenu();
		popup.add(createMenuItem("clickLog", "Abrir", new OpenViewClickListener(getApp())));
		popup.add(createMenuItem("fechar", "Fechar", new ExitClickListener()));
	}

	private MenuItem createMenuItem(String nome, String label, ActionListener listener)
	{
		MenuItem result = new MenuItem();
		result.setName(nome);
		result.setLabel(label);
		result.addActionListener(listener);
		
		return (result);
	}
	
	public IconeTray(SystemTray sysTray)
	{
		File f = new File("ico/check.gif");
		Image imageIcon = Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
		
		iniciaMenu();
		
		icone = new TrayIcon(imageIcon, "NFe Reader");
		icone.setPopupMenu(popup);
		icone.addMouseListener(new IconeTrayClickListener(getApp()));
		try
		{
			sysTray.add(icone);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private TokenReaderApp getApp(){
		return TokenReaderApp.getApplication();
	}
	
}
