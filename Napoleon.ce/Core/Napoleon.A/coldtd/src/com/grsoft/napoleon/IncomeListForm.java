package com.grsoft.napoleon;

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
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.impl.IncomeImpl;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class IncomeListForm extends BaseActivity {
	
	IncomeImpl doc = new IncomeImpl();
	List<Long> docs;
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, IncomeListForm.class);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incomelist);
		
		docs = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Income.class), "", "date desc");
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		Adapter adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( arg2 < docs.size() ) {
					IncomeForm.open(IncomeListForm.this, docs.get(arg2));
				}
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		doc.close();
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return docs.size(); }

		@Override
		public Object getItem(int arg0) {
			if( arg0 > getCount() )
				return null;
			
			doc.read(docs.get(arg0));
			return doc.getData();
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(IncomeListForm.this, R.layout.incomelist_row, null);
			Income i = (Income) getItem(arg0);
			
			if( i != null ) {
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvDate);
				tv.setText(Util.simpleDateFormat.format(i.date));

				tv = (TextView)view.findViewById(R.id.tvNumber);
				tv.setText(i.number);
			}
			return view;
		}
		
	}
}
