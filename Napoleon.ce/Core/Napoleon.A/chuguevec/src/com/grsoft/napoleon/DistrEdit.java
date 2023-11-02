package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DistrItem;
import com.grsoft.dataobjects.impl.DistrDocImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class DistrEdit extends BaseActivity {
	DistrDocImpl doc = new DistrDocImpl();
	
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
		
		((TextView) findViewById(R.id.tvOrg)).setText(org.getData().name);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new DistrItemAdapter());
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { send(); }
		});
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
		doc.close();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
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
				TextView tv;
				tv = (TextView) view.findViewById(R.id.tvNum);
				tv.setText(Integer.toString(di.number));
				
				tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(di.name);
				
				CheckBox cb = (CheckBox)view.findViewById(R.id.cbExists);
				cb.setChecked(di.exists > 0);
				
				if( !doc.isExported() ) {
					cb.setTag(di);
					cb.setOnClickListener(ce);
				} else
					cb.setEnabled(false);
			}
			return view;
		}
		
	}
}
