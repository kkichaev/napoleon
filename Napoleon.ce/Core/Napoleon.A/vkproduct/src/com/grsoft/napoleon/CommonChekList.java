package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.CommonCheckImpl;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;

import android.content.Context;
import android.content.Intent;

public class CommonChekList extends CommonIncassList {
	public static void open(Context context) {
		Intent intent = new Intent(context, CommonChekList.class);
		context.startActivity(intent);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Class<? extends CommonIncassImplBase> documentType() { return CommonCheckImpl.class; }
}
