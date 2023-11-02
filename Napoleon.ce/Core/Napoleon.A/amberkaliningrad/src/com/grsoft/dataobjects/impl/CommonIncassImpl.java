package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.napoleon.CommonIncassEdit;

public class CommonIncassImpl extends CommonIncassImplBase<CommonIncass> {
	@Override
	public void open(Context context){
		CommonIncassEdit.open(context, getRowid());
	}
}
