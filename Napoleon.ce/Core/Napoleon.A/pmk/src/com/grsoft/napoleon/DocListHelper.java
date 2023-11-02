package com.grsoft.napoleon;

import com.grsoft.dataobjects.ParamStateEx;
import com.grsoft.napoleon.documents.CreatableDocument;

public class DocListHelper {
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		int result = R.drawable.notsend;
		
		if ((doc.getData().params & ParamStateEx.approved) == ParamStateEx.approved)  
			result = R.drawable.approved;
		else if  ((doc.getData().params & ParamStateEx.pending) == ParamStateEx.pending)
			result = R.drawable.pending;
		else if (doc.isExported())
			result = R.drawable.pod;
		
		return result;
	}
}
