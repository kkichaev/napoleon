package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.view.BaseActivity;

public class Drivers extends BaseActivity {
	List<com.grsoft.dataobjects.Drivers> values = new ArrayList<com.grsoft.dataobjects.Drivers>();
	Adapter adapter;
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, Drivers.class);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.drivers);
		
		com.grsoft.dataobjects.Drivers data = new com.grsoft.dataobjects.Drivers();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(data.getClass());
		boolean bdo = r.select(data, table, null, "name");
		while(bdo) {
			values.add(data);
			data = new com.grsoft.dataobjects.Drivers();
			bdo = r.selectNext(data);
		}
		r.close();
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);
		registerForContextMenu(lv);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.call_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if( item.getItemId() == R.id.itCall) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			com.grsoft.dataobjects.Drivers driver = (com.grsoft.dataobjects.Drivers)adapter.getItem(menuInfo.position);
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel: %s", driver.phone)));
			startActivity(intent);			
		}
		return super.onContextItemSelected(item);
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return values.size(); }

		@Override public Object getItem(int arg0) { return values.get(arg0); }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(Drivers.this, R.layout.drivers_row, null);
			com.grsoft.dataobjects.Drivers driver = (com.grsoft.dataobjects.Drivers)getItem(arg0);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(driver.name);
			
			tv = (TextView)view.findViewById(R.id.tvPhone);
			tv.setText(driver.phone);
			
			view.setBackgroundResource((arg0 % 2 == 0) ? R.drawable.list_selector : R.drawable.even_row_selector);
			
			return view;
		}
		
	}
}
