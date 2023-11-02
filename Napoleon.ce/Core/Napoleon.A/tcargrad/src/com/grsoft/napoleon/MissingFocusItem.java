package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FocusedItemsTCImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class MissingFocusItem extends BaseActivity {
	
	long rid;
	OrderImplBase<? extends Order> doc = null;
	Adapter adapter = null;
	
	public static void open(Context ctx, OrderImplBase<? extends Order> doc) {
		Intent i = new Intent(ctx, MissingFocusItem.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.missing_focus);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState; 
		rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc = (OrderImplBase<? extends Order>) OrderDoc.instance().create();
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Item item = (Item)adapter.getItem(arg2);
				if( item != null )
					doc.editItem(item.rid, MissingFocusItem.this);
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(rid, false);
		adapter.refresh();
	}
	
	class Adapter extends BaseAdapter {
		
		ArrayList<Item> items = new ArrayList<Item>();
		
		public void refresh() {
			HashSet<String> recommend = new HashSet<String>();
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = doc.getId();
			oi.read();

			FocusedItemsTCImpl.loadItems(recommend, o.orgType, true);

			if( doc.getData().items != null )
				for(OrderItem i : doc.getData().items ) {
					if(recommend.contains(i.id) )
						recommend.remove(i.id);
				}
			
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			items.clear();
			
			for(String i : recommend) {
				p.id = i;
				if( pi.read() ) {
					Item item = new Item();
					item.name = p.name;
					item.rid = pi.getRowid();
					
					items.add(item);
				}
			}
			
			pi.close();
			
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return items.size(); }

		@Override
		public Object getItem(int arg0) {
			return (arg0 < items.size()) ? items.get(arg0) : null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(MissingFocusItem.this, R.layout.missing_item, null);
			
			Item item = (Item)getItem(pos);
			
			if( item != null ) {
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(item.name);
			}
			return view;
		}
		
	}
}

class Item {
	long rid;
	String name;
}
