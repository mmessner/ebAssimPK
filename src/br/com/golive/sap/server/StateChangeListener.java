package br.com.golive.sap.server;

import org.apache.log4j.Logger;

import br.com.golive.log.LogUtil;

import com.sap.mw.jco.*;
import com.sap.mw.jco.JCO.Server;

public class StateChangeListener implements JCO.ServerStateChangedListener {
	private static Logger log = LogUtil.getLogInstance(StateChangeListener.class.getName());

	public void serverStateChangeOccurred(Server server, int oldState, int newState) {
		String str = "Alterando status do servidor... De: [";

	    if ((oldState & JCO.STATE_STOPPED    ) != 0) str += " STOP ";
	    if ((oldState & JCO.STATE_STARTED    ) != 0) str += " START ";
	    if ((oldState & JCO.STATE_LISTENING  ) != 0) str += " LIST ";
	    if ((oldState & JCO.STATE_TRANSACTION) != 0) str += " TRANSACT ";
	    if ((oldState & JCO.STATE_BUSY       ) != 0) str += " BUS ";

	    str += "] para [";
	    if ((newState & JCO.STATE_STOPPED    ) != 0) str += " STOP ";
	    if ((newState & JCO.STATE_STARTED    ) != 0) str += " START ";
	    if ((newState & JCO.STATE_LISTENING  ) != 0) str += " LIST ";
	    if ((newState & JCO.STATE_TRANSACTION) != 0) str += " TRANSAC ";
	    if ((newState & JCO.STATE_BUSY       ) != 0) str += " BUS ";

	    str += "]";

	    log.warn(str);
	}
}
