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


public class DTaskEdit extends Activity {
	private DTaskImpl doc = new DTaskImpl();
	private TextView tvDispRemark;
	private EditText edVal;
	private View btnOk;
	private View btnCancel;
	
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
		btnCancel = findViewById(R.id.btnCancel);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		tvDispRemark.setText(doc.getData().disprem);
		edVal.setText(doc.getData().remark);
		
		if(doc.isEditable())
			btnOk.setOnClickListener(okClick);
		
		btnCancel.setOnClickListener(cancelClick);
	}
	
	private OnClickListener okClick = new OnClickListener() {
		@Override public void onClick(View v) {
			String sval = edVal.getText().toString().trim();
			
			if(sval.length() != 0){
				doc.getData().remark = sval;
				doc.write();
			}else
				doc.delete();
			
			doc.close();
			finish();
		}
	};
	
	private OnClickListener cancelClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(doc.getData().remark.trim().length() == 0){
				doc.delete();
				doc.close();
			}
			
			finish();
		}
	};
}
