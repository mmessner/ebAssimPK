package br.com.golive.sap.rfc;

import com.sap.mw.jco.JCO;

public class ZARQUIVO extends EstruturaBase{

	public ZARQUIVO(String structureName) {
		super(structureName);
	}

	public JCO.MetaData createStructure(){
		JCO.MetaData struct = new JCO.MetaData(getStructureName());
		struct.addInfo("LINE", JCO.TYPE_CHAR, 30000, 0, 0);
		return struct;
	}	
	
}
