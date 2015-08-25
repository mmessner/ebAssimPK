/*
 * TokenReaderView.java
 */

package tokenreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.annotation.processing.Filer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;

import com.sap.mw.jco.JCO;

import br.com.golive.config.AppConfigUtil;
import br.com.golive.config.Config;
import br.com.golive.log.LogUtil;
import br.com.golive.sap.server.Server;
import br.com.golive.sap.server.ServerTest;
import br.com.golive.sign.PKCS7Data;
import br.com.golive.sign.XMLSignatureException;
import br.com.golive.sign.ks.core.Keystores;
import br.com.golive.token.TokenReader;
import br.com.golive.util.ServerErrorUtil;
import br.com.golive.util.Util;

/**
 * The application's main frame.
 */
public class TokenReaderView extends FrameView {
	private static Logger log = LogUtil.getLogInstance(TokenReaderView.class.getName());
	
	private Config cfg = null;
	
	private static final String PATH = System.getProperty("user.dir") + "/";
	
	private static final String PATH_LOG = PATH + "/" + "log" + "/";
	
    public TokenReaderView(Application app) {
        super(app);

        initComponents();
        
        loadConfig();
        
        this.saveConfigButton.setEnabled(false);
        
        // preenche combo trc files
        String[] trcFilesNames = readTrcFiles();
        if(trcFilesNames!=null){
        	this.trcFileCombo.setModel(new DefaultComboBoxModel(trcFilesNames));
        }

        // preenche combo log files
        String[] logFilesNames = readLogFiles();
        if(logFilesNames!=null){
        	this.logFileCombo.setModel(new DefaultComboBoxModel(logFilesNames));
        }
        
        // le arq trc
        fillTextArea(trcFileTextArea,trcFileCombo.getSelectedItem(),PATH);
        
//    	// le arq log
        fillTextArea(logFileTextArea,logFileCombo.getSelectedItem(),PATH_LOG);
        

//        // status bar initialization - message timeout, idle icon and busy animation, etc
//        ResourceMap resourceMap = getResourceMap();
//        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
//        messageTimer = new Timer(messageTimeout, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                statusMessageLabel.setText("");
//            }
//        });
//        messageTimer.setRepeats(false);
//        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
//        for (int i = 0; i < busyIcons.length; i++) {
//            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
//        }
//        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
//                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
//            }
//        });
//        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
//        statusAnimationLabel.setIcon(idleIcon);
//        progressBar.setVisible(false);
//
//        // connecting action tasks to status bar via TaskMonitor
//        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
//        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
//            public void propertyChange(java.beans.PropertyChangeEvent evt) {
//                String propertyName = evt.getPropertyName();
//                if ("started".equals(propertyName)) {
//                    if (!busyIconTimer.isRunning()) {
//                        statusAnimationLabel.setIcon(busyIcons[0]);
//                        busyIconIndex = 0;
//                        busyIconTimer.start();
//                    }
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(true);
//                } else if ("done".equals(propertyName)) {
//                    busyIconTimer.stop();
//                    statusAnimationLabel.setIcon(idleIcon);
//                    progressBar.setVisible(false);
//                    progressBar.setValue(0);
//                } else if ("message".equals(propertyName)) {
//                    String text = (String)(evt.getNewValue());
//                    statusMessageLabel.setText((text == null) ? "" : text);
//                    messageTimer.restart();
//                } else if ("progress".equals(propertyName)) {
//                    int value = (Integer)(evt.getNewValue());
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(false);
//                    progressBar.setValue(value);
//                }
//            }
//        });
    }

    // ***************************************************************************
    // BACKUP IT
    
    protected void fillTextArea(JTextArea txtArea, Object selectedItem, String path){
    	String selectedFileName = removeDateTime(selectedItem);
    	txtArea.setText(readFile(path+selectedFileName));
    }
    
    protected static String removeDateTime(Object fileName){
//    	08/07/2009 11:10:15 - rfc03152_03360.trc
//    	31/12/1969 21:00:00 - aplicacao.log
    	
    	if(fileName==null) return "";
    	
    	String[] arr;
    	try{
    		arr=((String)fileName).split(" ");
    		if(arr.length==4)
    			return arr[3];
    	}catch(Exception e){}
    	
    	return "";
    }
    
    protected static String readFile(String fileName){
    	StringBuffer result = new StringBuffer();
    	try{
    		File f = new File(fileName);
    		BufferedReader input = new BufferedReader(new FileReader(f));
    		String line;
    		while((line=input.readLine())!=null){
    			result.append(line+"\n");
    		}
    	}catch(Exception e){e.printStackTrace();}
    
    	return result.toString();
    }
    
