package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class NapoleonEx extends Napoleon {
	public static HashMap<String, Integer> debtOrgs = null;
	ToggleButton tbFilter;
	
	void loadSet(HashMap<String, Integer> map, Date checkDate) {
		map.clear();
		
		DbWriter.checkDBTable(Delivery.class);
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT id, sum(sumd) FROM " + table + " WHERE paydate <= ? and sumD <> 0 group by id";		
		String[] args = { Long.toString(checkDate.getTime()) };
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, args);
			while( c.moveToNext() )
				map.put(c.getString(0), c.getInt(1));
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		table = DataObjectInfo.getInstance().getTableName(Payment.class);
		sql = "SELECT DISTINCT id, sum FROM " + table + " WHERE date <= ? and sum > 0";
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, args);
			while( c.moveToNext() ) {
				String id = c.getString(0);
				Integer sum = map.get(id);
				int paySum = c.getInt(1);
				if( sum == null )
					map.put(id, paySum);
				else
					map.put(id, sum + paySum);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tbFilter = (ToggleButton) findViewById(R.id.tbFilter);
		
		tbFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mainOrgsAdapter.refresh();
				mainOrgsAdapter.notifyDataSetChanged();
			}
		});
	}
	@Override
	protected void onResume() {
		Date d = new Date();
		if(debtOrgs == null){
			debtOrgs = new HashMap<String, Integer>();
			loadSet(debtOrgs, d);
		}
		
		super.onResume();
	}
	
	@Override
	protected void setOrgBackground(int pos, OrgImpl org, View v) {
		if( org == null ) {
			super.setOrgBackground(pos, org, v);
			return;
		}
		
		String id = org.getData().id;
		
		if(debtOrgs.containsKey(id) && debtOrgs.get(id) > 0) 
			v.setBackgroundResource(R.drawable.red_row);
		else  
			super.setOrgBackground(pos, org, v);
	}
	
	StringBuilder stringBuilder = new StringBuilder();
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		if(DocType.getCurDoc().equals(DebtDocEx.instance())){
			int docsum = 0;
			if (os != null && os.read())
				docsum = os.getData().sum;
			
			int debt = 0;
			
			String id = oi.getData().id;
			if(debtOrgs.containsKey(id))
				debt = debtOrgs.get(id);
			
			stringBuilder.setLength(0);
			stringBuilder.append(Util.IntToScaleStr(docsum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			stringBuilder.append("<br>");
			stringBuilder.append(Util.IntToScaleStr(debt, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			((TextView)view.findViewById(R.id.tvOrgSum)).setText(Html.fromHtml(stringBuilder.toString()));
		}
	}
	
	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}
	
	@Override
	protected MainOrgsAdapter getMainOrgAdapter()
			throws IllegalAccessException, InstantiationException {
		return new MainOrgsAdapter(this){
			@Override
			public void refresh() {
				if(tbFilter.isChecked())
					cursor.setCondition("id in (select id from org_sums where type='"+ 
							DocType.getCurDoc().getName() + "' and sum > 0)");
				else
					cursor.setCondition("");
				
				super.refresh();
			}
		};
	}
	
	@Override
	protected FindOnClickListener createFindOnClickListener() {
		return new FindOnClickListener(edFind, lvMainOrgs, llFind){
			@Override
			public void onClick(View v) {
				tbFilter.setChecked(false);
				super.onClick(v);
			}
			
			@Override
			protected void postOnClick(boolean shown) {
				super.postOnClick(shown);
				tbFilter.setEnabled(shown);
			}
		};
	}
}
