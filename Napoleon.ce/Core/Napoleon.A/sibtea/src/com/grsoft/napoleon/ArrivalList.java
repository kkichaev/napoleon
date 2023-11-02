package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Arrival;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class ArrivalList extends BaseActivity {
	public static void open(Context context) {
		Intent i = new Intent(context, ArrivalList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.arrivallist);
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(new Adapter());
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Arrival doc = (Arrival)arg0.getAdapter().getItem(arg2);
				ArrivalDetail.open(ArrivalList.this, doc);
			}
		});
	}
	
	class Adapter extends BaseAdapter {
		
		List<Arrival> docs = new ArrayList<Arrival>();
		
		public Adapter() {
			String table = DataObjectInfo.getInstance().getTableName(Arrival.class);
			Arrival data = new Arrival();
			DbReader r = new DbReader();
			boolean bdo = r.select(data, table, "", "date");
			while( bdo ) {
				docs.add(data);
				data = new Arrival();
				bdo = r.selectNext(data);
			}
			r.close();
		}

		@Override public int getCount() { return docs.size(); }
		@Override public Object getItem(int arg0) { return docs.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(ArrivalList.this, R.layout.arrival_list_row, null);
			
			Arrival doc = (Arrival) getItem(pos);
			
			if( doc != null ) {
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvNumber);
				tv.setText(doc.number);
				
				tv = (TextView)view.findViewById(R.id.tvDate);
				tv.setText(Util.simpleDateFormat.format(doc.date));
			}
			return view;
		}
		
	}
}
