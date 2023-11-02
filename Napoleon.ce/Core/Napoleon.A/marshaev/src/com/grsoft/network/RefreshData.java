package com.grsoft.network;

import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.napoleon.util.ProgID;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.ProgressManager;

public class RefreshData extends NetworkAsyncTask {

	public interface Handler {
		void onRead(boolean result);
	}
	
	Context context;
	Handler handler;
	List<Hitching> data;
	
	public RefreshData(Context context, Handler handler, List<Hitching> data) {
		super(new ProgressManager(context));
		
		this.context = context;
		this.handler = handler;
		this.data = data;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean result = false;
		
		String errMsg = "";
		ReadServiceBase rs = new ReadService(data);
		rs.setUpdateProcessListenet(this);
		try {
			ServerCommand.DeviceID = ProgID.getPrgID(context);
			
			rs.update(context, new LoginData("", "", "", context), false);			
			progressHelper.onUpdate(new ProgressValue(UpdateStatus.END_OF_PROCESS, 0));
			result = true;
		} catch (RuntimeException e) {
			errMsg = e.getMessage();
			showErrorMsg(errMsg, context);
			e.printStackTrace();
		}

		if( handler != null )
			handler.onRead(result);		
		return result;
	}
	
}
