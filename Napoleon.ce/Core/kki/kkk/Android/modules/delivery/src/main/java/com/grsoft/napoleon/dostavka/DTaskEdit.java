package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DTaskImpl;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


public class DTaskEdit extends Activity implements RejectAction {
	private DTaskImpl doc = new DTaskImpl();
	private TextView tvDispRemark;
	private EditText edVal;
	private View btnOk;
	private View btnReject;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, DTaskEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dtaskedit);
		
		tvDispRemark = (TextView) findViewById(R.id.tvDispRemark);
		edVal = (EditText) findViewById(R.id.edVal);
		btnOk = findViewById(R.id.btnOK);
		btnReject = findViewById(R.id.btnReject);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		tvDispRemark.setText(doc.getData().disprem);
		edVal.setText(doc.getData().remark);
		
		if(doc.isEditable())
			btnOk.setOnClickListener(okClick);
		
		btnReject.setOnClickListener(rejectClick);
	}
	
	private OnClickListener okClick = new OnClickListener() {
		@Override public void onClick(View v) {
			String sval = edVal.getText().toString().trim();
			
			if(sval.length() != 0){
				doc.getData().remark = sval;
				doc.setReadyToSend();
				doc.write();
			}else
				doc.delete();
			
			doc.close();
			finish();
		}
	};
	
	private OnClickListener rejectClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new RejectDialog().show(getFragmentManager(), RejectDialog.class.toString());
		}
	};

	@Override
	public void doReject(String remark) {
		doc.getData().remark = remark;
		doc.setRejected();
		doc.setReadyToSend();
		doc.write();
		doc.close();
	}
}
