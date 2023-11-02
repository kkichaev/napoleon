package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.RfrItem;
import com.grsoft.dataobjects.RfrOut;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RfrOutImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RfrDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class RfrEdit extends BaseActivity implements SendResultListener {
	protected static final int EDIT_ITEM_TEXT = 0;
	RfrOutImpl doc = new RfrOutImpl();
	RfrItem editItem;
	Adapter adapter;
	
	public static void open(Context context, RfrOutImpl doc) {
		Intent i = new Intent(context, RfrEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rfr_edit);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		doc.read(rid);
		
		RfrOut r = doc.getData();
		
		OrgImpl o = new OrgImpl();
		Org org= o.getData();
		org.id = r.id;
		o.read();
		o.close();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(org.name);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { send(); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == EDIT_ITEM_TEXT ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("¬ведите примечание");
			
			EditText ed = new EditText(this);
			ed.setId(R.id.edRemark);
			ed.setInputType(InputType.TYPE_CLASS_TEXT);
			
			b.setView(ed);
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( !doc.isExported() ) {
						EditText ed = (EditText)((AlertDialog)dialog).findViewById(R.id.edRemark);
						editItem.text = ed.getText().toString();
						doc.write();
					}
				}
			});
			
			b.setNegativeButton(R.string.cancel, null);
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == EDIT_ITEM_TEXT && editItem != null ) {
			EditText ed = (EditText)dialog.findViewById(R.id.edRemark);
			ed.setText(editItem.text);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	protected void send() {
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), 
				RfrDoc.instance().getObjectName(), doc, doc.getRowid(), this);
		ds.execute((Void[])null);
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
	
	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid());
	}
	
	OnClickListener setChecked = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!doc.isExported()) {
				RfrItem i = (RfrItem) ((View)v.getParent()).getTag();
				i.setChecked(!i.isChecked());
				doc.write();
			}
			adapter.notifyDataSetChanged();
		}
	};
	
	OnLongClickListener setText = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			editItem = (RfrItem) ((View)v.getParent()).getTag();
			showDialog(EDIT_ITEM_TEXT);
			return false;
		}
	};
	
	
	class Adapter extends BaseAdapter {

		@Override public long getItemId(int arg0) { return arg0; }
		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(RfrEdit.this, R.layout.rfr_item, null);

			RfrItem item = (RfrItem)getItem(arg0);
			view.setTag(item);

			CheckBox cb = (CheckBox)view.findViewById(R.id.cbCheck);
			cb.setChecked(item.isChecked());
			if(!doc.isExported())
				cb.setOnClickListener(setChecked);
			else
				cb.setEnabled(false);
			
			TextView tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.name);
			tv.setOnLongClickListener(setText);
			return view;
		}
		
	}
}
