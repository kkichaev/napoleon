package com.grsoft.napoleon.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DisabledFirmObject;
import com.grsoft.network.LoginData;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RawObject;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.exception.RuntimeException;

public class DisabledFirms {
	public interface Handler {
		void firmsLoaded(HashSet<String> disabledFirms);
		void error(String message);
	}
	
	static DSWorker instance = null;
	public static void loadDisabledFirms(Handler handler, Context context) {
		if( instance == null ) {
			instance = new DSWorker(handler, context);
			instance.start();
		}
	}
	
	static void done() {
		instance = null;
	}
}

class DSWorker extends Thread {
	DisabledFirms.Handler handler;
	LoginData connection;
	Context context;
	
	public DSWorker(DisabledFirms.Handler handler, Context context) {
		this.handler = handler;

		Config config = ConfigManager.getConfig();
		connection = new LoginData(config.login, config.passw, config.impersonate, context);
		this.context = context;
	}
	
	@Override
	public void run() {
		try {
			RcvDisabledFirms rcvr = new RcvDisabledFirms();
			List<Hitching> hitchings = new ArrayList<Hitching>();
			hitchings.add(rcvr);
			ReadServiceBase rsb = RWServiceFactory.instance.createReadService(hitchings);

			if( !rsb.update(context, connection, false) )
				handler.error(rsb.getMessage());
			else
				handler.firmsLoaded(rcvr.getFirms());
		} catch (Exception e) {
			e.printStackTrace();
			
			handler.error(e.getMessage());
		} finally {
			DisabledFirms.done();
		}
	}
}

class RcvDisabledFirms extends Hitching {
	HashSet<String> firms = new HashSet<String>();
	
	public RcvDisabledFirms() {
		super(DisabledFirmObject.class, "DisabledFirms");
	}
	
	@Override public void onStart() { }
	
	@Override public void onEnd() {}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DisabledFirmObject dobj = (DisabledFirmObject) rawObject.createDataObject(dataObject);
		firms.add(dobj.id);
	}
	
	HashSet<String> getFirms() { return firms; }
}