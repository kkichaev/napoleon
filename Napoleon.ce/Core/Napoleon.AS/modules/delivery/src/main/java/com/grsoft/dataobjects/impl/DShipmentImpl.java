package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DShipment;
import com.grsoft.napoleon.dostavka.DShipmentEdit;
import android.content.Context;

public class DShipmentImpl extends DWaybillDocumentImpl<DShipment>{
	@Override public void open(Context context) {	DShipmentEdit.open(context, this);}
}
