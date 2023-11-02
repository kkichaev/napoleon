package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ArchIncassImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;

import android.view.View;

public class DocListEx extends DocList {
	@Override
	protected void init(DocType docType) {
		super.init(docType);
		
		btnDelete.setVisibility(View.GONE);
	}
	
	@Override
	protected DocStatusChangeListener createStatusChangeListener() {
		return new ChStatus();
	}
	
	class ChStatus extends DocStatusChangeListener {
		@Override
		protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
			if( cd instanceof ArchIncassImpl )
				return false;
			return super.isAllowChangeStatus(cd);
		}
	}
}
