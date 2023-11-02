package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DFakeShipment;
import com.grsoft.napoleon.dostavka.DFakeShipmentEdit;

import android.content.Context;

public class DFakeShipmentImpl extends DWaybillDocumentImpl<DFakeShipment> {

	@Override public void open(Context context) { DFakeShipmentEdit.open(context, this); }

	@Override
	public void postInit() {
		data.created = new Date(1188777600000l); // 2007 - 09 - 03
	}
}
