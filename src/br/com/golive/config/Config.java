package br.com.golive.config;

import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.omg.CORBA.portable.OutputStream;

import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.core.Keystores;

/**
 *  Read and Write configurations. Used by Monitor TokenReaderView.
 * 
 * @author Carlos Cambra
 * 
 */
public class Config {
	private static Logger log = LogUtil.getLogInstance(Config.class.getName());
	
	private Properties   appConfig;
	private Properties[] sapConfig = new Properties[3];
	
	// App Config
	private String keysotreType;
	private String certAlias;
	private String rfcName;
	// Sap Config - Dev
	private String sapHostDev;
	private String sapGwDev;
	private String sapProgIdDev;
	// Sap Config - Qas
	private String sapHostQas;
	private String sapGwQas;
	private String sapProgIdQas;	
	// Sap Config - Prd
	private String sapHostPrd;
	private String sapGwPrd;
	private String sapProgIdPrd;	
	
	public String getKeysotreType() {
		return keysotreType;
	}

	public void setKeysotreType(String keysotreType) {
		this.keysotreType = keysotreType;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	public String getRfcName() {
		return rfcName;
	}

	public void setRfcName(String rfcName) {
		this.rfcName = rfcName;
	}

	public void load() throws Exception{
		loadAppProp();
		loadSapProp();
	}

	public void store() throws Exception{
		storeAppProp();
		storeSapProp();
	}
	
	// Read App Config Properties
	private void loadAppProp() throws Exception{
		this.appConfig = AppConfigUtil.getConfig();
		this.keysotreType = appConfig.getProperty("token.keystore.type");
		this.certAlias = appConfig.getProperty("token.cert.alias");
	}
	
	private void loadSapProp() throws Exception{
		Properties sapConfig; 
		
		// Dev
		sapConfig = SAPConfigUtil.getConfig("dev");
		setSapHostDev(sapConfig.getProperty("jco.server.gwhost"));
		setSapGwDev(sapConfig.getProperty("jco.server.gwserv"));
		setSapProgIdDev(sapConfig.getProperty("jco.server.progid"));
		this.sapConfig[0] = sapConfig;
		
		// Qas
		sapConfig = SAPConfigUtil.getConfig("qas");
		setSapHostQas(sapConfig.getProperty("jco.server.gwhost"));
		setSapGwQas(sapConfig.getProperty("jco.server.gwserv"));
		setSapProgIdQas(sapConfig.getProperty("jco.server.progid"));
		this.sapConfig[1] = sapConfig;	

		// Prd
		sapConfig = SAPConfigUtil.getConfig("prd");
		setSapHostPrd(sapConfig.getProperty("jco.server.gwhost"));
		setSapGwPrd(sapConfig.getProperty("jco.server.gwserv"));
		setSapProgIdPrd(sapConfig.getProperty("jco.server.progid"));
		this.sapConfig[2] = sapConfig;
	}
	

	// Write App Config Properties
	private void storeAppProp() throws Exception{
		appConfig.setProperty("token.keystore.type",getKeysotreType());
		appConfig.setProperty("token.cert.alias", getCertAlias());
		AppConfigUtil.store();
	}
	
	private void storeSapProp() throws Exception{
		// Dev
		Properties sapConfig = this.sapConfig[0];
		sapConfig.setProperty("jco.server.gwhost", getSapHostDev());
		sapConfig.setProperty("jco.server.gwserv", getSapGwDev());
		sapConfig.setProperty("jco.server.progid", getSapProgIdDev());
		
		SAPConfigUtil.store("dev", sapConfig);

		// Qas
		sapConfig = this.sapConfig[1];
		sapConfig.setProperty("jco.server.gwhost", getSapHostQas());
		sapConfig.setProperty("jco.server.gwserv", getSapGwQas());
		sapConfig.setProperty("jco.server.progid", getSapProgIdQas());

		SAPConfigUtil.store("qas", sapConfig);		
		
		// Prd
		sapConfig = this.sapConfig[2];
		sapConfig.setProperty("jco.server.gwhost", getSapHostPrd());
		sapConfig.setProperty("jco.server.gwserv", getSapGwPrd());
		sapConfig.setProperty("jco.server.progid", getSapProgIdPrd());
		
		SAPConfigUtil.store("prd", sapConfig);
	}

	public Properties getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(Properties appConfig) {
		this.appConfig = appConfig;
	}

	public Properties[] getSapConfig() {
		return sapConfig;
	}

	public void setSapConfig(Properties[] sapConfig) {
		this.sapConfig = sapConfig;
	}

	public String getSapHostDev() {
		return sapHostDev;
	}

	public void setSapHostDev(String sapHostDev) {
		this.sapHostDev = sapHostDev;
	}

	public String getSapGwDev() {
		return sapGwDev;
	}

	public void setSapGwDev(String sapGwDev) {
		this.sapGwDev = sapGwDev;
	}

	public String getSapProgIdDev() {
		return sapProgIdDev;
	}

	public void setSapProgIdDev(String sapProgIdDev) {
		this.sapProgIdDev = sapProgIdDev;
	}

	public String getSapHostQas() {
		return sapHostQas;
	}

	public void setSapHostQas(String sapHostQas) {
		this.sapHostQas = sapHostQas;
	}

	public String getSapGwQas() {
		return sapGwQas;
	}

	public void setSapGwQas(String sapGwQas) {
		this.sapGwQas = sapGwQas;
	}

	public String getSapProgIdQas() {
		return sapProgIdQas;
	}

	public void setSapProgIdQas(String sapProgIdQas) {
		this.sapProgIdQas = sapProgIdQas;
	}

	public String getSapHostPrd() {
		return sapHostPrd;
	}

	public void setSapHostPrd(String sapHostPrd) {
		this.sapHostPrd = sapHostPrd;
	}

	public String getSapGwPrd() {
		return sapGwPrd;
	}

	public void setSapGwPrd(String sapGwPrd) {
		this.sapGwPrd = sapGwPrd;
	}

	public String getSapProgIdPrd() {
		return sapProgIdPrd;
	}

	public void setSapProgIdPrd(String sapProgIdPrd) {
		this.sapProgIdPrd = sapProgIdPrd;
	}
}
