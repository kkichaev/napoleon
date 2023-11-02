package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import com.grsoft.chat.ChatService;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.UpdateProcess.Params;


public class NapoleonChat extends ChatService {
	public static Class<NapoleonChat> service = NapoleonChat.class;
	
	public static void init(Context context){
		Intent intent = new Intent(context, service);
		context.startService(intent);
	}
	
	@Override
	protected void setUserInfo(Params p) {
		Config c = ConfigManager.getConfig();
		p.ip1 = c.address;
		p.ip2 = c.address2;
		p.port1 = c.port;
		p.port2 = c.port2;
		p.login = c.login;
		p.pass = c.passw;
		p.duration = LoginData.readDuration(this);
	}
}
