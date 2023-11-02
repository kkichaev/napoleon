package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.ReturnCount;
import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnCount.open(context, this, itemRowid);
	}
}
