package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.TargetImpl;
import com.grsoft.napoleon.documents.TargetDoc;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class TargetEdit extends Activity implements OnClickListener {
	TargetImpl document = (TargetImpl) TargetDoc.instance().create();
	EditText edRemark;
	View btnOK;

	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, TargetEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.targetedit);
	
		edRemark = (EditText) findViewById(R.id.edRemark);
		btnOK = findViewById(R.id.btnOK);
		
		btnOK.setOnClickListener(this);
		
		document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		document.close();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) {
			String t = edRemark.getText().toString().trim();
			
			if (t.length() > 0) {
				document.getData().remark = t;
				document.write();
				document.close();
				finish();
			}
		}else {
			finish();
		}
	}
	
	@Override public void onBackPressed() {}
}
