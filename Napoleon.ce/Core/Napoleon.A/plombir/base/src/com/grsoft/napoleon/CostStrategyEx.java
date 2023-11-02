package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.IMatrix;
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
		return super.getItemCost(p, doc);
	}
}
