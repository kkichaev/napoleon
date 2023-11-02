package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	static String id = "";
	OrgMatrix matrix = null;
	
	public static void clearCache() {
		id = "";
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		
		if(id.equals(doc.getId()) == false ) {
			matrix = null;
			
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = doc.getId();
			oi.read();
			oi.close();
			
			if( oe.matrix.length() != 0) {
				OrgMatrixImpl mtx = new OrgMatrixImpl();
				matrix = mtx.getData();
				matrix.name = oe.matrix;
				if( !mtx.read() )
					matrix = null;
				mtx.close();
			}
		}
		
		int cost = super.getItemCost(p, doc);
		if( matrix != null ) {
			for(OrgMatrixItem omi : matrix.items)
				if( omi.id.equals(p.id) ) {
					cost = omi.cost;
					break;
				}
		}
		return cost;
	}
}
