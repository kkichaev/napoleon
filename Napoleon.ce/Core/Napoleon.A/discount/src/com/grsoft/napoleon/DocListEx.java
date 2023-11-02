package com.grsoft.napoleon;

import android.os.Bundle;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;

public class DocListEx extends DocList {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		int result = DocStatus.getImage(doc); 
		
		if(result == Consts.INVALID_ID)
			result = R.drawable.notsend;
		
		return result;
	}
}


