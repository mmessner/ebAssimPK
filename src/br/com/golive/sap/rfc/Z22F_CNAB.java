package br.com.golive.sap.rfc;

import com.sap.mw.jco.JCO;

public class Z22F_CNAB extends EstruturaBase {

	public Z22F_CNAB(String structureName) {
		super(structureName);
	}

	
	public JCO.MetaData createStructure(){
		JCO.MetaData struct = new JCO.MetaData(getStructureName());
		struct.addInfo("LINHA", JCO.TYPE_CHAR, 240, 0, 0);
		return struct;
	}
}
