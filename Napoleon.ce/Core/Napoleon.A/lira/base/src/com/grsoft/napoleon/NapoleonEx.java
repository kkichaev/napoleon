package com.grsoft.napoleon;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

public class NapoleonEx extends Napoleon {
	
	static final int END_LICENSE_DIALOG = 1000;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case END_LICENSE_DIALOG:
			return endLicenseAlert();
		}
		return super.onCreateDialog(id);
	}

	private Dialog endLicenseAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Внимание");
		builder.setMessage("У клиента просрочена лицензия");
		builder.setPositiveButton("ОК", null);
		return builder.create();
	}

	private boolean orgLicenseEnded(OrgImpl oi) {
		if( oi != null ) {
			Date el = ((OrgEx)oi.getData()).endLicense;
			Date now = new Date();
			Date start = new Date(70,0,2);
			if( el != null && el.before(now) && el.after(start) ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected String getOrgReadingFields() {
		return super.getOrgReadingFields() + ",endLicense";
	}
	
	@Override
	protected void setOrgBackground(int pos, OrgImpl org, View v) {
		if( orgLicenseEnded(org) )
			v.setBackgroundResource(R.drawable.list_grey_selector );
		else
			super.setOrgBackground(pos, org, v);
	}
	
	@Override
	protected OnItemClickListener getItemOnClickListner() { return new OpenOrg(); }

	class OpenOrg extends OrglListOnClickListener {
		
		@Override
		protected void openOrg(OrgImpl oi) {
			DocType dt = DocType.getCurDoc();
			if(dt == OrderDoc.instance() && orgLicenseEnded(oi) ) {
				showDialog(END_LICENSE_DIALOG);
			} else
				Documents.open(NapoleonEx.this, oi.getData());
		}
	}
}
