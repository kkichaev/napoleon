package com.grsoft.napoleon;

import com.grsoft.dataobjects.InvEquItem;
import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class InvEquRemark extends Activity implements OnClickListener {
	InvEquImpl doc = new InvEquImpl();
	InvEquItem item = null;
	
	EditText edRemark;
	public static void open(Context context, long rowid, String id) {
		Intent intent = new Intent(context, InvEquRemark.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ExtrasConst.ORG_ID_STR, id);
		
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.invequremark);
		edRemark = (EditText) findViewById(R.id.edRemark); 
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		item = doc.findItem(getIntent().getStringExtra(ExtrasConst.ORG_ID_STR));
		
		if (item != null) {
			TextView tv = (TextView) findViewById(R.id.tvBarcode);
			tv.setText(item.barcode);
			edRemark.setText(item.remark);
		}else 
			finish();
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) {
			item.remark = edRemark.getText().toString().trim();
			doc.write();
			doc.close();
			
			finish();
		}else if (v.getId() == R.id.btnCancel) 
			finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing())
			doc.close();
	}
}
