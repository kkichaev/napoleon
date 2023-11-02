package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;


public class DocListEx extends DocList {
	@Override
	protected DocFilterOnClickListener createDocListFilter() {
		return new DocFilterOnClickListener(this, true, false){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				if( !data.contains(WSOrderDoc.instance()) )
					data.add(WSOrderDoc.instance());
			}
		};
	}

}
