package com.grsoft.database;

import java.util.Locale;

import com.grsoft.dataobjects.Rfrg;
import com.grsoft.dataobjects.RfrgObjItem;
import com.grsoft.dataobjects.RfrgObjRcv;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class RfrgRcv extends RcvNewHitching {
	
	Rfrg rd = new Rfrg();

	public RfrgRcv() {
		super(Rfrg.class, "Rfrg");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		RfrgObjRcv dobj = (RfrgObjRcv) rawObject.createDataObject(RfrgObjRcv.class);
		
		rd.id = dobj.id;
		rd.ido = dobj.ido;
		rd.inv = dobj.inv;
		rd.type = dobj.type;
		rd.model = dobj.model;
		
		StringBuilder rfid = new StringBuilder("|");
		for(RfrgObjItem ri : dobj.items)
			rfid.append(ri.id.replace(" ", "").toUpperCase(Locale.getDefault())).append("|");
		
		rd.rfid = rfid.toString();
		
		dbProxy.insertRecord(rd);
	}
}
