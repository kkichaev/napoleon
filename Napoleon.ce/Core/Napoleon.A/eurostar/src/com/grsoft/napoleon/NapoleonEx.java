package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.napoleon.documents.RkoDoc;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	protected static final int CHOOSE_COMMON_DOC = 0x1203;

	@Override
	protected ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> ret = super.createMainMenuList();
		ret.add(2, new MenuHandler("Общие документы", new Runnable() {
			
			@Override
			public void run() {
				showDialog(CHOOSE_COMMON_DOC);
			}
		}));
		
		return ret;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CHOOSE_COMMON_DOC ) {
			CharSequence[] items = new CharSequence[] { "РКО", "Перемещения" };
			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					DocTypeBase dt = which == 0 ? RkoDoc.instance() : MovementDoc.instance();
					CommonDocList.open(NapoleonEx.this, dt);
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
}
