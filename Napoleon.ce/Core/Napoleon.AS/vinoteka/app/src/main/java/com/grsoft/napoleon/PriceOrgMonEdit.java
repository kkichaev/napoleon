package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceOrgMonImpl;
import com.grsoft.dataobjects.PriceOrgMonItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PriceOrgMonDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PriceOrgMonEdit extends BaseActivity implements SendResultListener {
	protected ImageButton btnSend;
	PriceOrgMonImpl doc;
	PriceImpl price = new PriceImpl();
	
	public static void open(Context context, PriceOrgMonImpl doc) {
		Intent i = new Intent(context, PriceOrgMonEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.price_mon_org);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc = new PriceOrgMonImpl();
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
		
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.getData().name);
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				send();
			}
		});
		
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setDividerHeight(0);
		lv.setAdapter(new Adapter());
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final PriceOrgMonItem i = (PriceOrgMonItem)arg0.getAdapter().getItem(arg2);
				InputNumberDlg.open(arg0.getContext(), new InputNumber() {
					
					@Override public int getValue() { return i.cost; }
					
					@Override
					public void applayInput(int value, Object... params) {
						if( doc.isEditable() ) {
							i.cost = value;
							doc.write();
						((BaseAdapter)arg0.getAdapter()).notifyDataSetChanged();
						}
					}
				}, Consts.SUM_SCALE, false, getString(R.string.input_cost));
			}
		});
	}
	
	protected void send() {
		new DocumentSender(PriceOrgMonEdit.this, btnSend, PriceOrgMonDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onDestroy() {
		doc.close();
		price.close();
		super.onDestroy();
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(PriceOrgMonEdit.this, R.layout.price_org_mon_item, null);
			PriceOrgMonItem i = (PriceOrgMonItem)getItem(arg0);
			if( i != null ) {
				Price p = price.getData();
				p.id = i.id;
				price.read();
				
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(p.name);				
				
				tv = (TextView)view.findViewById(R.id.tvCost);
				tv.setText(Util.IntToScaleStr(i.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			view.setBackgroundResource(arg0 % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);
			return view;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
}
