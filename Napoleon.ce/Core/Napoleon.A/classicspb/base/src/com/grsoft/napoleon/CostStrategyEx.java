package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceBase;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.impl.IMatrix;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof IMatrix) {
			OrgMatrix om = ((IMatrix)doc).getMatrix();
			if( om != null ) {
				for(OrgMatrixItem oi : om.items)
					if( oi.id.equals(p.id) )
						return oi.cost;
				return 0;
			}
		}
		if(doc instanceof OrderImpl) {
			OrderEx oe = (OrderEx) doc.getData();
			if(oe.whIndex > 0) {
				PriceBase pb = (PriceBase)p;
				List<PriceQtyItem> wq = pb.getWhQty();
				if(oe.whIndex <= wq.size()) {
					return wq.get(oe.whIndex - 1).cost;
				}
				return 0;
			}
		}
		return super.getItemCost(p, doc);
	}
}
