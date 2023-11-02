package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.Util;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

public abstract class DrawerActivity extends FragmentActivity implements OnDateSetListener {
	private DrawerHelper drawerHelper;
	private Date date = new Date();
	private SimpleDateFormat dayFmt = new SimpleDateFormat("dd");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutID());
		
		drawerHelper = DrawerHelper.getInstance();
        drawerHelper.onCreate(this);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        View v = getLayoutInflater().inflate(getActionBarLayoutID(), null);
        ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowCustomEnabled(true);
        a.setDisplayShowTitleEnabled(false);
        
        TextView tv = (TextView) v.findViewById(R.id.tvTitle);
        tv.setText(getActionBarTitle());
	}
	
	protected abstract int getLayoutID();
	protected abstract void postSyncUpdate();
	protected abstract String getActionBarTitle();

	protected int getActionBarLayoutID(){ return R.layout.action_bar; }
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
       drawerHelper.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerHelper.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
    	getMenuInflater().inflate(getOptionsMenuID(), menu);
    	
    	final MenuItem i = menu.findItem(R.id.itCal);
    	
    	if(i != null)
    		i.getActionView().setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { menu.performIdentifierAction(i.getItemId(), 0);	} });
    		
    	return true;
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		
		final MenuItem i = menu.findItem(R.id.itCal);
    	
    	if(i != null){
    		View v = i.getActionView();
    		if(v != null){
    			TextView tv = (TextView) v.findViewById(R.id.tvCurDate);
    			tv.setText(dayFmt.format(date));
    			result = true;
    		}
    	}
    	
		return result;
	}
	
	protected int getOptionsMenuID() { return R.menu.main_menu; }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = drawerHelper.onOptionsItemSelected(item);
         
        if(!result){	
	        if(item.getItemId() == R.id.itSync){
	        	doSync();
	        	result = true;
	        }else if (item.getItemId() == R.id.itCal){
	        	openCal();
	        	result = true;
	        }else
	        	result = super.onOptionsItemSelected(item);
        }
        
        return result;
    }
	
	private void openCal() {
    	DatePickerDialog dlg = new DatePickerDialog(this, this, date.getYear() + 1900, date.getMonth(), date.getDate());
    	dlg.show();
	}

	protected void initHitchings(List<Hitching> list){
		
	}
	
	protected void setSending(List<ObjectListener> toSend) {
		
	}
	
	protected void doSync() {
		GPSChecker.check(this, new Runnable() {
			
			@Override
			public void run() {
				List<Hitching> ret = new ArrayList<Hitching>();
				initHitchings(ret);
				
				List<ObjectListener> send = new ArrayList<ObjectListener>();
				setSending(send);
				
				UpdateProcess upp = new UpdateProcess(DrawerActivity.this, new UpdateCtrl() {
					@Override public void updateCtrl(boolean enabled) {}
					
					@Override
					public void onFinish(boolean success) {
						if( success )
							postSyncUpdate();
					}
				}, ret);
				upp.setSending(send);
				upp.execute((Void[]) null);
			}
		});		
	}
	
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
		date = new Date(year - 1900, monthOfYear, dayOfMonth);
		invalidateOptionsMenu();
	}
	
	public Date getDate(){ return Util.resetTime(date); }
	public void setDate( Date value){ date = value; }
	
	@Override
	protected void onResume() {
		super.onResume();
		drawerHelper.closeDrawer();
	}
}
