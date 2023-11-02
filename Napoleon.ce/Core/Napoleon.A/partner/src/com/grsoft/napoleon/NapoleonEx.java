package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {

	private int STOP_DLG = 1000;
	private String alertMessage = "";
	OrgClick orgClick = new OrgClick();
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == STOP_DLG ) {
			((AlertDialog)dialog).setMessage(alertMessage);
		} else
			super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == STOP_DLG ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Внимание");
			builder.setMessage("");
			builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) { orgClick.resumeClick(); }
			});
			
			builder.setNegativeButton("Отменить", null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		if( ((OrgEx)oi.getData()).stopMsg.length() > 0 ) {
			view.setBackgroundResource(R.drawable.list_grey_selector);
		}
	}
	
	@Override protected OnItemClickListener getItemOnClickListner() { return orgClick; }

	class OrgClick extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			clickedOrg = oi;
			alertMessage = ((OrgEx)oi.getData()).stopMsg;
			if( alertMessage.length() > 0 ) {
				showDialog(STOP_DLG);
			} else
				super.openOrg(oi);
		}
	}
}
