package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.WhPrice;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PriceList extends Activity {
	
	protected ArrayList<WhPrice> price = new ArrayList<WhPrice>();
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, PriceList.class);		
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.price_list);
		
		WhPrice p = new WhPrice();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(WhPrice.class);
		
		boolean bdo = r.select(p, table, "", "name");
		while( bdo ) {
			price.add(p);
			p = new WhPrice();
			bdo = r.selectNext(p);
		}
		r.close();
		
		ListView lv = (ListView)findViewById(android.R.id.list); 
		lv.setAdapter(new PriceAdapter());
	}
	class PriceAdapter extends BaseAdapter {
	
		@Override
		public int getCount() {
			return price.size();
		}
	
		@Override
		public Object getItem(int position) {
			return (position < price.size()) ? price.get(position) : null;
		}
	
		@Override
		public long getItemId(int position) {
			return position;
		}
	
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			view = View.inflate(PriceList.this, R.layout.price_list_row, null);
			
			WhPrice wp = (WhPrice)getItem(position);
			if( wp != null ) {
				TextView tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(wp.name);
				
				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setText(Util.IntToScaleStr(wp.qty, Consts.QTY_SCALE, Util.DEC_DELIM, false));
			}
			view.setBackgroundResource(position % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);		
			return view;
		}
		
	}
}