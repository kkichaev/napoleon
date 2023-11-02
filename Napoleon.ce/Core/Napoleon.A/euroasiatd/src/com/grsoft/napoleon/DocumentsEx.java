package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

import android.text.Html;

public class DocumentsEx extends Documents {
	public void updateOrgInfo(boolean dlvinfo) {
		tvOrgInfo.setText(Html.fromHtml(orgInfo(org.getData(), dlvinfo)));
	}
	
	protected String orgInfo(Org o, boolean dlvinfo) {
		String result =  orgInfo(o);
		
		if (dlvinfo)
			result += String.format("<br>%s", ((OrgEx)o).dlvinfo);
		
		return result;
	}
}
