package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReturnPriceList extends BaseActivity {

	ReturnImplEx doc = new ReturnImplEx();
//	DeliveryImpl dlv = new DeliveryImpl();
	long rid;
	Delivery d;
	boolean started = false;
	PriceImpl price = new PriceImpl();
	
	public static void open(Context ctx, ReturnImplEx r) {
		Intent i = new Intent(ctx, ReturnPriceList.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, r.getRowid());
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.return_price_list);

		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		doc.read(rid);
		doc.close();
		
//		d = dlv.getData();
//		d.id = doc.getId();
//		d.number = ((ReturnEx)doc.getData()).dlvNum;
//		dlv.read();
//		dlv.close();

		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter());
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View arg1, int pos, long arg3) {
				PriceImpl pi = (PriceImpl)list.getAdapter().getItem(pos);
				if( pi != null) {
					ReturnPriceCount.open(ReturnPriceList.this, pi.getRowid(), doc);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
		if( started ) {
			doc.read(rid, false);
			doc.close();

			ListView lv = (ListView)findViewById(R.id.lvItems);
			((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
		} else
			started = true;		
	}
	
	@Override
	protected void onStop() {
		price.close();
		super.onStop();
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return d.items == null ? 0 : d.items.size(); }

		@Override
		public Object getItem(int position) { 
			price.getData().id = d.items.get(position).id;
			if( price.read() )
				return price;
			return null;
		}

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PriceImpl pi = (PriceImpl) getItem(position);
			String name;
			int color = Color.BLACK;
			if( pi == null )
				name = "товар с кодом <" + d.items.get(position).id + ">";
			else {
				Price prc = pi.getData();
				name = prc.name;
				color = ( doc.findItem(prc.id) == null) ? Color.BLACK : Color.GREEN;
			}
			if( convertView == null )
				convertView = View.inflate(ReturnPriceList.this, R.layout.return_row, null);
			
			TextView tv = (TextView)convertView.findViewById(R.id.tvItem);
			tv.setText(name);
			tv.setTextColor(color);
			
			return convertView;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if( doc.getData().items.size() == 0 )
				doc.delete();
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
