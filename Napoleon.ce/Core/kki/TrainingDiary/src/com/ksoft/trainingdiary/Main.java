package com.ksoft.trainingdiary;

import com.ksoft.ksoftlib.ui.BaseAppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Main extends BaseAppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {
			return true;
		}
		
		if(id == R.id.itDatabase){
			DataBase.open(this);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public int getLayoutID() { return R.layout.main; }
}
