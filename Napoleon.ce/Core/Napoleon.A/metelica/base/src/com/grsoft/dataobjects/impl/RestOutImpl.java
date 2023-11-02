package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import android.content.Context;

import com.grsoft.dataobjects.RestInItem;
import com.grsoft.dataobjects.RestOut;
import com.grsoft.dataobjects.RestOutItem;
import com.grsoft.dataobjects.Restin;
import com.grsoft.napoleon.RestOutEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.MessageBox;

public class RestOutImpl extends CreatableDocument<RestOut> {

	@Override
	public void open(Context context) {
		if(rowid != ExtrasConst.INVALID_ID)
			RestOutEdit.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		RestinImpl ri = new RestinImpl();
		Restin r = ri.getData();
		r.id = orgId;
		
		boolean res = ri.read();
		ri.close();
		if( !res ) {
			MessageBox.show(context, "Ошибка", "Нет планов на точку");
			return false;
		}
		
		data.items = new ArrayList<RestOutItem>();
		for(RestInItem i : r.items) {
			RestOutItem ro = new RestOutItem();
			ro.id = i.id;
			ro.plan = i.plan;
			data.items.add(ro);
		}
		
		return super.init(context, orgId, gpsCoord);
	}
}
