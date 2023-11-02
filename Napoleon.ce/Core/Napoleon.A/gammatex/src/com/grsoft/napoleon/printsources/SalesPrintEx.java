package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.OrgImpl;

public class SalesPrintEx extends SalesPrint {

	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		OrgEx oe = (OrgEx)op;
		if( oe.ido != null && !oe.ido.equals(oe.id) ) {
			OrgImpl oi = new OrgImpl();
			OrgEx payOrg = (OrgEx)oi.getData();
			payOrg.id = oe.ido;
			if( oi.read() ) {
				payAddress = payOrg.address;
				payName = payOrg.name;
				payPhone = payOrg.phone;
				payBank = payOrg.bank;
				payInn = payOrg.inn;
			}
			oi.close();
		}
	}
}
