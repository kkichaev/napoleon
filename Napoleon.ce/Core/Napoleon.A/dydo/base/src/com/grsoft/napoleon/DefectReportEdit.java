package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import com.grsoft.dataobjects.DefectReport;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DefectReportDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.ExtrasConst;


public class DefectReportEdit extends VisitEdit{
	EditText edDevice;
	
	public static void open(Context context, DbObject<?> dbobj){
		Intent i = new Intent(context, DefectReportEdit.class);		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, dbobj.getRowid());
		context.startActivity(i);	
	}
	
	@Override
	protected void init(Bundle savedInstanceState) {
		edDevice = (EditText) findViewById(R.id.edDevice);
		super.init(savedInstanceState);
		DefectReport def = (DefectReport) visit.getData();
		edDevice.setText(def.device);
	}
	
	@Override
	protected int getContentView() { return R.layout.defectreport; }
	
	@SuppressWarnings("unchecked")
	protected CreatableDocument<? extends Visit> createDocument() { return (CreatableDocument<? extends Visit>) DefectReportDoc.instance().create(); }
	
	@Override
	protected boolean saveVisit() {
		DefectReport def = (DefectReport) visit.getData();
		def.device = edDevice.getText().toString().trim();
		
		return super.saveVisit();
	}
	
	@Override
	protected void setEditableControl(boolean isEditable) {
		super.setEditableControl(isEditable);
		edDevice.setEnabled(isEditable);
	}
	
	@Override
	protected void send() {
		new DocumentSender(this, btnSend, 
				DefectReportDoc.OBJ_NAME, visit, visit.getRowid(), 
				this).execute((Void[])null);
	}
}

