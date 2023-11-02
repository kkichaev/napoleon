package com.grsoft.napoleon.utl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.FormReport;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RawObject;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.exception.RuntimeException;

public class ServerFormReader {
	
	static String REPORT_NAME = "getprintform";

	static FormWorker worker = null;
	public static void getForm(Context context, DataObject data, Object signal, StringBuilder result) {
		if(worker == null) {
			worker = new FormWorker(signal, data, context, result);
			worker.start();
		}		
	}
	
	static void done() {
		worker = null;
	}
	
}

class FormWorker extends Thread {
	LoginData connection;
	Context context;
	Object signal;
	DataObject data;
	StringBuilder result;
	
	public FormWorker(Object signal, DataObject data, Context context, StringBuilder result) {
		this.signal = signal;
		this.data = data;
		this.result = result;

		Config config = ConfigManager.getConfig();
		connection = new LoginData(config.login, config.passw, config.impersonate, context);
		this.context = context;
	}
	
	@Override
	public void run() {
		try {
			List<Hitching> hitchings = new ArrayList<Hitching>();
			hitchings.add(new ReportHitching(ServerFormReader.REPORT_NAME, data, new ResHitching(result)) );
			ReadServiceBase rsb = RWServiceFactory.instance.createReadService(hitchings);
			rsb.update(context, connection, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ServerFormReader.done();
			synchronized (signal) {
				signal.notifyAll();				
			}
		}
	}
}

class ResHitching extends Hitching {
	StringBuilder result;
	
	public ResHitching(StringBuilder result) {
		super(FormReport.class, "FormReport");
		this.result = result;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		FormReport data = (FormReport) rawObject.createDataObject(dataObject);
		
		result.delete(0, result.length());
		result.append(data.form);
	}
}
