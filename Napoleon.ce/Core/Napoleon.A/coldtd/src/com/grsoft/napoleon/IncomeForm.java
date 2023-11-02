package com.grsoft.napoleon;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.IncomeItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.IncomeImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class IncomeForm extends BaseActivity {
	IncomeImpl doc = new IncomeImpl();
	PriceImpl price = new PriceImpl();
	
	public static void open(Context context, long docRowID) {
		Intent i = new Intent(context, IncomeForm.class);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, docRowID);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.income);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		price.setReadingFields("name");
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		Adapter adapter = new Adapter(doc.getData().items);
		lv.setAdapter(adapter);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		price.close();
		doc.close();
	}
	
	class Adapter extends BaseAdapter {
		List<IncomeItem> items;
		
		public Adapter(List<IncomeItem> items) { this.items = items; }
		
		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int position) { return position < getCount() ? items.get(position) : null; }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(IncomeForm.this, R.layout.income_row, null);
			IncomeItem i = (IncomeItem) getItem(position);
			
			if( i != null ) {
				Price p = price.getData();
				p.id = i.id;
				price.read();
				
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(p.name);

				String qtyText;
				String packName = "רע.";
				int scale = Consts.QTY_SCALE;
				int qty = i.qty;
				if( i.pack != 0 ) {
					int inPack = p.qtyInPack;
					if( inPack == 0 )
						inPack = Consts.QTY_SCALE;
					packName = getString(R.string.pack_lbl);
					qty = ((int)((long)i.qty * Consts.QTY_SCALE / inPack) + 50) / 100;
					scale = 10;
				}
				qtyText = Util.IntToScaleStr(qty, scale) + " " + packName;

				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setText(qtyText);
			}
			return view;
		}
		
	}
}
