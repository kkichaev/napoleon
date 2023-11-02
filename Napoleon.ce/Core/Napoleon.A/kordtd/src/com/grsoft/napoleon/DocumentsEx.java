package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class DocumentsEx extends Documents {

	OrgEx curOrg;

	@Override
	protected boolean isOrgBlocked(Org o, DocType dt) {
		curOrg = (OrgEx) o;
		if (dt == OrderDoc.instance() && (curOrg.stopMsg.length() > 0 || curOrg.blockMsg.length() > 0))
			return true;
		return super.isOrgBlocked(o, dt);
	}

	@Override
	protected Dialog createWarningStopListDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);

		if (curOrg.blockMsg.length() == 0) {
			builder.setMessage(curOrg.stopMsg);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					doCreate();
				}
			});
		} else {
			builder.setMessage(curOrg.blockMsg);
			builder.setPositiveButton(android.R.string.ok, null);
		}

		builder.setNegativeButton(android.R.string.cancel, null);
		
		return builder.create();
	}
}
