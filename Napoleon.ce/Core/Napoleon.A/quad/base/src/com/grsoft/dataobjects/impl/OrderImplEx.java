package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SklRest;
import com.grsoft.dataobjects.SklRestItem;
import com.grsoft.napoleon.DocumentsEx;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.GpsCoord;

public class OrderImplEx extends OrderImpl {
	
	SklRest sklRest = null;
	
	@Override
	public int getItemValue(Price item) {
		SklRestItem i = getSkladItem(item);
		return i == null ? 0 : i.qty;
//		return ((OrderEx)data).whCode == 0 ? item.qty : ((PriceEx)item).whQty;
	}
	
	public SklRestItem getSkladItem(Price item) {
		String idWh = ((OrderEx)data).whId;
		OrderItemEx oei = (OrderItemEx) findItem(item.id);
		if( oei != null)
			idWh = oei.idWh;
		
		if(idWh != null){
			if(sklRest == null || sklRest.id.equals(idWh) == false) {
				SklRestImpl sri = new SklRestImpl();
				sklRest = sri.getData();
				sklRest.id = idWh;
				sri.read();
				sri.close();
			}
			for(SklRestItem si : sklRest.items)
				if(si.id.equals(item.id))
					return si;
		}
		
		return null;
	}
	
	@Override
	public boolean initSilent(String orgId, GpsCoord coord) {
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		
		OrderEx ex = (OrderEx) data;
		ex.ordNumber = DocHelper.makeDocNumber(this);
		ex.debet = DocumentsEx.debet;
		
		return super.initSilent(orgId, coord);
	}
}
