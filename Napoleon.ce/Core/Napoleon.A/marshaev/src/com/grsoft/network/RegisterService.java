package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.grsoft.dataobjects.CurrentAgent;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ProgID;
import com.grsoft.network.exception.RuntimeException;

public class RegisterService extends NetworkAsyncTask {
	
	public interface Handler {
		void onCompleete(boolean result);
	}
	
	Context context;
	CfgNplEx config;
	Handler handler;
	
	public RegisterService(Context context, CfgNplEx config, Handler handler) {
		super(new SendProgressManager(context, null));
		
		this.context = context;
		this.config = config;
		this.handler = handler;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		
		boolean result = false;
		
		PutAgent pa = new PutAgent(context);
		List<PutAgent> snd = new ArrayList<PutAgent>();
		snd.add(pa);

		String errMsg = "";
		WriteServiceBase ws = new WriteService(snd, false);
		ws.setUpdateProcessListenet(this);
		if( ws.write(context, new LoginData("admin", config.admPwd, "", context)) ) {
			result = pa.getSuccess();
		} else {
			errMsg = ws.getMessage();
			showErrorMsg(errMsg, context);
		}
		
		handler.onCompleete(result);
		return result;
	}
}

class PutAgent implements ObjectExportListener {

	CurrentAgent agent;
	boolean success = false;
	
	public PutAgent(Context context) { 
		agent = new CurrentAgent(); 

		String id = ProgID.getPrgID(context);
		agent.progid = id;
		agent.id = id;
		agent.name = id;
	}
	
	@Override
	public void onStart() {}
	
	public boolean getSuccess() { return success; } 

	@Override public void onRead(RawObject rawObject) throws RuntimeException {  }

	@Override public void onSave() { }
	@Override public void onEnd() { success = true; }
	@Override public String getObjectName() { return "Agents"; }
	@Override public int size() { return 1; }
	@Override public DataObject get(int i) { return i == 0 ? agent : null; }
	
}
