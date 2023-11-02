package com.grsoft.napoleon;

import com.grsoft.dataobjects.Claim;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ClaimImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.ClaimDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClaimEdit extends BaseActivity implements SendResultListener {
	
	ClaimImpl doc;
	
	public static void open(Context context, ClaimImpl doc) {
		Intent i = new Intent(context, ClaimEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.claim_edit);
	
		doc = new ClaimImpl();
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rowid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rowid);
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = doc.getId();
		oi.read();
		oi.close();
		
		TextView tv = (TextView)findViewById(R.id.tvOrgName);
		tv.setText(o.name);
		
		final EditText ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(doc.getData().remark);
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send();}
		});

		
	}
	
	protected void send() {
		if(doc.isEditable()) {
			EditText ed = (EditText)findViewById(R.id.edRemark);
			String text = ed.getText().toString();
			if(text.length() == 0) {
				Toast.makeText(this, "Не могу отправить пустой документ", Toast.LENGTH_SHORT).show();
				return;
			}
			Claim d = doc.getData();
			d.remark = text;
			doc.write();
		}
		
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), ClaimDoc.instance().getObjectName(), 
				doc, doc.getRowid(), this);
		ds.execute((Void[])null);
	}

	@Override
	public void onBackPressed() {
		if(doc.isEditable()) {
			EditText ed = (EditText)findViewById(R.id.edRemark);
			Claim d = doc.getData();
			String text = ed.getText().toString();
			
			if(text.length() == 0) {
				doc.delete();
			} else {
				d.remark = text;
				doc.write();
			}
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);		
	}
}
