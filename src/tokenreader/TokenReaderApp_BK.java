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
public class TokenReaderApp_BK extends SingleFrameApplication {

	private static TokenReaderView mainView;
	
	private static IconeTray iconeTray;
	
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
    	mainView = new TokenReaderView(this);
//    	this.setMainFrame(mainView.getFrame());
    	mainView.getFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of TokenReaderApp
     */
    public static TokenReaderApp_BK getApplication() {
        return Application.getInstance(TokenReaderApp_BK.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(TokenReaderApp_BK.class, args);
    }
    
    public static void start() {
        launch(TokenReaderApp_BK.class, null);
        iconeTray = new IconeTray(SystemTray.getSystemTray());
    }

	public static TokenReaderView getMainViewCust() {
		return mainView;
	}
	
	@Override
	protected void shutdown() {
//		super.shutdown();
		getMainFrame().setVisible(false);
	}
}
