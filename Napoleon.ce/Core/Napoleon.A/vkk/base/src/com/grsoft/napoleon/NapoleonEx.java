package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.DatePeriod;

public class NapoleonEx extends Napoleon {
	protected static final int SET_ORG_FILTER = 100;
	HashSet<String> ylwOrgs = new HashSet<String>();
	HashSet<String> redOrgs = new HashSet<String>();
	Adapter adapter;
	
	@Override
	protected int getResourceID() {
		return R.layout.napoleonex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { showDialog(SET_ORG_FILTER); }
		});
	}

	private void blockNapoleon() {
		Date now = new Date();
		SharedPreferences sp = getSharedPreferences(UpdateDBEx.PREF_NAME, Context.MODE_PRIVATE);
		Date lastSync = new Date(sp.getLong(UpdateDBEx.LAST_SYNC, 0));
		
		int maxDayBlock = 1;
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		if(cfg.getValue(sb, "Ѕлокировать„ерезƒней")){
			try{
				maxDayBlock = Integer.parseInt(sb.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		if(Math.abs(DatePeriod.daysDiff(now, lastSync)) >= maxDayBlock )
			UpdateDBEx.openBlocked(this);
	}
	
	@Override
	protected MainOrgsAdapter getMainOrgAdapter() throws IllegalAccessException, InstantiationException {
		if( adapter == null )
			adapter = new Adapter(this);
		return adapter;
	}
	
	void filterOrgs(int choice) {
		if( listViewMode == ListViewMode.ORG_LIST && adapter != null)
			adapter.setFilter(choice);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == SET_ORG_FILTER ) {
			CharSequence[] choice = new CharSequence[] { "все", "синие", "красные" }; 
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("‘ильтр торговых точек");
			b.setItems(choice, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { filterOrgs(which); }
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		blockNapoleon();
		
		ylwOrgs.clear();
		redOrgs.clear();		
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long redValue = c.getTime().getTime();
		long ylwValue = redValue + 3l * 24 * 3600 * 1000;
		
		Date minDate = new Date(71, 0, 1);
		
		try {
			String table = DataObjectInfo.getInstance().getTableName(Payment.class);
			String sql = "select id, min(payDate) as paydate from [" + table + "] where payDate < " + Long.toString(ylwValue) + 
					" and payDate > " + Long.toString(minDate.getTime()) + " and sum > 0 group by id";
			Cursor cursor = DataBaseManager.getDataBase().rawQuery(sql, null);
			while(cursor.moveToNext()) {
				String id = cursor.getString(0);
				long val = cursor.getLong(1);
				if( val < redValue)
					redOrgs.add(id);
				else
					ylwOrgs.add(id);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName); 
		String id = oi.getData().id;
		if( redOrgs.contains(id))
			tv.setTextColor(Color.RED);
		else if( ylwOrgs.contains(id) )
			tv.setTextColor(Color.BLUE);
	}

	class Adapter extends MainOrgsAdapter {
		
		int currentMode = 0;
		ArrayList<Long> filtred = new ArrayList<Long>();
		OrgImpl org = new OrgImpl();

		public Adapter(Context context) throws IllegalAccessException, InstantiationException {
			super(context);
		}

		public void setFilter(int choice) {
			if( currentMode != choice ) {
				currentMode = choice;
				updateItems();
				notifyDataSetChanged();
			}
		}
		
		@Override
		public void close() {
			org.close();
			super.close();
		}
		
		@Override
		public int getCount() {
			return (currentMode == 0 ) ? super.getCount() : filtred.size();
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if( currentMode == 0 )
				return super.getView(arg0, arg1, arg2);
			
			long pos = filtred.get(arg0);
			org.read(pos);
			return getMainView(org, arg0, arg1, arg2);
		}
		
		@Override
		public void applyFilter(String value) {
			super.applyFilter(value);
			if( currentMode != 0 )
				updateItems();
		}
		
		@Override
		public void resetFilter() {
			super.resetFilter();
			if( currentMode != 0 )
				updateItems();
		}
		
		@Override
		public void refresh() {
			super.refresh();
			if( currentMode != 0 )
				updateItems();
		}

		private void updateItems() {
			filtred.clear();
			for( int i=0; i<cursor.getCount(); i++ ) {
				OrgImpl oi = (OrgImpl)cursor.get(i);
				String id = oi.getData().id;
				if( (currentMode == 1 && ylwOrgs.contains(id)) || ((currentMode == 2 || currentMode == 1) && redOrgs.contains(id)) )
					filtred.add(oi.getRowid());
			}
		}
		
	}
}

