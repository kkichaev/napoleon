package com.grsoft.napoleon.dostavka;

import com.grsoft.chat.Chat;
import com.grsoft.chat.ChatEx;
import com.grsoft.chat.ChatService;
import com.grsoft.database.ChatRcvHitching;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.ChatData;
import com.grsoft.dataobjects.ChatDataEx;
import com.grsoft.dataobjects.ChatGroup;
import com.grsoft.dataobjects.ChatUser;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.LoginData;
import com.grsoft.network.UpdateProcess.Params;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MichelChat extends ChatService {
	public static Class<MichelChat> service = MichelChat.class;
	
	public static void init(Context context){
		ChatRcvHitching.OBJECT_NAME = "ChatQueryAgent";
		Chat.activity = ChatEx.class;
		
		DbObject.regNewDataType(ChatData.class, ChatDataEx.class);
		Intent intent = new Intent(context, service);
		context.startService(intent);
	}
	
	@Override
	protected void setUserInfo(Params p) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		p.ip1 = pref.getString(getString(R.string.ip1_pref), "");
		p.ip2 = pref.getString(getString(R.string.ip2_pref), "");
		p.port1 =  Integer.parseInt(pref.getString(getString(R.string.port_pref),getString(R.string.def_port_val)));
		p.login = pref.getString(getString(R.string.login_pref), "");
		p.pass = pref.getString(getString(R.string.pass_pref), "");
		
//		if(BuildConfig.DEBUG){
//			p.ip1 = "192.168.0.100";
//			p.login = "2";
//			p.pass = "2";
//		}
		
		p.duration = LoginData.readDuration(this);
	}
	
	@Override
	protected void inputHitching(Params p) {
		super.inputHitching(p);
		
		p.indata.add((new Hitching(ChatGroup.class)));
		p.indata.add(new Hitching(ChatUser.class));
	}
}
