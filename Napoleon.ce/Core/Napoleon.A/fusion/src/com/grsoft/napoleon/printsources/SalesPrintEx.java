package com.grsoft.napoleon.printsources;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.OrgImpl;

public class SalesPrintEx extends SalesPrint {
	public String factAddress;
	public String baseName;
	public String baseAddress;
	public String basePhone;
	public String baseBank;
	public String baseInn;
	public String agent;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
	}
	
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		
		OrgEx oe = (OrgEx)op;
		factAddress = oe.factAddress;
		
		if( oe.fullName != null && oe.fullName.length() > 0 )
			name = oe.fullName;
		
		OrgImpl oi = new OrgImpl();
		OrgEx bo = (OrgEx) oi.getData();
		bo.id = oe.baseOrg;
		if( oi.read() ) {
			baseName = bo.fullName.length() > 0 ? bo.fullName : bo.name;
			baseAddress = bo.address;
			basePhone = bo.phone;
			baseBank = bo.bank;
			baseInn = bo.inn;
	   } else
	   {
		   baseName = name;
		   baseAddress = oe.address;
		   basePhone = oe.phone;
		   baseBank = oe.bank;
		   baseInn = oe.inn;
		}
		oi.close();
		
		AgentPrefix ap = new AgentPrefix();
		String table = DataObjectInfo.getInstance().getTableName(AgentPrefix.class);
		DbReader r = new DbReader(); 				
		if( r.select(ap, table, "id=userid") )
			this.agent = ap.name;
		else
			this.agent = "";
		r.close();
	}
}
