package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

public class SalesRestore extends DocumentRestore {
	public SalesRestore() {
		super(SalesDoc.instance());
	}
	
	@Override
	protected void beforeWrite(DataObject dobj) {
		super.beforeWrite(dobj);
		for(OrderItem i : ((Sales)dobj).items) {
			SalesItem si = (SalesItem)i;
			if( si.sum == 0 && si.cost != 0 )
				si.sum = (int)FPOperation.itemMul(si.cost, si.qty, Consts.QTY_SCALE);
			if( si.taxSum != 0 ) {
				long sumWTax = si.sum - si.taxSum;
				si.costWOtax = (int)((long)sumWTax * Consts.QTY_SCALE / si.qty);
			}
		}
	}
}
