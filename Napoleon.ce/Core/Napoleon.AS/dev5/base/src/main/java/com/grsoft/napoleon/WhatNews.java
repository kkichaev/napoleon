package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class WhatNews extends Activity {
	public static Class<? extends Activity> activity = WhatNews.class;
	
	public static void open(Context context){
		Intent intent = new Intent(context, activity);
		context.startActivity(intent);
	}
}
