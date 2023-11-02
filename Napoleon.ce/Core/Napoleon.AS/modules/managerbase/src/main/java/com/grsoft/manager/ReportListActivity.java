package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AKBData;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderSum;
import com.grsoft.dataobjects.TopSel;
import com.grsoft.dataobjects.TopSelParam;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class ReportListActivity extends DrawerActivity implements OnClickListener {

	public static void open(Context context){
		Intent i = new Intent(context, ReportListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.leaders).setOnClickListener(this);
		findViewById(R.id.routes).setOnClickListener(this);
		findViewById(R.id.orders_sum).setOnClickListener(this);
		findViewById(R.id.akb).setOnClickListener(this);
	}
	
	@Override protected int getLayoutID() {	return R.layout.report_view; }

	@Override protected void postSyncUpdate() {}

	@Override protected String getActionBarTitle() {	return getString(R.string.reports);	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {	return true; }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.leaders)
			openLeadersReport(this);
		else if(id == R.id.routes)
			ActivityReport.open(this);
		else if (id == R.id.akb)
			openAkbReport(this);
		else if (id == R.id.orders_sum)
			openOrderSumReport(this);
	}

	private void openLeadersReport(final Context context) {
		List<Hitching> ret = new ArrayList<Hitching>();
		
		TopSelParam p = new TopSelParam();
		p.range = 31;
		
		ret.add(new ReportHitching("topsel", p, new Hitching(TopSel.class)));
		
		final Date d = new Date();
		Log.d("openLeadersReport", String.format("start: %s", Util.simpleDateFormat.format(d)));
		UpdateProcess upp = new UpdateProcess(this, new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) {}
			
			@Override
			public void onFinish(boolean success) {
				Log.d("openLeadersReport", String.format("finish: %d", 
						(new Date().getTime()  - d.getTime()) / 1000));
				if( success )
					LeaderReport.open(context);
			}
		}, ret);
		
		upp.execute((Void[]) null);
	}
	
	public static class Param extends DataObject{
		public String p = "";
	}
	
	private void openAkbReport(final Context context) {
		List<Hitching> ret = new ArrayList<Hitching>();
		
		Param dummy = new Param();
		
		ret.add(new ReportHitching("akbreport", dummy, new Hitching(AKBData.class)));
		
		UpdateProcess upp = new UpdateProcess(this, new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) {}
			
			@Override
			public void onFinish(boolean success) {
				if( success )
					AKBReport.open(context);
			}
		}, ret);
		
		upp.execute((Void[]) null);
	}
	
	private void openOrderSumReport(final Context context) {
		List<Hitching> ret = new ArrayList<Hitching>();
		
		Param dummy = new Param();
		
		ret.add(new ReportHitching("ordersum", dummy, new Hitching(OrderSum.class)));
		
		UpdateProcess upp = new UpdateProcess(this, new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) {}
			
			@Override
			public void onFinish(boolean success) {
				if( success )
					OrderSumReport.open(context);
			}
		}, ret);
		
		upp.execute((Void[]) null);
	}
}
