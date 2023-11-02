package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesBanImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.ScriptDoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class ScriptEditEx extends ScriptEdit {

	@Override
	public void onBackPressed() {
		if(doc.isContainsItem() && doc.isComplete() == false) {
			showDialog(R.id.script_incompleete);
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.script_incompleete) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage("Сценарий не закончен. Выйти?");
			b.setTitle("Вопрос");
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) {
					DocType.setCurDoc(ScriptDoc.instance());
					finish();
				}
			});
			
			b.setNegativeButton(android.R.string.no, null);
			return b.create();
		}
		return super.onCreateDialog(id);
	}
}
