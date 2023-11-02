package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;

public class ManagerConfigurationNew extends ManagerConfiguration{
	
	public static void open(Context context){
		Intent i = new Intent(context, ManagerConfigurationNew.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}
	
	@Override protected int getLayoutID() { return R.layout.config_new;	}
	
	@Override protected String getActionBarTitle() { return getString(R.string.action_settings); }
}
