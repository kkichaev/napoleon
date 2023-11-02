package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {
	
	public static final int SHOW_ORG_MESSAGE = 1223;
	OrgClick oc;
	
	@Override
	protected void setOrgBackground(int pos, OrgImpl org, View v) {
		if( org != null ) {
			OrgEx oe = (OrgEx) org.getData();
			if( oe.stopMsg != null && oe.stopMsg.length() > 0 ) {
				v.setBackgroundResource(R.drawable.stop_row_selector);
				return;
			}
			if( oe.debtMsg != null && oe.debtMsg.length() > 0 ) {
				v.setBackgroundResource(R.drawable.debt_row_selector);
				return;
			}
		}
		super.setOrgBackground(pos, org, v);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id != SHOW_ORG_MESSAGE )
			return super.onCreateDialog(id);
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Заголовок");
		b.setMessage("");
		b.setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {			
			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if( oc != null )
					oc.resumeClick();
			}
		});
		return b.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id != SHOW_ORG_MESSAGE )
			super.onPrepareDialog(id, dialog);
		else {
			if( oc != null && oc.getOrg() != null ) {
				AlertDialog ad = (AlertDialog)dialog;
				OrgEx oe = (OrgEx) oc.getOrg().getData();
				if( oe.stopMsg.length() > 0 ) {
					ad.setTitle("Клиент заблокирован");
					ad.setMessage(oe.stopMsg);
				} else if( oe.debtMsg.length() > 0 ) {
					ad.setTitle("Просрочка");
					ad.setMessage(oe.debtMsg);
				}
			}
		}
	}
	
	@Override protected String getOrgReadingFields()  { return "name,id,address,color,flags,stopMsg,debtMsg"; }
	
	@Override protected OnItemClickListener getItemOnClickListner() { 
		oc = new OrgClick();
		return oc; 
	}
	
	class OrgClick extends OrglListOnClickListener {
		
		public OrgImpl getOrg() { return clickedOrg; }
		
		@Override
		protected void openOrg(OrgImpl oi) {
			clickedOrg = oi;
			OrgEx oe = (OrgEx) oi.getData();
			if( (oe.stopMsg != null && oe.stopMsg.length() > 0) || (oe.debtMsg != null && oe.debtMsg.length() > 0) ) {
				showDialog(SHOW_ORG_MESSAGE);
				return;
			}
			super.openOrg(oi);
		}
	}
}
