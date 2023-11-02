package com.grsoft.napoleon;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.DocType;
import android.view.View;


public class DocumentsEx extends Documents {
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	@Override
	protected String orgInfo(Org o) {
		String result = "<b>" + o.name + "</b>";
		
		if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
			result += "<br><i>" + o.address + "</i>";
		}
		
		Contact c = null;
		if(o.contacts.size() > 0){
			c = o.contacts.get(0);
			
			if(c.name.trim().length() > 0)
				result += "<br>" + (c.name);
		}
		
		return result;
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	@Override protected void onlyVisitInit() {}
}
