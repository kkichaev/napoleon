package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;

import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class VisitEditEx extends VisitEdit {
	final static int ASK_TO_DEL_VISIT_MSG = 333;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			
			if (ScriptDefImpl.canScripting() && 
					(visit.getData().items == null || visit.getData().items.size() == 0))
				showDialog(ASK_TO_DEL_VISIT_MSG);
			else{
				if (!saveVisit())
					visit.delete();
				
				finish();
			}
			
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == ASK_TO_DEL_VISIT_MSG)
			return createAskToDelDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createAskToDelDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Внимание");
		builder.setMessage("Документ без фотографий будет удален, удалить?");
		builder.setPositiveButton("ОК", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				visit.delete();
				finish();
			}
		});
		
		builder.setNegativeButton("Отмена", null);
		
		return builder.create();
	}
}
