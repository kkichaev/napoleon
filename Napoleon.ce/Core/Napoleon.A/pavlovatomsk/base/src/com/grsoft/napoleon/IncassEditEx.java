package com.grsoft.napoleon;

import com.grsoft.dataobjects.IncassEx;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class IncassEditEx extends IncassEdit {
	private EditText edDover;
	
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		edDover = (EditText) findViewById(R.id.edDover);
		edDover.setText(((IncassEx)doc.getData()).dover);
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		((IncassEx)doc.getData()).dover = edDover.getText().toString().trim();
	}
	
	@Override
	public void onBackPressed() {
		if (doc.isEditable() && getSum() > 0 &&  edDover.getText().toString().trim().length() == 0)
			Toast.makeText(this, R.string.dover_error_msg, Toast.LENGTH_SHORT).show();
		else
			super.onBackPressed();
	}
}
