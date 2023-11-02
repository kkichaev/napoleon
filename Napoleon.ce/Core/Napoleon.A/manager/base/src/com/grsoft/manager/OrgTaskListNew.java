package com.grsoft.manager;

import java.util.Date;

import com.grsoft.util.ExtrasConst;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;

public class OrgTaskListNew extends OrgTaskList implements OnClickListener {
	public static void open(Context context, String id, String userId) {
		Intent i = new Intent(context, OrgTaskListNew.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, id);
		i.putExtra(ExtrasConst.USER_ID_STR, userId);
		context.startActivity(i);
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvFrom).setOnClickListener(this);
		findViewById(R.id.tvTill).setOnClickListener(this);
		
		View v = getLayoutInflater().inflate(R.layout.org_task_list_action_bar, null);
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.org_task_list_menu, menu);
		return true;
	}
	
	@Override protected int getLayoutID() { return R.layout.org_task_list_new; }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if(id == R.id.itAdd){
			editItem(null);
			return true;
		}else if (id == R.id.itSync){
			refreshTask();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

	private OnDateSetListener dateFromSelected = new OnDateSetListener() {
		@Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dateFrom = new Date(year - 1900, monthOfYear, dayOfMonth);
			refreshDate();
		}};
	
	private OnDateSetListener dateTillSelected = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dateTill = new Date(year - 1900, monthOfYear, dayOfMonth);
			refreshDate();
		}};
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.tvFrom){
			DatePickerDialog dlg = new DatePickerDialog(this, dateFromSelected, dateFrom.getYear() + 1900, dateFrom.getMonth(), dateFrom.getDate());
	    	dlg.show();
		}else if (id == R.id.tvTill){
			DatePickerDialog dlg = new DatePickerDialog(this, dateTillSelected, dateTill.getYear() + 1900, dateTill.getMonth(), dateTill.getDate());
	    	dlg.show();
		}
	}
}
