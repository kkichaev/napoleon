package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DReturn;
import com.grsoft.napoleon.dostavka.DReturnEdit;
import android.content.Context;

public class DReturnImpl extends DWaybillDocumentImpl<DReturn>{
	@Override public void open(Context context) { DReturnEdit.open(context, this);}
}
