package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Arrival;
import com.grsoft.dataobjects.ArrivalItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ArrivalImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class ArrivalDetail extends BaseActivity {
	
	ArrivalImpl doc = new ArrivalImpl();
	PriceImpl price = new PriceImpl();
	
	static final String NUMBER_TAG = "numberTag";
	
	public static void open(Context context, Arrival doc) {
		Intent i = new Intent(context, ArrivalDetail.class);
		i.putExtra(NUMBER_TAG, doc.number);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.arrival_detail);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		String number = b.getString(NUMBER_TAG);
		Arrival a = doc.getData();
		a.number = number;
		doc.read();
		doc.close();
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		price.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(NUMBER_TAG, doc.getData().number);
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int position) { return doc.getData().items.get(position); }
		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int pos, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(ArrivalDetail.this, R.layout.arrival_detail_row, null);
			
			ArrivalItem item = (ArrivalItem) getItem(pos);
			
			if( item != null ) {
				TextView tv;
				
				Price prc = price.getData();
				prc.id = item.id;
				price.read();
				
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(prc.name);
				
				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			}
			return view;
		}
		
	}
}