    // Fill fields with values from configuration files
    protected boolean loadConfig(){
    	cfg = new Config();
    	
    	try{
    		cfg.load();
    	}catch(Exception e){
    		return false;
    	}
    		
    	// Get App Conf
        textCertName.setText(cfg.getCertAlias());
        comboKeystore.setModel(new DefaultComboBoxModel(getKeystoreTypes()));
        comboKeystore.setSelectedItem(cfg.getKeysotreType());
        // Get Sap Conf Dev
        sapGwDevText.setText(cfg.getSapGwDev());
        sapHostDevText.setText(cfg.getSapHostDev());
        sapProgIdDevText.setText(cfg.getSapProgIdDev());
        // Get Sap Conf Qas
        sapGwQasText.setText(cfg.getSapGwQas());
        sapHostQasText.setText(cfg.getSapHostQas());
        sapProgIdQasText.setText(cfg.getSapProgIdQas());
        // Get Sap Conf Prd
        sapGwPrdText.setText(cfg.getSapGwPrd());
        sapHostPrdText.setText(cfg.getSapHostPrd());
        sapProgIdPrdText.setText(cfg.getSapProgIdPrd());        
        
        return true;
    }    
    
    protected void storeConfig(){
    	// Set App Config
    	cfg.setCertAlias(textCertName.getText());
    	cfg.setKeysotreType((String)comboKeystore.getSelectedItem());
    	
    	// Set Sap Config - Dev
    	cfg.setSapGwDev(sapGwDevText.getText());
    	cfg.setSapHostDev(sapHostDevText.getText());
    	cfg.setSapProgIdDev(sapProgIdDevText.getText());

    	// Set Sap Config - Qas
    	cfg.setSapGwQas(sapGwQasText.getText());
    	cfg.setSapHostQas(sapHostQasText.getText());
    	cfg.setSapProgIdQas(sapProgIdQasText.getText());    	

    	// Set Sap Config - Prd
    	cfg.setSapGwPrd(sapGwPrdText.getText());
    	cfg.setSapHostPrd(sapHostPrdText.getText());
    	cfg.setSapProgIdPrd(sapProgIdPrdText.getText());    	    	
    	
    	// write config file
    	try {
			cfg.store();
		} catch (Exception e) {
			// TODO - Exibir erro 
		}
    }
    
    private String[] getKeystoreTypes(){
    	String[] ksTypes;
    	
    	Keystores[] values = Keystores.values();
    	ksTypes = new String[values.length];
    	
    	for(int i=0;i<ksTypes.length;i++){
    		ksTypes[i] = values[i].name();
    	}
    	
    	return ksTypes;
    }        
        
//    public static void main(String[] args) {
////    	String[] files = readTrcFiles();
////    	
////    	for(int i=0;i<files.length;i++){
////    		System.out.println(files[i]);
////    	}
////    	
////    	files = readLogFiles();
////    	
////    	for(int i=0;i<files.length;i++){
////    		System.out.println(files[i]);
////    	}
////    	String path = System.getProperty("user.dir")+"\\";
////    	String fileName = "rfc02600_03332.trc";
////    	System.out.println(path+fileName);
////    	System.out.println(readFile(path+fileName));
//    	
//    	String r = removeDateTime("08/07/2009 11:10:15 - rfc03152_03360.trc");
//    	System.out.println(r);
//    }
    
    private static String[] readTrcFiles(){
    	String ext = ".trc";
    	
    	return readFiles(ext, PATH);
    }
    
    private static String[] readLogFiles(){
    	String ext = ".log";
    	return readFiles(ext, PATH_LOG);
    }
    
