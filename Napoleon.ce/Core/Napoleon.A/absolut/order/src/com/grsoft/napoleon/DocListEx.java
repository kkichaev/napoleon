package com.grsoft.napoleon;

import com.grsoft.dataobjects.ParamStateEx;
import com.grsoft.napoleon.documents.CreatableDocument;

public class DocListEx extends DocList {
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if ((doc.getData().params & ParamStateEx.ofConfirm) == ParamStateEx.ofConfirm)
			return R.drawable.dlvstatus;
		
		return super.getDocStatusResource(doc);
	}
}
