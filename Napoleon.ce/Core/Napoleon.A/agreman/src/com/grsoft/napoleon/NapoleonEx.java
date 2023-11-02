package com.grsoft.napoleon;

import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PurchaseDoc;

public class NapoleonEx extends Napoleon {
	protected OnItemClickListener getItemOnClickListner() { 
		return new OrglListOnClickListener(){
	
			@Override
			protected void openOrg(OrgImpl oi) {
				if(DocType.getCurDoc() == PurchaseDoc.instance())
					PurchaseList.open(NapoleonEx.this, oi.getRowid());
				else
					super.openOrg(oi);
			}
		};
	}
}
