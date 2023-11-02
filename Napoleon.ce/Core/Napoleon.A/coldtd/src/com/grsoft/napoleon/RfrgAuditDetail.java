package com.grsoft.napoleon;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Rfrg;
import com.grsoft.dataobjects.RfrgAudit;
import com.grsoft.dataobjects.RfrgAuditItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RfrgAuditImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RfrgAuditDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.rfid.Rfid;
import com.grsoft.napoleon.rfid.RfidHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.view.BaseActivity;

public class RfrgAuditDetail extends BaseActivity implements SendResultListener {
	private static final int WAIT_SCANNING = 0;
	RfrgAuditImpl doc = new RfrgAuditImpl();
	Adapter adapter;
	
	//boolean enableRfid = false;
	
	public static void open(Context context, RfrgAuditImpl doc) {
		Intent i = new Intent(context, RfrgAuditDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rfrg_detail);
	
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));

		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
				
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.getData().name);
		
		EditText ed = (EditText)findViewById(R.id.edExclusive);
		ed.setText(Integer.toString(doc.getData().exclusive));
		ed.selectAll();
		ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(View arg0, boolean arg1) { ((EditText)arg0).selectAll(); }
		});
		
		ed.setFilters(new InputFilter[]{ new InputMinMax(0, 100)});
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter(doc.getData().items);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if( arg2 < doc.getData().items.size() )
					RfrgItemEdit.open(RfrgAuditDetail.this, doc, arg2);
			}
		});
		
		View btnSend = findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override public void onClick(View v) { send(); }
		});
		
		findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { RfrgItemEdit.open(RfrgAuditDetail.this, doc, -1); }
		});
	}
	
	class InputMinMax implements InputFilter {

	    private int min, max;

	    public InputMinMax(int min, int max) {
	        this.min = min;
	        this.max = max;
	    }

	    @Override
	    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {   
	        try {
	            // Remove the string out of destination that is to be replaced
	            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
	            // Add the new string in
	            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
	            int input = Integer.parseInt(newVal);
	            if (isInRange(min, max, input))
	                return null;
	        } catch (NumberFormatException nfe) { }
	        return "";
	    }

	    private boolean isInRange(int a, int b, int c) {
	        return b > a ? c >= a && c <= b : c >= b && c <= a;
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( keyCode == 222 ) {
			if( !RfidHelper.isScanning() ) {
				showDialog(WAIT_SCANNING);
				RfidHelper.startScanning(new RfidHelper.Handler() {
					@Override public void recievedRFID(String rfid) { checkRfid(rfid); } }, this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void checkRfid(String rfid) {
		if( !doc.isEditable() )
			return;
		
		Rfrg rfrg = new Rfrg();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(rfrg.getClass());
		String where = "rfid like '%|" + rfid + "|%'";
		if( r.select(rfrg, table, where) ) {
			checkOrAddItem(rfrg, rfid);
		}
		r.close();
	}

	void notifyNewRfid() {
		doc.write();
		runOnUiThread(new Runnable() {
			@Override public void run() {
				Toast.makeText(RfrgAuditDetail.this, "Ќайдена метка", Toast.LENGTH_SHORT).show();
				adapter.notifyDataSetChanged(); 
			}
		});
	}
	
	private void checkOrAddItem(Rfrg rfrg, String rfid) {
		RfrgAudit d = doc.getData();
		for(RfrgAuditItem item : d.items) {
			if( item.doc_id.equals(rfrg.id) || item.fact_id.equals(rfrg.id) ) {
				if( item.fact_id.length() == 0 ) {
					item.fact_id = rfrg.id;
					item.fact_rfid = rfid;
					item.model = rfrg.model;
					
					notifyNewRfid();
				}
				return;
			}
		}
		
		RfrgAuditItem newItem = new RfrgAuditItem();
		newItem.fact_id = rfrg.id;
		newItem.fact_rfid = rfid;
		newItem.model = rfrg.model;
		d.items.add(newItem);

		notifyNewRfid();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == WAIT_SCANNING ) {
			return RfidHelper.createWaitDialog(this);
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		adapter.refresh(doc.getData().items);

//		enableRfid = false;
//		if( doc.isEditable() ) {
//			enableRfid = (Rfid.getRfid() != null);
//		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (doc.isEditable())
			getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public void onBackPressed() {		
		try {
			saveDoc();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		super.onBackPressed();
	}

	private void saveDoc() {
		if( doc.isEditable() ) {
			EditText ed = (EditText)findViewById(R.id.edExclusive);
			doc.getData().exclusive = Integer.parseInt(ed.getText().toString());
			doc.write();
			RfrgAuditDoc.instance().refreshDocSum(doc.getId());
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		RfrgAuditItem ri = (RfrgAuditItem) adapter.getItem(pos);
		switch(item.getItemId()) {
		case R.id.itDelete:
			// удал€ем только созданные строки
			if( ri.doc_id.length() == 0 && doc.isEditable() ) {
				List<RfrgAuditItem> items = doc.getData().items;
				doc.write();
				items.remove(pos);
				adapter.refresh(items);
			}
			break;
		case R.id.itEdit:
			RfrgItemEdit.open(RfrgAuditDetail.this, doc, pos);
			break;
		}
		return super.onContextItemSelected(item);
	}

	void send() {
		saveDoc();
		new DocumentSender(this, findViewById(R.id.btnSend), RfrgAuditDoc.instance().getObjectName(), 
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
		
		doc.close();
		Rfid.close();
	}

	class Adapter extends BaseAdapter {
		List<RfrgAuditItem> items;
		
		public Adapter(List<RfrgAuditItem> items) { this.items = items; }
		
		public void refresh(List<RfrgAuditItem> items) { 
			this.items = items;
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return items.size(); }

		@Override public Object getItem(int position) { return position < getCount() ? items.get(position) : null; }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(RfrgAuditDetail.this, R.layout.rfrg_detail_row, null);
			
			RfrgAuditItem item = (RfrgAuditItem) getItem(position);
			if( item != null ) {
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvDocId);
				tv.setText(item.doc_id);

				tv = (TextView)view.findViewById(R.id.tvFactId);
				tv.setText(item.fact_id);
				
				view.setBackgroundResource(item.doc_id.equals(item.fact_id) ? R.drawable.list_selector : R.drawable.orange_row);
			}
			return view;
		}
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
}
