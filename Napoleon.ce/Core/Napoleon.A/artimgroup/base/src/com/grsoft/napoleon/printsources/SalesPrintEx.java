package com.grsoft.napoleon.printsources;

import java.util.List;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesPrintEx extends SalesPrint {

	public String torg = "";
	public String dogovor = "";
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int disc = 0;
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int sumWDisc = 0;

	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int totalQty = 0;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
		
		AgentPrefix a = AgentPrefix.get();
		if( a != null )
			torg = a.name;
		sumWDisc = totalSum;
		totalQty = totalPack;
		disc = ((SalesEx)sales).discval;
		totalSum -= (int) (((long) totalSum * disc + Consts.SUM_SCALE
				* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
	}

	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		return new SalesItemPrintEx((SalesItem)item, index, sales.sumType);
	}
	
	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		List<OrgDogovor> dogovors = ((OrgEx)op).dogovors;
		String dogCode = ((SalesEx)sales).iddog;
		if( dogovors != null ) {
			for( OrgDogovor d : dogovors)
				if( d.id.equals(dogCode) ) {
					dogovor = d.name;
					break;
				}
		}
	}
}
