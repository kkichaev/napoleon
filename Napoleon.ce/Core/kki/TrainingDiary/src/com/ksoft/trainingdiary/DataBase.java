package com.ksoft.trainingdiary;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.ksoft.ksoftlib.ui.BaseAppCompatActivity;

public class DataBase extends BaseAppCompatActivity {
	
	public static void open(Context context){
		Intent i = new Intent(context, DataBase.class);
		context.startActivity(i);
	}

	@Override
	public int getLayoutID() { return R.layout.database;}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.database, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

}
