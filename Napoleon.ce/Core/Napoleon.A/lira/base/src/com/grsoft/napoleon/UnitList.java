package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.RegDurationActivity;

public class UnitList extends RegDurationActivity {
	
	OrderImpl doc = new OrderImpl();
	OrgImpl org = new OrgImpl();
	
	public static void open(Context ctx, OrderImpl doc) {
		Intent i = new Intent(ctx, UnitList.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		ctx.startActivity(i);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.unit_list);
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		doc.read(rowid);
		org.getData().id = doc.getId();
		org.read();
		
		ListView units = (ListView)findViewById(R.id.lvUnits);
		units.setAdapter(new UnitsAdapter((OrgEx)org.getData()));
		units.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override public void onItemClick(AdapterView<?> parent, View view, int pos, long rowid) {
				((OrderEx)doc.getData()).unitCode = (int)rowid;
				doc.write();
				doc.close();
				finish();
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	class UnitsAdapter extends BaseAdapter {
		
		OrgEx org;
		public UnitsAdapter(OrgEx org) { this.org = org; }

		@Override public int getCount() { return (org.units == null) ? 0 : org.units.size(); }

		@Override public Object getItem(int position) { return (org.units == null) ? null : org.units.get(position); }

		@Override public long getItemId(int position) { return (org.units == null) ? 0 : org.units.get(position).id;  }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( org.units == null )
				return null;
			
			if( convertView == null )
				convertView = View.inflate(UnitList.this, R.layout.unit_row, null);
			
			TextView tv = (TextView)convertView.findViewById(R.id.tvName);
			tv.setText(org.units.get(position).name);
			
			convertView.setBackgroundResource(position % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);
			return convertView;
		}
		
	}
}
