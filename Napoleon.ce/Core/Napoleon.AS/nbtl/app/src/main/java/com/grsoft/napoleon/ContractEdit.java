package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


public class ContractEdit extends Activity {
	private View btnCancel;
	private View btnOK;
	private EditText edRemark;
	private TextView tvOrg;
	
	private boolean edit = false;
	private ContractImpl document = new ContractImpl();
	
	public static void open(Context context, long rowid, boolean edit){
		Intent i = new Intent(context, ContractEdit.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, edit);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);

		context.startActivity(i);	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contractedit);
		
		btnCancel = findViewById(R.id.btnCancel);
		btnOK = findViewById(R.id.btnOK);
		edRemark = (EditText) findViewById(R.id.edRemark);
		tvOrg = (TextView) findViewById(R.id.tvOrgName);
		
		edit = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, false);
		
		document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		document.close();
		
		btnCancel.setOnClickListener(cancelClick);
		btnOK.setOnClickListener(okClick);
		edRemark.setText(document.getData().remark);
		
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		org.close();
		tvOrg.setText(org.getData().name);
		
		if(!document.isEditable()){
			edRemark.setEnabled(false);
			btnOK.setEnabled(false);
		}
	}
	
	private OnClickListener cancelClick = new OnClickListener() { 
		@Override public void onClick(View v) { 
			finish();
			
			if (!edit && document.getData().items.size() == 0)
				document.delete();
		} };
	
	private OnClickListener okClick = new OnClickListener() {
		@Override public void onClick(View v) {
			saveDoc();
			finish();
			
			if(!edit)
				Warehouse.open(v.getContext(), document, false);
		}
	};

	protected void saveDoc() {
		document.getData().remark = edRemark.getText().toString().trim();
		document.write();
		document.close();
	}
}
