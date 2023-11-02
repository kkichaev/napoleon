package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class DocumentsEx extends DocumentsPrint {
//	@Override
//	protected DocFilterOnClickListener createDocFilter() {
//		return new DocFilterOnClickListener(this){
//			@Override
//			protected void initData(boolean creatableFilter) {
//				super.initData(creatableFilter);
//				data.remove(WSOrderDoc.instance());
//			}
//		};
//	}
	
	@Override
	protected boolean hideMakePko() {
		return DebtDoc.instance() != DocType.getCurDoc() && super.hideMakePko();
	}
}
