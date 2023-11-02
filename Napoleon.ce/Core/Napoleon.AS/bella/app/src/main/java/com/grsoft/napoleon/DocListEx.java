package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.CreatableDocument;



public class DocListEx extends DocList {
	protected DocStatusChangeListener createStatusChangeListener() {
		return new DocStatusChangeListener(){
			@Override
			protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
				if (cd instanceof OrderImpl){
					if( cd.isProceeded() )
						return false;
					else 
						return true;
				}else
					return super.isAllowChangeStatus(cd);
			}
		};
	}
}
