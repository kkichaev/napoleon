package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DistrGroupItem;
import com.grsoft.dataobjects.DistrItem;
import com.grsoft.dataobjects.DistribGroup;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.DistrDocImpl;
import com.grsoft.dataobjects.impl.DistribGroupImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class DistrEdit extends Activity {
	DistribGroupImpl group = new DistribGroupImpl();
	DistrDocImpl doc = new DistrDocImpl();
	HashSet<String> salled = new HashSet<String>();
	
	CheckExists ce = new CheckExists();
	
	static public void open(Context context, DistrDocImpl doc) {
		Intent i = new Intent(context, DistrEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.distr_edit);
		
		long rid = getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc.read(rid);

		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
		
		Date end = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		c.add(Calendar.MONTH, -1);
		DatePeriod dp = new DatePeriod(c.getTime(), end);
		DocList dl = OrderDoc.instance().docList(doc.getId(), "", dp);
		for(Document<?> doc : dl) {
			for(OrderItem oi : ((OrderImpl)doc).getData().items)
				salled.add(oi.id);
		}
		dl.close();
		
		((TextView) findViewById(R.id.tvOrg)).setText(org.getData().name);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new DistrItemAdapter());
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { send(); }
		});
	}

	@Override
	protected void onDestroy() {
		group.close();
		doc.close();
		super.onDestroy();
	}
	
	protected void send() {
		if( !doc.isExported() )
			doc.write();
		new DocumentSender(this, findViewById(R.id.btnSend), 
				DocType.getCurDoc().getObjectName(), doc, doc.getRowid()).execute((Void[])null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if( !doc.isExported() )
			doc.write();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// remove empty order
			if( doc.getData().items.size() == 0 )
				doc.delete();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	class CheckExists implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			DistrItem i = (DistrItem)v.getTag();
			i.exists = ((CheckBox)v).isChecked() ? 1 : 0;
		}
		
	}
	
	class DistrItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return doc.getData().items.size();
		}

		@Override
		public Object getItem(int position) {
			return doc.getData().items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(DistrEdit.this, R.layout.distr_row, null);
			
			DistrItem di = (DistrItem)getItem(position);
			if( di != null ) {
				DistribGroup dg = group.getData();
				dg.id = di.id;
				group.read();
				
				boolean haveSalled = haveSalled(dg);
				int color = haveSalled ? Color.GREEN : Color.BLACK;
				
				TextView tv;
				tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(dg.name);
				tv.setTextColor(color);
				
				CheckBox cb = (CheckBox)view.findViewById(R.id.cbExists);
				cb.setChecked(di.exists > 0);
				cb.setTextColor(color);
				
				if( !doc.isExported() ) {
					cb.setTag(di);
					cb.setOnClickListener(ce);
				} else
					cb.setEnabled(false);
			}
			return view;
		}
		
	}

	public boolean haveSalled(DistribGroup dg) {
		for(DistrGroupItem i : dg.items)
			if(salled.contains(i.id))
				return true;
		return false;
	}
}
