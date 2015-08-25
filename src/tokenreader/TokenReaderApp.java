/*
 * TokenReaderApp.java
 */

package tokenreader;

import java.awt.SystemTray;

import javax.swing.JFrame;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import br.com.golive.tray.IconeTray;

/**
 * The main class of the application.
 */
public class TokenReaderApp extends Application {

	private static TokenReaderView mainView;
	
	JFrame mainFrame = null;
	
	private static IconeTray iconeTray;
	
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
    	mainView = new TokenReaderView(this);
//    	this.setMainFrame(mainView.getFrame());
    	mainFrame = mainView.getFrame();
    	mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of TokenReaderApp
     */
    public static TokenReaderApp getApplication() {
        return Application.getInstance(TokenReaderApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(TokenReaderApp.class, args);
    }
    
    public static void start() {
        launch(TokenReaderApp.class, null);
        iconeTray = new IconeTray(SystemTray.getSystemTray());
    }

	public static TokenReaderView getMainView() {
		return mainView;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}
}
