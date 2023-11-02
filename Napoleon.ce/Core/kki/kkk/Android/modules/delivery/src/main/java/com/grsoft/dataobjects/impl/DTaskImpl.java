package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DTask;
import com.grsoft.napoleon.dostavka.DTaskEdit;
import android.content.Context;


public class DTaskImpl extends DispatchDocImpl<DTask>  {
	@Override public void open(Context context) { DTaskEdit.open(context, getRowid()); }
}
