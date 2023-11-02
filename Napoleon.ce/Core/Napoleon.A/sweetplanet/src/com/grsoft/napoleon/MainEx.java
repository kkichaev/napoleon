package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class MainEx extends Main {
	protected int getSolidRowID() {
		if (DocType.getCurDoc() == DebtDoc.instance())
			return R.layout.debt_list_row;
		else
			return super.getSolidRowID();
	}
	protected int getFolderRowID() { 
		return getSolidRowID(); 
	}
}
