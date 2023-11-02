package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


public class ScriptImplEx extends ScriptImpl{
	@Override
	public String getDescription(Context context) {
		
		String result = super.getDescription(context);
		CreatableDocument<?>[] d = getDocuments();
		
		if(d != null)
			for(int i = 0; i < d.length; i++)
				if(d[i] != null && d[i].getClass().equals(OrderImplEx.class)){
					result = d[i].getDescription(context);
					break;
				}
		
		return result;
	}
}
