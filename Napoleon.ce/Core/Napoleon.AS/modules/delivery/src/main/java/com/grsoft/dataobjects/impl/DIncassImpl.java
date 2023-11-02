package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DIncass;
import com.grsoft.napoleon.dostavka.DIncassEdit;
import android.content.Context;

public class DIncassImpl extends DispatchDocImpl<DIncass> {
	@Override public void open(Context context) { DIncassEdit.open(context, getRowid()); }
	
	
	
}
