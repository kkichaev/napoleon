package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.DispatchReturnsInfoImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class DispatchReturnRemark extends Activity {
	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, DispatchReturnRemark.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.remarkview);
		
		DispatchReturnsInfoImpl doc = new DispatchReturnsInfoImpl();
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		EditText ed = (EditText)findViewById(R.id.edNotes);
		ed.setText(doc.getData().remark);
		
		findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
