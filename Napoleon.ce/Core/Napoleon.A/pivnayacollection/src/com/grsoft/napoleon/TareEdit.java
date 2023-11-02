package com.grsoft.napoleon;

import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.Inventory;
import com.grsoft.dataobjects.TareItem;
import com.grsoft.dataobjects.impl.TareImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.TareDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TareEdit extends BaseActivity implements SendResultListener {
	TareImpl doc;
	
	Adapter adapter;
	
	public static void open(Context context, TareImpl doc) {
		Intent i = new Intent(context, TareEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tare_edit);
		doc = new TareImpl();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rid);
		
		adapter = new Adapter(doc.getData().items);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
	}
	
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), TareDoc.instance().getObjectName(),
				doc, doc.getRowid(), this).execute((Void[])null); 
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	View.OnClickListener setQty = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final TareItem item = (TareItem)v.getTag();
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override public boolean useComma() { return false; }
				@Override public boolean replaceCommaToPlus() { return false; }
				
				@Override
				public void applayInput(int value, Object... params) {
					if(doc.isEditable()) {
						item.fact = value;
						doc.write();
						adapter.notifyDataSetChanged();
					}
				}

				@Override
				public int getValue() {	return item.fact;}
			});
			
		}
	};
	
	class Adapter extends BaseAdapter {
		List<TareItem> items;
		Map<String, Inventory> invMaps;
		
		public Adapter(List<TareItem> items) {
			this.items = items;
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null)
				arg1 = View.inflate(TareEdit.this, R.layout.tare_edit_row, null);

			if(invMaps == null)
				invMaps = Inventory.get();
			
			TareItem i = (TareItem)getItem(arg0);
			TextView tv;

			
			Inventory inv = invMaps.get(i.id);
			tv = (TextView)arg1.findViewById(R.id.tvName);
			tv.setText(inv == null ? i.id: inv.name);
			
			tv = (TextView)arg1.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));

			tv = (TextView)arg1.findViewById(R.id.tvFact);
			tv.setText(Util.IntToScaleStr(i.fact, Consts.QTY_SCALE));
			tv.setTag(i);
			tv.setOnClickListener(setQty);
			
			int diff = i.fact - i.qty ;
			tv = (TextView)arg1.findViewById(R.id.tvDiff);
			tv.setText(Util.IntToScaleStr(diff, Consts.QTY_SCALE));
			tv.setTextColor(diff == 0 ? Color.BLACK : Color.RED);

			return arg1;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);		
	}
	
	@Override
	public void onBackPressed() {
		TareDoc.instance().refreshDocSum(doc.getId());
		super.onBackPressed();
	}
}
