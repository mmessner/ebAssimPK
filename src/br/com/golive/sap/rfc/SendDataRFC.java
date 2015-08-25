package br.com.golive.sap.rfc;

import com.sap.mw.jco.JCO;

public class SendDataRFC extends EstruturaBase{ 
	public SendDataRFC(String structureName) {
		super(structureName);
	}

	public JCO.MetaData createFunction(){
		JCO.MetaData fmeta = new JCO.MetaData(getStructureName());

		/*
		FUNCTION Z22F_REC_ARQ_ASS.
		*"----------------------------------------------------------------------
		*"*"Interface local:
		*"  IMPORTING
		*"     VALUE(I_IDENT) TYPE  FRGCO OPTIONAL
		*"     VALUE(I_TSNAM) TYPE  TSNAM OPTIONAL
		*"     VALUE(I_ECPF) TYPE  Z22FECPF OPTIONAL
		*"  EXPORTING
		*"     VALUE(E_STATUS) TYPE  CHAR2
		*"     VALUE(E_DESCRICAO) TYPE  CHAR200
		*"  TABLES
		*"      T_ARQUIVO STRUCTURE  ZARQUIVO OPTIONAL
		*"----------------------------------------------------------------------
        */
		
		/*
 		fmeta.addInfo("I_IDENT",      JCO.TYPE_CHAR,  2,     0,              0, JCO.IMPORT_PARAMETER, 	null); // USERNAME
		fmeta.addInfo("I_TSNAM", 	  JCO.TYPE_CHAR,  20,    getOffset(12),  0, JCO.IMPORT_PARAMETER, 	null); // FILE ID
		fmeta.addInfo("E_STATUS", 	  JCO.TYPE_CHAR,  2,     getOffset(20),  0, JCO.EXPORT_PARAMETER, 	null); 
		fmeta.addInfo("E_DESCRICAO",  JCO.TYPE_CHAR,  200,   getOffset(2),   0, JCO.EXPORT_PARAMETER, 	null);
		fmeta.addInfo("T_ARQUIVO", 	  JCO.TYPE_TABLE, 144,   0,              0, JCO.OPTIONAL_PARAMETER, "T_ARQUIVO"); // FILE
//		fmeta.addInfo("T_CNAB", 	  JCO.TYPE_TABLE, 144,   0,              0, JCO.OPTIONAL_PARAMETER, "T_CNAB");		
		*/
		
 		fmeta.addInfo("I_IDENT",      JCO.TYPE_CHAR,  2,     -1,    0, JCO.IMPORT_PARAMETER, 	null); // USERNAME
		fmeta.addInfo("I_TSNAM", 	  JCO.TYPE_CHAR,  20,    -1,	0, JCO.IMPORT_PARAMETER, 	null); // FILE ID
		fmeta.addInfo("I_ECPF", 	  JCO.TYPE_CHAR,  100,   -1,	0, JCO.IMPORT_PARAMETER, 	null); // ECPF
		fmeta.addInfo("E_STATUS", 	  JCO.TYPE_CHAR,  2,     -1,  	0, JCO.EXPORT_PARAMETER, 	null); 
		fmeta.addInfo("E_DESCRICAO",  JCO.TYPE_CHAR,  200,   -1,   	0, JCO.EXPORT_PARAMETER, 	null);
		fmeta.addInfo("T_ARQUIVO", 	  JCO.TYPE_TABLE, 144,   -1,    0, JCO.OPTIONAL_PARAMETER, "T_ARQUIVO"); // FILE
		return fmeta;
	}
}
