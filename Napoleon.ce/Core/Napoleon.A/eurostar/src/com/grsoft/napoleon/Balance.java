package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class Balance extends Activity {
	public static void open(Context context){
		Intent intent = new Intent(context, Balance.class);
		context.startActivity(intent);
	}
}
