package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class DocumentsEx extends Documents {

	private static final int ASK_SHOW_TASK = 0;

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new DocExAdapter(this, docType, id, null);
	}
	
	@Override 
	protected int getContentViewID() { return R.layout.docs_ex; }

	@Override
	protected void createNewDoc() {
		if(DocType.getCurDoc() == OrderDoc.instance() && ((CfgNplEx)ConfigManager.getConfig()).showAgentTask) {
			showDialog(ASK_SHOW_TASK);
		} else
			super.createNewDoc();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ASK_SHOW_TASK) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Создание заявки");
			b.setSingleChoiceItems(new String[] {"Показать задачи", "Внеплановый визит"}, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( which == 1 )
						DocumentsEx.super.createNewDoc();
					else
						AgentTaskList.open(DocumentsEx.this, org.getData().id, false);
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
}
