package com.grsoft.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.CheckConfirm;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.CommonChek;
import com.grsoft.dataobjects.CommonChekItem;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.network.exception.RuntimeException;

public class ChekStatusSender implements ObjectExportListener {
	
	List<CheckConfirm> items = new ArrayList<CheckConfirm>();
	AgentRcv a = AgentRcv.currentAgent();
	
	public ChekStatusSender(CommonChek data, int type) {
		for(CommonIncassItem cii : data.items) {
			CommonChekItem cci = (CommonChekItem)cii;
			
			CheckConfirm cc = new CheckConfirm();
			cc.created = cci.created;
			cc.handled = new Date();
			cc.status = ChekBase.CHEK_IN_COMMON_LIST;
			cc.type = type;
			cc.userid = a == null ? "" : a.userid;
			
			items.add(cc);
		}
	}

	@Override public void onStart() { }

	@Override public void onRead(RawObject rawObject) throws RuntimeException { }

	@Override public void onSave() { }

	@Override public void onEnd() { }

	@Override
	public String getObjectName() { return "CheckConfirm"; }

	@Override
	public int size() { return items.size(); }

	@Override
	public DataObject get(int i) { return items.get(i); }

}
