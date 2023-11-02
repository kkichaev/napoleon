package com.grsoft.napoleon;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.ItemsAuditItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ItemsAuditImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ItemsAuditDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.view.BaseActivity;

public class ItemsAuditDetail extends BaseActivity implements SendResultListener {
	
	ItemsAuditImpl doc = new ItemsAuditImpl();
	PriceImpl price = new PriceImpl();
	Adapter adapter;
	
	public static void open(Context context, ItemsAuditImpl doc) {
		Intent i = new Intent(context, ItemsAuditDetail.class);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.items_audit);
	
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));

		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
				
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.getData().name);
		
		final ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter(doc.getData().items);
		lv.setAdapter(adapter);
		
//		lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				View v = arg1.findViewById(R.id.cbRepr);
//				if(v != null)
//					v.requestFocus();
//			}
//
//			@Override public void onNothingSelected(AdapterView<?> arg0) {}
//		});
	
		lv.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if( arg2.getAction() == KeyEvent.ACTION_DOWN && arg2.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
					ItemsAuditItem item = (ItemsAuditItem) lv.getSelectedItem();
					if( item != null ) {
						int value = ( item.repr != 0 ) ? 0 : 1;
						item.repr = value;
						item.pack = value;
						item.block = value;
						item.price = value;

						doc.write();
						adapter.notifyDataSetChanged();
					}
				}
				return false;
			}
		});
		
		View btnSend = findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override public void onClick(View v) { send(); }
		});
	
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbRepr);
		cb.setOnClickListener( new CBFormHandler() {
			@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.repr = value; }
		});

		cb = (CheckBox)findViewById(R.id.cbPack);
		cb.setOnClickListener( new CBFormHandler() {
			@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.pack = value; }
		});

		cb = (CheckBox)findViewById(R.id.cbBlock);
		cb.setOnClickListener( new CBFormHandler() {
			@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.block = value; }
		});

		cb = (CheckBox)findViewById(R.id.cbPrice);
		cb.setOnClickListener( new CBFormHandler() {
			@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.price = value; }
		});
		
		cb = (CheckBox)findViewById(R.id.cbOrderDone);
		cb.setChecked(doc.getData().orderCreated != 0);
		cb.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				doc.getData().orderCreated = (isChecked) ? 1 : 0;
				doc.write();
			}
		});
	}

	void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), ItemsAuditDoc.instance().getObjectName(), 
				doc, doc.getRowid()).execute((Void[])null);
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
		List<ItemsAuditItem> items;
		
		public Adapter(List<ItemsAuditItem> items) { this.items = items; }
		
		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int position) { return position < getCount() ? items.get(position) : null; }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(ItemsAuditDetail.this, R.layout.items_audit_row, null);
			ItemsAuditItem i = (ItemsAuditItem) getItem(position);
			
			if( i != null ) {
				Price p = price.getData();
				p.id = i.id;
				price.read();
				
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(p.name);
				
				CheckBox cb;
				cb = (CheckBox)view.findViewById(R.id.cbRepr);
				cb.setTag(i);
				cb.setChecked(i.repr != 0);
				cb.setOnClickListener( new CBItemHandler() {
					@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.repr = value; }
				});

				cb = (CheckBox)view.findViewById(R.id.cbPack);
				cb.setTag(i);
				cb.setChecked(i.pack != 0);
				cb.setOnClickListener( new CBItemHandler() {
					@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.pack = value; }
				});

				cb = (CheckBox)view.findViewById(R.id.cbBlock);
				cb.setTag(i);
				cb.setChecked(i.block != 0);
				cb.setOnClickListener( new CBItemHandler() {
					@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.block = value; }
				});

				cb = (CheckBox)view.findViewById(R.id.cbPrice);
				cb.setTag(i);
				cb.setChecked(i.price != 0);
				cb.setOnClickListener( new CBItemHandler() {
					@Override  protected void setItem(ItemsAuditItem ii, int value) { ii.price = value; }
				});
			}
			return view;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		if( doc.isEditable() && (doc.getData().items == null || doc.getData().items.size() == 0) )
			doc.delete();
		super.onBackPressed();
	}
	
	class CBFormHandler implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(doc.isEditable() == false)
				return;

			int value = ((CheckBox)v).isChecked() ? 1 : 0;
			for(ItemsAuditItem ii : doc.getData().items)
				setItem(ii, value);
			doc.write();
			adapter.notifyDataSetChanged();
		}
		
		protected void setItem(ItemsAuditItem ii, int value) { }
	}
	
	class CBItemHandler implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(doc.isEditable() == false)
				return;
			
			ItemsAuditItem ii = (ItemsAuditItem)v.getTag();
			setItem(ii, ((CheckBox)v).isChecked() ? 1 : 0);
			doc.write();
//			adapter.notifyDataSetChanged();
		}
		
		protected void setItem(ItemsAuditItem ii, int value) { }
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
}
