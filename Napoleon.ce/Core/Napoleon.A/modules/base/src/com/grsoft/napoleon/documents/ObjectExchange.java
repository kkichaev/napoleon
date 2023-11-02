package com.grsoft.napoleon.documents;

import android.content.Context;
import android.view.View;

import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ExchangeService;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UserInfo;

public class ObjectExchange extends NetworkAsyncTask {
	
	public interface ObjectSendedHandler {
		void sended(DbObject<?> object, String response, int result);
	}
	
	public static final String WRITE_OBJECTS = "Write";
	public static final String OBJECTS_COMMAND = "Object Command";
	public static final String SERV_RESPONSE = "^response";
	public static final String SERV_RESULT = "^result";

	public static final int RESULT_FAIL   = 0; // документ не сохранен
	public static final int RESULT_SAVE   = 1; // документ сохранен
	public static final int RESULT_COMMIT = 2; // документ проведен. Изменения не возможны

	
	DbObject<?> object;
	String objName;
	String command;
	ObjectSendedHandler handler;
	Context context;
	public ObjectExchange(Context context, View control, String objName, String command, DbObject<?> object, ObjectSendedHandler handler) {
		super(new SendProgressManager(context, control));
		
		this.object = object;
		this.objName = objName;
		this.handler = handler;
		this.command = command;
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			Config config = ConfigManager.getConfig();
			UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, context);
			
			ExchangeService es = new ExchangeService(context,
					object.getData(), objName, command);
			es.doExchange(userInfo, (CfgNplW)config, this);
			if( handler != null )
				handler.sended(object, es.getResponse(), es.getResult());
			
		} catch(Exception e) {
			e.printStackTrace();
			if( handler != null )
				handler.sended(null, context.getString(R.string.processing_error), -1);
		}
		return null;
	}

}
