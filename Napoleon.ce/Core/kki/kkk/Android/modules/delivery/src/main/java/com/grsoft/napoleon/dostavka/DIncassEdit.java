package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.DIncass;
import com.grsoft.dataobjects.impl.DIncassImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


public class DIncassEdit extends Activity implements RejectAction{
	private DIncassImpl doc = new DIncassImpl();
	private TextView tvDispRemark;
	private EditText edVal;
	private View btnOk;
	private View btnReject;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, DIncassEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dincassedit);
		
		tvDispRemark = (TextView) findViewById(R.id.tvDispRemark);
		edVal = (EditText) findViewById(R.id.edVal);
		btnOk = findViewById(R.id.btnOK);
		btnReject = findViewById(R.id.btnReject);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		DIncass idoc = doc.getData();
		tvDispRemark.setText(idoc.disprem);
		
		if(idoc.sum != 0)
			edVal.setText(Util.IntToScaleStr(idoc.sum, Consts.SUM_SCALE));
		
		((EditText)findViewById(R.id.edRemark)).setText(idoc.remark);
		
		if(doc.isEditable())
			btnOk.setOnClickListener(okClick);
		
		btnReject.setOnClickListener(rejectClick);
	}
	
	private OnClickListener okClick = new OnClickListener() {
		@Override public void onClick(View v) {
			String sval = edVal.getText().toString().trim();
			int val = Util.StrToScale(sval, Consts.SUM_SCALE);
			String remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
			
			if(val != 0 || remark.length() > 0){
				DIncass idoc = doc.getData();
				idoc.sum = val;
				idoc.remark = remark;
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
		doc.write();
		doc.close();
	}
}
