package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RestockItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RestockImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RestockDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.AskForSend;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class RestockDetail extends BaseActivity implements SendResultListener, DataSetNotify {
	boolean inited = true;
	RestockImpl doc;
	PriceImpl price = new PriceImpl();
	RestockItem changedItem;
	
	Adapter adapter;
	
	public static void open(Context context, RestockImpl doc) {
		Intent i = new Intent(context, RestockDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.restock_detail);
		
		doc = new RestockImpl();
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc.read(rid);
	
		adapter = new Adapter(); 
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setDividerHeight(0);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
		
		findViewById(R.id.btnAddItems).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { addItem(); }
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if( doc.isEditable() )
			getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		RestockItem orderItem = (RestockItem)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		
		if (item.getItemId() == R.id.itDelete)
			deleteItem(orderItem);
		else if (item.getItemId() == R.id.itEdit)
			editItem(orderItem);
		
		return super.onContextItemSelected(item);
	}
	
	protected void addItem() {
		DocType.setCurDoc(RestockDoc.instance());
		Warehouse.open(this, doc, true);
	}

	private void editItem(RestockItem orderItem) {
		PriceImpl pi = new PriceImpl();
		pi.getData().id = orderItem.id;
		if( pi.read() )
			doc.editItem(pi.getRowid(), this);
		pi.close();
		adapter.notifyDataSetChanged();
	}

	private void deleteItem(RestockItem orderItem) {
		PriceImpl pi = new PriceImpl();
		pi.getData().id = orderItem.id;
		if( pi.read() )
			doc.updateQty(pi, 0, 0, false);
		pi.close();
		adapter.notifyDataSetChanged();
	}

	protected void send() {
		AskForSend.askSend(this, new DocumentSender(this, findViewById(R.id.btnSend), RestockDoc.instance().getObjectName(),
				doc, doc.getRowid(), this));
//		new DocumentSender(this, findViewById(R.id.btnSend), RestockDoc.instance().getObjectName(),
//				doc, doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( inited )
			inited = false;
		else {
			doc.read(doc.getRowid(), false);
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		price.close();
	}
	
	ChangeQty changeQty = new ChangeQty();
	
	class ChangeQty extends InputNumber implements OnClickListener {
		
		public ChangeQty() {}

		@Override
		public void onClick(View view) {
			if(doc.isExported())
				return;
			
			changedItem = (RestockItem) ((View)view.getParent()).getTag();
			
			InputNumberDlg.open(view.getContext(), this, Consts.QTY_SCALE, true, "Введите количество");
		}

		@Override
		public void applayInput(int value, Object... params) {
			changedItem.qty = value;
			doc.write();
			adapter.notifyDataSetChanged();
		}

		@Override
		public int getValue() {
			return changedItem.qty;
		}
		
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }

		@Override
		public Object getItem(int arg0) {
			return arg0 < getCount() ? doc.getData().items.get(arg0) : null;
		}

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(RestockDetail.this, R.layout.restock_row, null);
			
			view.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);

			RestockItem item = (RestockItem) getItem(pos);
			if( item != null ) {
				view.setTag(item);
				Price prc = price.getData();
				
				TextView tv;
				String text;

				text = "нет";
				if(item.id.length() != 0) {
					prc.id = item.id;
					if(price.read())
						text = prc.name;
					else
						text = "<Код товара'"+ item.id + "'>";
				}
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(text);
				
				text = Util.IntToScaleStr(item.sold, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvSold);
				tv.setText(text);

				text = Util.IntToScaleStr(prc.qty, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvRest);
				tv.setText(text);

				text = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setOnClickListener(changeQty);
				tv.setText(text);
			}
			return view;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}

	@Override
	public void notifyDataSetChanged() {
		if( adapter != null )
			adapter.notifyDataSetChanged();
	}
}
