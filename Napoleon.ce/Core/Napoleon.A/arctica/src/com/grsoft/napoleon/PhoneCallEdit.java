package com.grsoft.napoleon;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PhoneCallImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PhoneCallEdit extends BaseActivity {
	PhoneCallImpl doc;
	OrgEx oe;
	
	public static void open(Context context, PhoneCallImpl doc) {
		Intent i = new Intent(context, PhoneCallEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_call_edit);
		
		doc = new PhoneCallImpl();
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		long rc = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rc);
		
		OrgImpl oi = new OrgImpl();
		oe = (OrgEx) oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();
		
		TextView tv = (TextView)findViewById(R.id.tvOrgName);
		tv.setText(oe.name);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter());
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { finish(); }
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(doc.isEditable()) {
					EditText ed = (EditText)findViewById(R.id.edRemark);
					doc.getData().remark = ed.getText().toString();
					doc.addAction();
				}
				finish();
				
			}
		});
		
		EditText ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(doc.getData().remark);
		
		findViewById(R.id.btnOrder).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doc.addAction();
				
				DocType.setCurDoc(OrderDoc.instance());
				OrderImpl oi = new OrderImpl();
				oi.initSilent(oe.id, GPSUtilNew.getLastKnownLocation());
				CreateOrder.createByPhone(PhoneCallEdit.this, oi);
				oi.close();
				finish();
			}
		});
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Contact c = (Contact) arg0.getAdapter().getItem(arg2);
				if(c.phone.length() > 0) {
					doc.addAction();
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel: %s", c.phone)));
					PhoneCallEdit.this.startActivity(intent);
				}
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(doc.isEditable() && doc.isEmpty())
			doc.delete();
		doc.close();
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return oe.contacts.size(); }
		@Override public Object getItem(int arg0) { return oe.contacts.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(PhoneCallEdit.this, R.layout.phone_call_row, null);
			TextView tv;
			
			Contact c = (Contact) getItem(arg0);
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(c.name);
			
			tv = (TextView)view.findViewById(R.id.tvPhone);
			tv.setText(c.phone);

			return view;
		}
		
	}
}