    private static String[] readFiles(String ext, String path){
    	File f = new File(path);
    	File filetmp;
    	String dateStr;
    	
    	ArrayList list = new ArrayList();
    	
    	// obtem nome dos arquivos
    	String[] fileNames = f.list(new FilenameFilter(ext));
    	// adiciona data e hora
    	for(int i=0;i<fileNames.length;i++){
    		filetmp = new File(path+fileNames[i]);
    		dateStr = Util.formatDate(new Date(filetmp.lastModified()));
    		fileNames[i] = dateStr + " - " + fileNames[i];
    		list.add(fileNames[i]);
    	}
    	
    	// ordena
    	Collections.sort(list);
    	Collections.reverse(list);
    	
    	// converte Object[] para String[]
    	String[] retorno = null;
    	if(list!=null && list.size()>0){
    		retorno = new String[list.size()];
    		for(int i=0;i<list.size();i++){
    			retorno[i]=(String)list.get(i);
    		}
    	}
    	
    	return retorno;
    }
    
    
    // BACKUP IT
    // ***************************************************************************
    
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = TokenReaderApp.getApplication().getMainFrame();
            aboutBox = new TokenReaderAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        aboutBox.show();
//        TokenReaderApp.getApplication().getMainView().showAboutBox();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        mainTabPane = new javax.swing.JTabbedPane();
        testPanel = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        lbTest01 = new javax.swing.JLabel();
        lbTestStatusOk = new javax.swing.JLabel();
        fullTestButton = new javax.swing.JButton();
        lbTestStatusErr = new javax.swing.JLabel();
        configPanel = new javax.swing.JPanel();
        certPanel = new javax.swing.JPanel();
        keystoreLabel = new javax.swing.JLabel();
        certNameLabel = new javax.swing.JLabel();
        textCertName = new javax.swing.JTextField();
        comboKeystore = new javax.swing.JComboBox();
        sapPanel = new javax.swing.JPanel();
        sapTabPane = new javax.swing.JTabbedPane();
        sapDevPanel = new javax.swing.JPanel();
        sapGwDevText = new javax.swing.JTextField();
        sapHostDevText = new javax.swing.JTextField();
        sapProgIdDevText = new javax.swing.JTextField();
        sapHostDevLabel = new javax.swing.JLabel();
        sapGwDevLabel = new javax.swing.JLabel();
        sapProgIdDevLabel = new javax.swing.JLabel();
        testSapConfigDevButton = new javax.swing.JButton();
        sapDevTestLabel = new javax.swing.JLabel();
        sapQasPanel = new javax.swing.JPanel();
        sapGwQasText = new javax.swing.JTextField();
        sapHostQasText = new javax.swing.JTextField();
        sapProgIdQasText = new javax.swing.JTextField();
        sapHostQasLabel = new javax.swing.JLabel();
        sapGwQasLabel = new javax.swing.JLabel();
        sapProgIdQasLabel = new javax.swing.JLabel();
        testSapConfigQasButton = new javax.swing.JButton();
        sapQasTestLabel = new javax.swing.JLabel();
        sapPrdPanel = new javax.swing.JPanel();
        sapGwPrdText = new javax.swing.JTextField();
        sapHostPrdText = new javax.swing.JTextField();
        sapProgIdPrdText = new javax.swing.JTextField();
        sapHostPrdLabel = new javax.swing.JLabel();
        sapGwPrdLabel = new javax.swing.JLabel();
        sapProgIdPrdLabel = new javax.swing.JLabel();
        testSapConfigPrdButton = new javax.swing.JButton();
        sapPrdTestLabel = new javax.swing.JLabel();
        editConfigButton = new javax.swing.JButton();
        saveConfigButton = new javax.swing.JButton();
        trcPanel = new javax.swing.JPanel();
        trcFileCombo = new javax.swing.JComboBox();
        trcFileScrollPane = new javax.swing.JScrollPane();
        trcFileTextArea = new javax.swing.JTextArea();
        logPanel = new javax.swing.JPanel();
        logFileCombo = new javax.swing.JComboBox();
        logFileScrollPane = new javax.swing.JScrollPane();
        logFileTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        mainTabPane.setName("mainTabPane"); // NOI18N

