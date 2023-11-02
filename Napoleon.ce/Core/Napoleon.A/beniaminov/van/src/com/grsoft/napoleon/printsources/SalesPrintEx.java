package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.util.Util;

public class SalesPrintEx extends SalesPrint {

	public String dogovor;
	public String dogDate;
	public String dogNum;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		
		OrgEx oe = (OrgEx)op;
		
		dogDate = "";
		dogNum = "";
		dogovor = oe.dogovor;
		if(oe.divName.length() > 0)
			name = oe.divName;
		
		if(oe.dogNum.length() > 0) {
			dogDate = Util.simpleDateFormat.format(oe.dogDate);
			dogNum = oe.dogNum;
			
			dogovor = dogNum + " от " + dogDate;
		}
		
		if(oe.payBank.length() > 0)
			payBank = oe.payBank;
		if(oe.payInn.length() > 0)
			payInn = oe.payInn;
		if(oe.payName.length() > 0)
			payName = oe.payName;
		if(oe.payPhone.length() > 0)
			payPhone = oe.payPhone;
		
		if(oe.kpk > 0){
			name = ((SalesEx)sales).orgName;
			address = ((SalesEx)sales).orgAddress;
		}
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		return new SalesItemPrintEx((SalesItem)item, index, sales.sumType);
	}
	
	@Override
	protected SupplSource createSupplSource() {
		return new SupplSourceEx();
	}
}
