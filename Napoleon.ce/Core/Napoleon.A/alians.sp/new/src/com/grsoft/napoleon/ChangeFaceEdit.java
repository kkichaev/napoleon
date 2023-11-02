package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ChangeFaceEdit extends Activity {
	public static void open(Context context) {
		Intent i = new Intent(context, NewOrgEdit.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.changeface);
	}
}
