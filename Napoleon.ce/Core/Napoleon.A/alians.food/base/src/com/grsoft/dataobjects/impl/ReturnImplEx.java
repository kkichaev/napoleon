package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ArchReturn;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.documents.ArchReturnDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public boolean initSilent(String orgId, GpsCoord coord) {
		data.number = DocHelper.makeDocNumber(this);
		return super.initSilent(orgId, coord);
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		boolean ret = super.updateQty(priceImpl, qty, cost, inPack); 
		DebtDoc.instance().refreshDocSum(data.id);
		OrgHelper.refresh();
		return ret;
	}
	
	@Override
	public int getItemValue(Price item) {
		return ((PricePrint)item).vanQty;
	}
	
	@Override
	public long sum() {
		return -super.sum();
	}
	
	@Override
	protected boolean checkPriceQty() {
		return true;
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		return qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		((PricePrint)price.getData()).vanQty += -qty;
		price.write();
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, this);
	}
	
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		OrderItemEx oie = (OrderItemEx)item;
		if(oie.costCode == null || oie.costCode.length() == 0) {
			ReturnEx oe = (ReturnEx) data;
			oie.costCode = oe.costCode;
			oie.costIndex = oe.sumType;
		}
	}
	
	@Override
	public void updateItemsCost(int sumType) {
	}
	
	@Override
	public boolean delete() {
		if( data.items.size() > 0 ) {
			ArchReturn ar = new ArchReturn();
			String table = DataObjectInfo.getInstance().getTableName(Return.class);
			DbReader r = new DbReader();
			if( r.select(ar, table, "created="+data.created.getTime()) ) {
				DbWriter w = new DbWriter();
				ar.params = 0;
				w.insertRecord(ar);
				w.close();
				ArchReturnDoc.instance().refreshDocSum(data.id);
			}
		}
		
		if( !super.delete() )
			return false;

		return true;
	}
}
