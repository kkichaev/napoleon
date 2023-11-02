package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.RestOut;
import com.grsoft.dataobjects.RestOutItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RestOutImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.view.BaseActivity;

public class RestOutEdit extends BaseActivity {
	protected static final int EDIT_REMARK = 7;
	RestOutImpl doc;
	Folder folder = new Folder();
	DbReader reader = new DbReader();
	Adapter adapter;
	
	EditText edRemark;
	
	public static void open(Context ctx, RestOutImpl doc) {
		Intent i = new Intent(ctx, RestOutEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rest_out_edit);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc = new RestOutImpl();
		doc.read(rid);
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = doc.getId();
		oi.read();
		oi.close();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(o.name);
		
		ListView lv;
		lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		findViewById(R.id.btnRemark).setOnClickListener(new View.OnClickListener() {
			
			@Override public void onClick(View arg0) { showDialog(EDIT_REMARK); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == EDIT_REMARK) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("¬ведите комментарий по за€вке");
			View v = View.inflate(this, R.layout.rest_out_remark, null);
			edRemark = (EditText) v.findViewById(R.id.edRemark);
			b.setView(v);
			
			b.setNegativeButton(android.R.string.cancel, null);
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if(doc.isEditable()) {
						doc.getData().remark = edRemark.getText().toString();
						doc.write();
					}
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == EDIT_REMARK) {
			edRemark.setText(doc.getData().remark);
			return;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	protected void send() {
		new DocumentSender(RestOutEdit.this, findViewById(R.id.btnSend), 
				DocType.getCurDoc().getObjectName(), doc, 
				doc.getRowid()).execute((Void[])null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		doc.close();
		reader.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	class Adapter extends BaseAdapter {

		@Override
		public int getCount() { return doc.getData().items.size(); }

		@Override
		public Object getItem(int position) {
			RestOut ro = doc.getData();
			if(ro.items.size() > position)
				return ro.items.get(position);
			return null;
		}

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(RestOutEdit.this, R.layout.rest_out_row, null);
			
			TextView tv;
			RestOutItem ri = (RestOutItem)getItem(position);
			if( ri != null ) {
				boolean res = reader.select(folder, DataObjectInfo.getInstance().getTableName(Folder.class), "fid=?", null, new String[] { ri.id }, false);
				if( res ) {
					tv = (TextView)view.findViewById(R.id.tvName);
					tv.setText(folder.name);
				}
				
				tv = (TextView)view.findViewById(R.id.tvPlan);
				tv.setText(Integer.toString(ri.plan));

				tv = (TextView)view.findViewById(R.id.tvQty);
				tv.setText(Integer.toString(ri.qty));
				tv.setOnClickListener(new QtyInput(ri));

				tv = (TextView)view.findViewById(R.id.tvOrder);
				tv.setText(Integer.toString(ri.order));
				tv.setOnClickListener(new OrderInput(ri));
			}
			return view;
		}
	}
	
	class QtyInput implements View.OnClickListener {
		RestOutItem ri;
		
		public QtyInput(RestOutItem ri) { this.ri = ri; }

		@Override
		public void onClick(View v) {
			if(doc.isExported() == false) { 
				InputNumberDlg.open(RestOutEdit.this, new InputNumber() {
					@Override public int getValue() { return ri.qty; }
					
					@Override
					public void applayInput(int value, Object... params) {
						ri.qty = value;
						doc.write();
						adapter.notifyDataSetChanged();
					}
				}, 1, true, "¬ведите наличие");
			}
		}
	}

	class OrderInput implements View.OnClickListener {
		RestOutItem ri;
		
		public OrderInput(RestOutItem ri) { this.ri = ri; }

		@Override
		public void onClick(View v) {
			if(doc.isExported() == false) { 
				InputNumberDlg.open(RestOutEdit.this, new InputNumber() {
					@Override public int getValue() { return ri.order; }
					
					@Override
					public void applayInput(int value, Object... params) {
						ri.order = value;
						doc.write();
						adapter.notifyDataSetChanged();
					}
				}, 1, true, "¬ведите заказ");
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK){
				save();
				finish();
				return true;
			}else
				return super.onKeyDown(keyCode, event);
	}

	private void save() {
		// TODO Auto-generated method stub
		
	}
}
