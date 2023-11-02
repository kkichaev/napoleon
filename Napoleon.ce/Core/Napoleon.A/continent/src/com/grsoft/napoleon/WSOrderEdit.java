package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class WSOrderEdit extends Activity implements OnClickListener {
	WSOrderImpl doc = new WSOrderImpl();
	EditText edRemark;
	private boolean editMode = false;
	
	public static void open(Context context, long rowid, boolean edit) {
		Intent i = new Intent(context, WSOrderEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		i.putExtra(ExtrasConst.EDIT_MODE_STR, edit);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.wsorderedit);
		
		edRemark = (EditText)findViewById(R.id.edRemark);
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		edRemark.setText(doc.getData().remark);
		
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnCancel) {
			if (!editMode && doc.isEmpty()) {
				doc.delete();
			}
		}else if (v.getId() == R.id.btnOK) {
			doc.getData().remark = edRemark.getText().toString().trim();
			doc.write();
			
			if(!editMode)
				Warehouse.open(WSOrderEdit.this, doc, false);
		}
			
		doc.close();
		finish();
	}

}