        testPanel.setName("testPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(TokenReaderView.class);
        statusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("statusPanel.border.title"))); // NOI18N
        statusPanel.setName("statusPanel"); // NOI18N

        lbTest01.setText(resourceMap.getString("lbTest01.text")); // NOI18N
        lbTest01.setName("lbTest01"); // NOI18N

        lbTestStatusOk.setIcon(resourceMap.getIcon("lbTestStatusOk.icon")); // NOI18N
        lbTestStatusOk.setText(resourceMap.getString("lbTestStatusOk.text")); // NOI18N
        lbTestStatusOk.setName("lbTestStatusOk"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(tokenreader.TokenReaderApp.class).getContext().getActionMap(TokenReaderView.class, this);
        fullTestButton.setAction(actionMap.get("startTestFull")); // NOI18N
        fullTestButton.setText(resourceMap.getString("fullTestButton.text")); // NOI18N
        fullTestButton.setName("fullTestButton"); // NOI18N

        lbTestStatusErr.setIcon(resourceMap.getIcon("lbTestStatusErr.icon")); // NOI18N
        lbTestStatusErr.setText(resourceMap.getString("lbTestStatusErr.text")); // NOI18N
        lbTestStatusErr.setName("lbTestStatusErr"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fullTestButton)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addComponent(lbTest01)
                        .addGap(27, 27, 27)
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbTestStatusErr)
                            .addComponent(lbTestStatusOk))))
                .addContainerGap(850, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(fullTestButton)
                .addGap(10, 10, 10)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTest01)
                    .addComponent(lbTestStatusOk))
                .addGap(27, 27, 27)
                .addComponent(lbTestStatusErr)
                .addContainerGap(150, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout testPanelLayout = new javax.swing.GroupLayout(testPanel);
        testPanel.setLayout(testPanelLayout);
        testPanelLayout.setHorizontalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        testPanelLayout.setVerticalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );

        mainTabPane.addTab(resourceMap.getString("testPanel.TabConstraints.tabTitle"), testPanel); // NOI18N

        configPanel.setName("configPanel"); // NOI18N

        certPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("certPanel.border.title"))); // NOI18N
        certPanel.setName("certPanel"); // NOI18N

        keystoreLabel.setText(resourceMap.getString("keystoreLabel.text")); // NOI18N
        keystoreLabel.setName("keystoreLabel"); // NOI18N

        certNameLabel.setText(resourceMap.getString("certNameLabel.text")); // NOI18N
        certNameLabel.setName("certNameLabel"); // NOI18N

        textCertName.setToolTipText(resourceMap.getString("textCertName.toolTipText")); // NOI18N
        textCertName.setEnabled(false);
        textCertName.setName("textCertName"); // NOI18N

        comboKeystore.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboKeystore.setEnabled(false);
        comboKeystore.setName("comboKeystore"); // NOI18N

        javax.swing.GroupLayout certPanelLayout = new javax.swing.GroupLayout(certPanel);
        certPanel.setLayout(certPanelLayout);
        certPanelLayout.setHorizontalGroup(
            certPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(certPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(certPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(certNameLabel)
                    .addComponent(keystoreLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(certPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboKeystore, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textCertName, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                .addContainerGap(478, Short.MAX_VALUE))
        );
        certPanelLayout.setVerticalGroup(
            certPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(certPanelLayout.createSequentialGroup()
                .addGroup(certPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboKeystore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keystoreLabel))
                .addGap(18, 18, 18)
                .addGroup(certPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(certNameLabel)
                    .addComponent(textCertName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sapPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("sapPanel.border.title"))); // NOI18N
        sapPanel.setName("sapPanel"); // NOI18N

        sapTabPane.setToolTipText(resourceMap.getString("sapTabPane.toolTipText")); // NOI18N
        sapTabPane.setName("sapTabPane"); // NOI18N

        sapDevPanel.setName("sapDevPanel"); // NOI18N

        sapGwDevText.setEnabled(false);
        sapGwDevText.setName("sapGwDevText"); // NOI18N

        sapHostDevText.setToolTipText(resourceMap.getString("sapHostDevText.toolTipText")); // NOI18N
        sapHostDevText.setEnabled(false);
        sapHostDevText.setName("sapHostDevText"); // NOI18N

        sapProgIdDevText.setEnabled(false);
        sapProgIdDevText.setName("sapProgIdDevText"); // NOI18N

        sapHostDevLabel.setText(resourceMap.getString("sapHostDevLabel.text")); // NOI18N
        sapHostDevLabel.setName("sapHostDevLabel"); // NOI18N

        sapGwDevLabel.setText(resourceMap.getString("sapGwDevLabel.text")); // NOI18N
        sapGwDevLabel.setName("sapGwDevLabel"); // NOI18N

        sapProgIdDevLabel.setText(resourceMap.getString("sapProgIdDevLabel.text")); // NOI18N
        sapProgIdDevLabel.setName("sapProgIdDevLabel"); // NOI18N

        testSapConfigDevButton.setAction(actionMap.get("startTest")); // NOI18N
        testSapConfigDevButton.setIcon(resourceMap.getIcon("testSapConfigDevButton.icon")); // NOI18N
        testSapConfigDevButton.setText(resourceMap.getString("testSapConfigDevButton.text")); // NOI18N
        testSapConfigDevButton.setToolTipText(resourceMap.getString("testSapConfigDevButton.toolTipText")); // NOI18N
        testSapConfigDevButton.setName("testSapConfigDevButton"); // NOI18N

        sapDevTestLabel.setText(resourceMap.getString("sapDevTestLabel.text")); // NOI18N
        sapDevTestLabel.setName("sapDevTestLabel"); // NOI18N

        javax.swing.GroupLayout sapDevPanelLayout = new javax.swing.GroupLayout(sapDevPanel);
        sapDevPanel.setLayout(sapDevPanelLayout);
        sapDevPanelLayout.setHorizontalGroup(
            sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapDevPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sapDevPanelLayout.createSequentialGroup()
                        .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sapProgIdDevLabel)
                            .addComponent(sapGwDevLabel)
                            .addComponent(sapHostDevLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sapHostDevText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sapGwDevText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sapProgIdDevText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(sapDevPanelLayout.createSequentialGroup()
                        .addComponent(testSapConfigDevButton)
                        .addGap(18, 18, 18)
                        .addComponent(sapDevTestLabel)))
                .addContainerGap(202, Short.MAX_VALUE))
        );
        sapDevPanelLayout.setVerticalGroup(
            sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapDevPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapHostDevText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapHostDevLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapGwDevText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapGwDevLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapProgIdDevText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapProgIdDevLabel))
                .addGap(18, 18, 18)
                .addGroup(sapDevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testSapConfigDevButton)
                    .addComponent(sapDevTestLabel))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        sapTabPane.addTab(resourceMap.getString("sapDevPanel.TabConstraints.tabTitle"), sapDevPanel); // NOI18N

        sapQasPanel.setName("sapQasPanel"); // NOI18N

        sapGwQasText.setEnabled(false);
        sapGwQasText.setName("sapGwQasText"); // NOI18N

        sapHostQasText.setEnabled(false);
        sapHostQasText.setName("sapHostQasText"); // NOI18N

        sapProgIdQasText.setEnabled(false);
        sapProgIdQasText.setName("sapProgIdQasText"); // NOI18N

        sapHostQasLabel.setText(resourceMap.getString("sapHostQasLabel.text")); // NOI18N
        sapHostQasLabel.setName("sapHostQasLabel"); // NOI18N

        sapGwQasLabel.setText(resourceMap.getString("sapGwQasLabel.text")); // NOI18N
        sapGwQasLabel.setName("sapGwQasLabel"); // NOI18N

        sapProgIdQasLabel.setText(resourceMap.getString("sapProgIdQasLabel.text")); // NOI18N
        sapProgIdQasLabel.setName("sapProgIdQasLabel"); // NOI18N

        testSapConfigQasButton.setAction(actionMap.get("startTestSapQas")); // NOI18N
        testSapConfigQasButton.setIcon(null);
        testSapConfigQasButton.setText(resourceMap.getString("testSapConfigQasButton.text")); // NOI18N
        testSapConfigQasButton.setToolTipText(resourceMap.getString("testSapConfigQasButton.toolTipText")); // NOI18N
        testSapConfigQasButton.setName("testSapConfigQasButton"); // NOI18N

        sapQasTestLabel.setText(resourceMap.getString("sapQasTestLabel.text")); // NOI18N
        sapQasTestLabel.setName("sapQasTestLabel"); // NOI18N

        javax.swing.GroupLayout sapQasPanelLayout = new javax.swing.GroupLayout(sapQasPanel);
        sapQasPanel.setLayout(sapQasPanelLayout);
        sapQasPanelLayout.setHorizontalGroup(
            sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapQasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sapQasPanelLayout.createSequentialGroup()
                        .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sapProgIdQasLabel)
                            .addComponent(sapGwQasLabel)
                            .addComponent(sapHostQasLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sapHostQasText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sapGwQasText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sapProgIdQasText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(sapQasPanelLayout.createSequentialGroup()
                        .addComponent(testSapConfigQasButton)
                        .addGap(18, 18, 18)
                        .addComponent(sapQasTestLabel)))
                .addContainerGap(202, Short.MAX_VALUE))
        );
        sapQasPanelLayout.setVerticalGroup(
            sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapQasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapHostQasText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapHostQasLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapGwQasText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapGwQasLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapProgIdQasText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapProgIdQasLabel))
                .addGap(18, 18, 18)
                .addGroup(sapQasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testSapConfigQasButton)
                    .addComponent(sapQasTestLabel))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        sapTabPane.addTab(resourceMap.getString("sapQasPanel.TabConstraints.tabTitle"), sapQasPanel); // NOI18N

        sapPrdPanel.setName("sapPrdPanel"); // NOI18N

        sapGwPrdText.setEnabled(false);
        sapGwPrdText.setName("sapGwPrdText"); // NOI18N

        sapHostPrdText.setEnabled(false);
        sapHostPrdText.setName("sapHostPrdText"); // NOI18N

        sapProgIdPrdText.setEnabled(false);
        sapProgIdPrdText.setName("sapProgIdPrdText"); // NOI18N

        sapHostPrdLabel.setText(resourceMap.getString("sapHostPrdLabel.text")); // NOI18N
        sapHostPrdLabel.setName("sapHostPrdLabel"); // NOI18N

        sapGwPrdLabel.setText(resourceMap.getString("sapGwPrdLabel.text")); // NOI18N
        sapGwPrdLabel.setName("sapGwPrdLabel"); // NOI18N

        sapProgIdPrdLabel.setText(resourceMap.getString("sapProgIdPrdLabel.text")); // NOI18N
        sapProgIdPrdLabel.setName("sapProgIdPrdLabel"); // NOI18N
        
        testSapConfigPrdButton.setAction(actionMap.get("startTestSapDev")); // NOI18N
        testSapConfigPrdButton.setIcon(null);
        testSapConfigPrdButton.setText(resourceMap.getString("testSapConfigPrdButton.text")); // NOI18N
        testSapConfigPrdButton.setToolTipText(resourceMap.getString("testSapConfigPrdButton.toolTipText")); // NOI18N
        testSapConfigPrdButton.setName("testSapConfigPrdButton"); // NOI18N

        sapPrdTestLabel.setText(resourceMap.getString("sapPrdTestLabel.text")); // NOI18N
        sapPrdTestLabel.setName("sapPrdTestLabel"); // NOI18N

        javax.swing.GroupLayout sapPrdPanelLayout = new javax.swing.GroupLayout(sapPrdPanel);
        sapPrdPanel.setLayout(sapPrdPanelLayout);
        sapPrdPanelLayout.setHorizontalGroup(
            sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapPrdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sapPrdPanelLayout.createSequentialGroup()
                        .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sapProgIdPrdLabel)
                            .addComponent(sapGwPrdLabel)
                            .addComponent(sapHostPrdLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sapHostPrdText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sapGwPrdText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sapProgIdPrdText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(sapPrdPanelLayout.createSequentialGroup()
                        .addComponent(testSapConfigPrdButton)
                        .addGap(18, 18, 18)
                        .addComponent(sapPrdTestLabel)))
                .addContainerGap(202, Short.MAX_VALUE))
        );
        sapPrdPanelLayout.setVerticalGroup(
            sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapPrdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapHostPrdText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapHostPrdLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapGwPrdText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapGwPrdLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sapProgIdPrdText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sapProgIdPrdLabel))
                .addGap(18, 18, 18)
                .addGroup(sapPrdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testSapConfigPrdButton)
                    .addComponent(sapPrdTestLabel))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        sapTabPane.addTab(resourceMap.getString("sapPrdPanel.TabConstraints.tabTitle"), sapPrdPanel); // NOI18N

        javax.swing.GroupLayout sapPanelLayout = new javax.swing.GroupLayout(sapPanel);
        sapPanel.setLayout(sapPanelLayout);
        sapPanelLayout.setHorizontalGroup(
            sapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapPanelLayout.createSequentialGroup()
                .addComponent(sapTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(693, Short.MAX_VALUE))
        );
        sapPanelLayout.setVerticalGroup(
            sapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sapPanelLayout.createSequentialGroup()
                .addComponent(sapTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        editConfigButton.setAction(actionMap.get("editConfig")); // NOI18N
        editConfigButton.setToolTipText(resourceMap.getString("editConfigButton.toolTipText")); // NOI18N
        editConfigButton.setName("editConfigButton"); // NOI18N

        saveConfigButton.setAction(actionMap.get("saveConfig")); // NOI18N
        saveConfigButton.setText(resourceMap.getString("saveConfigButton.text")); // NOI18N
        saveConfigButton.setToolTipText(resourceMap.getString("saveConfigButton.toolTipText")); // NOI18N
        saveConfigButton.setName("saveConfigButton"); // NOI18N

        javax.swing.GroupLayout configPanelLayout = new javax.swing.GroupLayout(configPanel);
        configPanel.setLayout(configPanelLayout);
        configPanelLayout.setHorizontalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(configPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(configPanelLayout.createSequentialGroup()
                        .addComponent(sapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(configPanelLayout.createSequentialGroup()
                        .addComponent(editConfigButton)
                        .addGap(18, 18, 18)
                        .addComponent(saveConfigButton)
                        .addContainerGap())
                    .addGroup(configPanelLayout.createSequentialGroup()
                        .addComponent(certPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(423, 423, 423))))
        );
        configPanelLayout.setVerticalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(configPanelLayout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addGroup(configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editConfigButton)
                    .addComponent(saveConfigButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(certPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77))
        );

        mainTabPane.addTab(resourceMap.getString("configPanel.TabConstraints.tabTitle"), null, configPanel, resourceMap.getString("configPanel.TabConstraints.tabToolTip")); // NOI18N

        trcPanel.setName("trcPanel"); // NOI18N

        trcFileCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        trcFileCombo.setName("trcFileCombo"); // NOI18N
        trcFileCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                trcFileComboItemStateChanged(evt);
            }
        });

        trcFileScrollPane.setName("trcFileScrollPane"); // NOI18N

        trcFileTextArea.setColumns(20);
        trcFileTextArea.setRows(5);
        trcFileTextArea.setName("trcFileTextArea"); // NOI18N
        trcFileScrollPane.setViewportView(trcFileTextArea);

        javax.swing.GroupLayout trcPanelLayout = new javax.swing.GroupLayout(trcPanel);
        trcPanel.setLayout(trcPanelLayout);
        trcPanelLayout.setHorizontalGroup(
            trcPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trcPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(trcPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(trcFileCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trcFileScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
                .addContainerGap())
        );
        trcPanelLayout.setVerticalGroup(
            trcPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trcPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(trcFileCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trcFileScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabPane.addTab(resourceMap.getString("trcPanel.TabConstraints.tabTitle"), null, trcPanel, resourceMap.getString("trcPanel.TabConstraints.tabToolTip")); // NOI18N

        logPanel.setName("logPanel"); // NOI18N

        logFileCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        logFileCombo.setName("logFileCombo"); // NOI18N
        logFileCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                logFileComboItemStateChanged(evt);
            }
        });

        logFileScrollPane.setName("logFileScrollPane"); // NOI18N

        logFileTextArea.setColumns(20);
        logFileTextArea.setRows(5);
        logFileTextArea.setName("logFileTextArea"); // NOI18N
        logFileScrollPane.setViewportView(logFileTextArea);

        javax.swing.GroupLayout logPanelLayout = new javax.swing.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logFileCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logFileScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
                .addContainerGap())
        );
        logPanelLayout.setVerticalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logFileCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logFileScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabPane.addTab(resourceMap.getString("logPanel.TabConstraints.tabTitle"), null, logPanel, resourceMap.getString("logPanel.TabConstraints.tabToolTip")); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        
        jButton1.setAction(actionMap.get("getCertificados"));
        jButton1.setText("Atualizar");	
        
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(822, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(193, Short.MAX_VALUE))
        );

        mainTabPane.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabPane)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void trcFileComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_trcFileComboItemStateChanged
        // TODO add your handling code here:
        trcFileTextArea.setText("Arquivo selecionado: " + (String)evt.getItem());
    }//GEN-LAST:event_trcFileComboItemStateChanged

    private void logFileComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logFileComboItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_logFileComboItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed




    @Action
    public void editConfig() {
        sapGwPrdText.setEnabled(true);
        sapHostPrdText.setEnabled(true);
        sapProgIdPrdText.setEnabled(true);
        sapGwQasText.setEnabled(true);
        sapHostQasText.setEnabled(true);
        sapProgIdQasText.setEnabled(true);
        sapGwDevText.setEnabled(true);
        sapHostDevText.setEnabled(true);
        sapProgIdDevText.setEnabled(true);
        textCertName.setEnabled(true);
        editConfigButton.setEnabled(false);
        saveConfigButton.setEnabled(true);
    }
    
    @Action
    public void getCertificados() {
        
    	StringBuilder sb = new StringBuilder();
    	Enumeration<String> aliases = null;
    	try {
			KeyStore ks = null;
			try {
				//ks = KeyStore.getInstance(KeyStore.getDefaultType());
				ks = KeyStore.getInstance("Windows-MY");
				// Note: When a security manager is installed,
				// the following call requires SecurityPermission
				// "authProvider.SunMSCAPI".
				ks.load(null, null);
			} catch (KeyStoreException e) {
				throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
			} catch (CertificateException e) {
				throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
			} catch (IOException e) {
				throw new KeyStoreException("Erro ao carregar KeyStore. " + e + e.getMessage());
			}

			if (ks == null) {
				throw new KeyStoreException("Erro ao carregar KeyStore. KeyStore nula.");
			}
			
			aliases = ks.aliases();			
			while (aliases.hasMoreElements()) {
				sb.append(aliases.nextElement() + "\n");
			} ;
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		if (sb.length() > 0) {
			jTextArea1.setText(sb.toString());
		} else {
			jTextArea1.setText("Nenhum certificado instalado");
		}
    }

    @Action
    public void saveConfig() {
        sapGwPrdText.setEnabled(false);
        sapHostPrdText.setEnabled(false);
        sapProgIdPrdText.setEnabled(false);
        sapGwQasText.setEnabled(false);
        sapHostQasText.setEnabled(false);
        sapProgIdQasText.setEnabled(false);
        sapGwDevText.setEnabled(false);
        sapHostDevText.setEnabled(false);
        sapProgIdDevText.setEnabled(false);
        textCertName.setEnabled(false);
        editConfigButton.setEnabled(true);
        saveConfigButton.setEnabled(false);
    }

    @Action
    public void startTestFull() {
    }
    
    @Action
    public void manager() {
        comboKeystore.setEnabled(true);
    }

    @Action
    public void startTestSapDev() {
    	log.info("Inicio teste de conex�o. Ambiente [DEV].");

    	log.debug("Propriedades:");
    	log.debug("jco.server.gwhost=" + sapHostDevText.getText());
    	log.debug("jco.server.progid=" + sapProgIdDevText.getText());
    	log.debug("jco.server.gwserv=" + sapGwDevText.getText());    	
    	
    	Properties conf = new Properties();
    	conf.setProperty("jco.server.gwhost", sapHostDevText.getText());
    	conf.setProperty("jco.server.progid", sapProgIdDevText.getText());
    	conf.setProperty("jco.server.gwserv", sapGwDevText.getText());
    	conf.setProperty("jco.server.trace",  "1");
    	conf.setProperty("jco.server.unicode", "0");
    	
    	String resultado = testSap(conf);
    	this.sapDevTestLabel.setText(resultado);
    	
    	log.info("Fim teste de conex�o. Ambiente [DEV].");
    }    
    
    @Action
    public void startTestSapQas() {
    	log.info("Inicio teste de conex�o. Ambiente [QAS].");

    	log.debug("Propriedades:");
    	log.debug("jco.server.gwhost=" + sapHostQasText.getText());
    	log.debug("jco.server.progid=" + sapProgIdQasText.getText());
    	log.debug("jco.server.gwserv=" + sapGwQasText.getText());    	
    	
    	Properties conf = new Properties();
    	conf.setProperty("jco.server.gwhost", sapHostQasText.getText());
    	conf.setProperty("jco.server.progid", sapProgIdQasText.getText());
    	conf.setProperty("jco.server.gwserv", sapGwQasText.getText());
    	conf.setProperty("jco.server.trace",  "1");
    	conf.setProperty("jco.server.unicode", "0");
    	
    	String resultado = testSap(conf);
    	this.sapQasTestLabel.setText(resultado);
    	
    	log.info("Fim teste de conex�o. Ambiente [QAS].");    	
    }

    @Action
    public void startTestSapPrd() {
    	log.info("Inicio teste de conex�o. Ambiente [PRD].");

    	log.debug("Propriedades:");
    	log.debug("jco.server.gwhost=" + sapHostPrdText.getText());
    	log.debug("jco.server.progid=" + sapProgIdPrdText.getText());
    	log.debug("jco.server.gwserv=" + sapGwPrdText.getText());    	
    	
    	Properties conf = new Properties();
    	conf.setProperty("jco.server.gwhost", sapHostPrdText.getText());
    	conf.setProperty("jco.server.progid", sapProgIdPrdText.getText());
    	conf.setProperty("jco.server.gwserv", sapGwPrdText.getText());
    	conf.setProperty("jco.server.trace",  "1");
    	conf.setProperty("jco.server.unicode", "0");
    	
    	String resultado = testSap(conf);
    	this.sapPrdTestLabel.setText(resultado);
    	
    	log.info("Fim teste de conex�o. Ambiente [PRD].");        	
    }

    private String testSap(Properties conf){
    	String resultado;
    	try {
    		log.debug("Iniciando servidor.");
    		
    		ServerTest server = new ServerTest(conf);
			server.start();
			
			Thread.currentThread().sleep(15000);
			
//			String status = "";
//			if(server.getState()==JCO.STATE_CONNECTED){
//				status = "connected";
//			}else if(server.getState()==JCO.STATE_STARTED){
//				status = "started";
//			}else if(server.getState()==JCO.STATE_LISTENING){
//				status = "listening";
//			}else if(server.getState()==JCO.STATE_BUSY){
//				status = "busy";
//			}else if(server.getState()==JCO.STATE_DISCONNECTED){
//				status = "disconnected";
//			}else if(server.getState()==JCO.STATE_STOPPED){
//				status = "stopped";
//			}else if(server.getState()==JCO.STATE_SUSPENDED){
//				status = "suspended";
//			}else if(server.getState()==JCO.STATE_TRANSACTION){
//				status = "transaction";
//			}else if(server.getState()==JCO.STATE_USED){
//				status = "used";
//			}
			server.abort("");
			server.disconnect();
			server.stop();
			
			if(ServerErrorUtil.getLastServerError(server)==null){
				resultado = "Teste realizado com sucesso. Configura��es validas!";
			}else{
				resultado = "Configura��es inv�lidas. Detalhes no log!";
				fillTextArea(logFileTextArea,logFileCombo.getSelectedItem(),PATH_LOG);
			}
			
//			log.debug("### LAST ERROR: " + ServerErrorUtil.getLastServerError(server));
//			log.debug("$$$ STATUS: " + status);			
    	} catch (Exception e) {
			log.error(e);
			resultado = "Erro durante o teste! Detalhes no log!";
		}
		return resultado;
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel certNameLabel;
    javax.swing.JPanel certPanel;
    javax.swing.JComboBox comboKeystore;
    javax.swing.JPanel configPanel;
    javax.swing.JButton editConfigButton;
    javax.swing.JButton fullTestButton;
    javax.swing.JButton jButton1;
    javax.swing.JLabel jLabel1;
    javax.swing.JPanel jPanel1;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTextArea jTextArea1;
    javax.swing.JLabel keystoreLabel;
    javax.swing.JLabel lbTest01;
    javax.swing.JLabel lbTestStatusErr;
    javax.swing.JLabel lbTestStatusOk;
    javax.swing.JComboBox logFileCombo;
    javax.swing.JScrollPane logFileScrollPane;
    javax.swing.JTextArea logFileTextArea;
    javax.swing.JPanel logPanel;
    javax.swing.JPanel mainPanel;
    javax.swing.JTabbedPane mainTabPane;
    javax.swing.JMenuBar menuBar;
    javax.swing.JPanel sapDevPanel;
    javax.swing.JLabel sapDevTestLabel;
    javax.swing.JLabel sapGwDevLabel;
    javax.swing.JTextField sapGwDevText;
    javax.swing.JLabel sapGwPrdLabel;
    javax.swing.JTextField sapGwPrdText;
    javax.swing.JLabel sapGwQasLabel;
    javax.swing.JTextField sapGwQasText;
    javax.swing.JLabel sapHostDevLabel;
    javax.swing.JTextField sapHostDevText;
    javax.swing.JLabel sapHostPrdLabel;
    javax.swing.JTextField sapHostPrdText;
    javax.swing.JLabel sapHostQasLabel;
    javax.swing.JTextField sapHostQasText;
    javax.swing.JPanel sapPanel;
    javax.swing.JPanel sapPrdPanel;
    javax.swing.JLabel sapPrdTestLabel;
    javax.swing.JLabel sapProgIdDevLabel;
    javax.swing.JTextField sapProgIdDevText;
    javax.swing.JLabel sapProgIdPrdLabel;
    javax.swing.JTextField sapProgIdPrdText;
    javax.swing.JLabel sapProgIdQasLabel;
    javax.swing.JTextField sapProgIdQasText;
    javax.swing.JPanel sapQasPanel;
    javax.swing.JLabel sapQasTestLabel;
    javax.swing.JTabbedPane sapTabPane;
    javax.swing.JButton saveConfigButton;
    javax.swing.JPanel statusPanel;
    javax.swing.JPanel testPanel;
    javax.swing.JButton testSapConfigDevButton;
    javax.swing.JButton testSapConfigPrdButton;
    javax.swing.JButton testSapConfigQasButton;
    javax.swing.JTextField textCertName;
    javax.swing.JComboBox trcFileCombo;
    javax.swing.JScrollPane trcFileScrollPane;
    javax.swing.JTextArea trcFileTextArea;
    javax.swing.JPanel trcPanel;
    // End of variables declaration//GEN-END:variables

//    private final Timer messageTimer;
//    private final Timer busyIconTimer;
//    private final Icon idleIcon;
//    private final Icon[] busyIcons = new Icon[15];
//    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
