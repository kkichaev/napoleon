package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;

public class NapoleonEx extends Napoleon {
	@Override
	protected boolean isPotencialOrg(long rowid) {
		return false;
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		DocType.getCurDoc().setMainView(view, linesController, oi, os);
	}
}
