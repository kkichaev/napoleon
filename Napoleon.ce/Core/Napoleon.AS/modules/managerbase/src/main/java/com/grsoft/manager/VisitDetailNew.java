package com.grsoft.manager;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class VisitDetailNew extends VisitDetail {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		View v = getLayoutInflater().inflate(R.layout.action_bar, null);
		TextView tv = (TextView) v.findViewById(R.id.tvTitle);
		tv.setText(getString(R.string.visit_doc_title));
		
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
	}
	
	@Override
	protected int getLayoutID() { return R.layout.visitdetail_new; }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.visitdetail_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itSync){
			sync();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
}
