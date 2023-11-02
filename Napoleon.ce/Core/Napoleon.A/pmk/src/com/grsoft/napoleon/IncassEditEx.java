package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class IncassEditEx extends IncassEdit {
	
	boolean sendDoc = false;
	
	@Override
	protected void btnOkPressed() {
		if(doc.isEditable() && doc.isExported()){
			sendDoc = false;
			showDialog(R.id.save_incass_dialog);
			return;
		}
		super.btnOkPressed();
	}
	
	@Override
	protected void send() {
		if(doc.isEditable() && doc.isExported()) {
			sendDoc = true;
			showDialog(R.id.save_incass_dialog);
			return;
		}
		super.send();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.save_incass_dialog) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтверждение");
			b.setMessage("Инкассация уже была отправлена ранее, внести корректировку?");
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { arg0.dismiss(); }
			});
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { 
					arg0.dismiss();
					save();
					if(sendDoc)
						IncassEditEx.super.send();
					else
						finish();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override protected void handlingInvalidSum() { }
}
