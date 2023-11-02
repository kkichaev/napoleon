package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrgDistrictImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import android.os.Bundle;
import android.widget.Toast;

public class DocumentsEx extends Documents{
	OrgDistrictImpl orgDistrict = new OrgDistrictImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		orgDistrict.read("id", org.getData().id);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		if (docType==ReturnDoc.instance() && orgDistrict.getData().rejret != 0){
			btnNewDoc.setEnabled(false);
			Toast.makeText(this, R.string.return_reject_message, Toast.LENGTH_SHORT).show();
		}
	}
}
