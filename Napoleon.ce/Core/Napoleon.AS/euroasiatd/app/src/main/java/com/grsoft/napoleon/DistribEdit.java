package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class DistribEdit extends Activity implements OnClickListener {
	EditText edRemark;
	DistribImpl doc = (DistribImpl) DistribDoc.instance().create();
	boolean editMode = false;
	
	public static void open(Context context, long rowid, boolean edit) {
		Intent i = new Intent(context, DistribEdit.class);
		i.putExtra(ExtrasConst.EDIT_MODE_STR, edit);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.distrib_edit);
		edRemark = (EditText) findViewById(R.id.edRemark);
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, false);
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		edRemark.setText(doc.getData().remark);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK)
			setOKResult();
		else if(v.getId() == R.id.btnCancel)
			setCancelMode();
	}

	protected void setCancelMode() {
		if(!editMode) {
			doc.delete();
			doc.close();
		}
		
		finish();
	}

	private void setOKResult() {
		doc.getData().remark = edRemark.getText().toString().trim();
		
		doc.write();
		doc.close();
		
		if(!editMode)
			Warehouse.open(this, doc, false);
		
		finish();
	}
}
