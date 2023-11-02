package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ArchSales;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.napoleon.documents.ArchSalesDoc;

public class SalesImplEx extends SalesImpl {
	@Override
	public boolean delete() {
		if( data.items.size() > 0 ) {
			ArchSales as = new ArchSales();
			String table = DataObjectInfo.getInstance().getTableName(Sales.class);
			DbReader r = new DbReader();
			if( r.select(as, table, "created="+data.created.getTime()) ) {
				DbWriter w = new DbWriter();
				as.params = 0;
				w.insertRecord(as);
				w.close();
				ArchSalesDoc.instance().refreshDocSum(data.id);
			}
		}
		
		if( !super.delete() )
			return false;

		OrgHelper.refresh();
		return true;
	}
	
	
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		SalesItemEx oie = (SalesItemEx)item;
		if(oie.costCode == null || oie.costCode.length() == 0) {
			SalesEx oe = (SalesEx) data;
			oie.costCode = oe.costCode;
			oie.costIndex = oe.sumType;
		}
	}
	
	@Override
	public void updateItemsCost(int sumType) {
		write();
		close();
	}
}
