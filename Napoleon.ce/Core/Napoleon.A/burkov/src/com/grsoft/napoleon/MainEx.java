package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import android.widget.BaseAdapter;

public class MainEx extends Main {
	@Override
	protected BaseAdapter createSolidMainAdapter() {
		final OrgSumImpl sumImpl = new OrgSumImpl();
		
		return new SolidMainAdapter(this){
			@Override
			protected boolean skipItem(Org o) {
				boolean result = super.skipItem(o);
				
				DocType dt = DocType.getCurDoc();
				
				if(dt == DebtDoc.instance()){
					sumImpl.getData().type = dt.getName();
					sumImpl.getData().id = o.id;
					
					result = dt.getSum(sumImpl) == 0;
				}
				
				return result;
			}
		};
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if (DocType.getCurDoc() == DebtDoc.instance() || docType == DebtDoc.instance()){
			BaseAdapter a = (BaseAdapter) list.getAdapter();
			
			if(a instanceof SolidMainAdapter){
				DocType.setCurDoc(docType, true);
				((SolidMainAdapter)a).reload();
			}
		}
			
		super.adjustViewForDocType(docType);
	}
}
