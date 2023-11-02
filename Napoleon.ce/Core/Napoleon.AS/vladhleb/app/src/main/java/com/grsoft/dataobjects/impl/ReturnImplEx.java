package com.grsoft.dataobjects.impl;

import android.content.Context;
import android.widget.Toast;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class ReturnImplEx extends ReturnImpl {
	
	Context curContext;
	
	@Override
	public void postInit() {
		super.postInit();

		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();
		
		data.sumType = o.costype;
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		curContext = context;
		super.editItem(itemRowid, context);
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {

		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();

		int maxRet = o.retLimit;
		if( maxRet != 0 ) {
			int sum = 0;
			DatePeriod dp = new DatePeriod(Util.getDate(), Util.getDateTime());
			DocList dl = ReturnDoc.instance().docList(data.id, "", dp);
			for(Document<?> d : dl) {
				sum += d.sum();
			}
			dl.close();
			
			OrderItem item = (OrderItem) findItem(priceImpl.getData().id);
			if( item != null ) {
				sum -= (int) ((long)item.qty * item.cost / Consts.QTY_SCALE);
			}
			
			sum += (int)((long)qty * cost / Consts.QTY_SCALE);
			if( sum > maxRet) {
				if(curContext != null) {
					Toast.makeText(curContext, String.format("Возвраты превышают дневной лимит (%s)", 
							Util.IntToScaleStr(maxRet, Consts.SUM_SCALE, Util.DEC_DELIM, false)), Toast.LENGTH_SHORT).show();
					curContext = null;
				}
				return false;
			}
		}
		return super.updateQty(priceImpl, qty, cost, inPack);
	}
}
