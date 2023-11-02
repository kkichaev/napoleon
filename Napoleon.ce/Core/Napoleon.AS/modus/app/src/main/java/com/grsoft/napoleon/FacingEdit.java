package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.FacingImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.FacingDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


public class FacingEdit extends Activity implements SendResultListener {
	private EditText edCount;
	private EditText edRemark;
	private TextView tvOrgName;
	private FacingImpl doc = new FacingImpl();
	private View btnSend;
	private View btnOK;
	
	public static void open(Context context, long rowid){
		Intent i = new Intent(context, FacingEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.facing);
		edCount = (EditText) findViewById(R.id.edCount);
		tvOrgName = (TextView) findViewById(R.id.tvOrgName);
		btnSend = findViewById(R.id.btnSend);
		edRemark = (EditText) findViewById(R.id.edRemark);
		btnOK = findViewById(R.id.btnOK);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		tvOrgName.setText(org.getData().name);
		
		edCount.setInputType(InputType.TYPE_NULL);
		edCount.setText(Util.IntToScaleStr(doc.getData().qty, Consts.QTY_SCALE));
		new KeypadHelper(this, R.id.edCount, false);
		
		btnSend.setOnClickListener(sendClick);
		btnOK.setOnClickListener(okClick);
		edRemark.setText(doc.getData().remark);
		edCount.selectAll();
	}
	
	private OnClickListener okClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			save();
			finish();
		}
	};
	
	private OnClickListener sendClick = new OnClickListener() {
		@Override public void onClick(View v) {
			save();
			send(); 
		}
	};
	
	private void save() {
		if(doc.isEditable()){
			doc.getData().qty = Util.StrToScale(edCount.getText().toString().trim(), Consts.QTY_SCALE);
			doc.getData().remark = edRemark.getText().toString().trim();
			doc.write();
			doc.close();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		save();
	}

	protected void send() {	new DocumentSender(this, btnSend, FacingDoc.OBJ_NAME, doc, doc.getRowid(), this).execute((Void[])null);	}

	@Override
	public void postSendExecute(boolean result) {
		if( result ){
			doc.read(doc.getRowid(), false);
			finish();
		}
	};
}
