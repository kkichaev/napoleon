package com.grsoft.napoleon;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.Move;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.MoveImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class MoveProperties extends BaseActivity {
	MoveImpl doc = new MoveImpl();
	Spinner spSrc;
	Spinner spDst;
	Spinner spFSrc;
	Spinner spFDst;
	EditText edRemark;
	
	boolean editMode = false;
	private static final int DIALOG_DATE_PICKER_ID = 0;
	DateHandler dateHandler;
	
	public static void open(Context context, long rowid, boolean editMode) {
		Intent intent = new Intent(context, MoveProperties.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		context.startActivity(intent);
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moveprop);
		
		spSrc = (Spinner)findViewById(R.id.spSrc);
		spDst = (Spinner)findViewById(R.id.spDst);
		spFSrc = (Spinner)findViewById(R.id.spFSrc);
		spFDst = (Spinner)findViewById(R.id.spFDst);
		edRemark = (EditText)findViewById(R.id.edRemark);
		
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, false);
		
		ConfigImpl config = new ConfigImpl();
		
		if(doc.read(rowid)){
			Move move = doc.getData();
			
			if (!editMode){
				OrgImpl org = new OrgImpl();
				org.read("id", move.id);
				move.sumType = org.getData().costype;
			}
			
			DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(),spSrc, move.src);
			DialogHelper.loadSpinnerWithKey(config, "Склады", new ArrayList<KeyValue>(),spDst, move.dst);
			
			Button btnOK = (Button) findViewById(R.id.btnOK);
			btnOK.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!doc.isExported()){
						KeyValue src = (KeyValue)((Spinner)findViewById(R.id.spSrc)).getSelectedItem();
						KeyValue dst = (KeyValue)((Spinner)findViewById(R.id.spDst)).getSelectedItem();
						
						Move move = doc.getData();
						move.src = src.key.toString();
						move.dst = dst.key.toString();
						move.fsrc = spFSrc.getSelectedItemPosition();
						move.fdst = spFDst.getSelectedItemPosition();
						move.remark = edRemark.getText().toString().trim();
						move.date = dateHandler.getDate();
						
						doc.write();
						
						if(!editMode)
							Warehouse.open(MoveProperties.this, doc, false);
						
						finish();
					}
				}
			});
			
			Button btnCancel = (Button) findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), spFSrc, move.fsrc);
			DialogHelper.loadSpinnerFromConfig(config, "Организация", new ArrayList<CharSequence>(), spFDst, move.fdst);
			edRemark.setText(move.remark);
		}
		
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), doc.getData().date, DIALOG_DATE_PICKER_ID);
		
		doc.close();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_PICKER_ID:
				return dateHandler.createDialog();
		}
		return super.onCreateDialog(id);
	}
}
