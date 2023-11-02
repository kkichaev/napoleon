package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocType.CountTextResolver;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;

public class DocListEx extends DocList implements CountTextResolver {
	
	PriceImpl pi = new PriceImpl();
	
	int countDocTare(OrderEx doc) {
		long res = 0;
		PriceEx pe = (PriceEx) pi.getData();
		for(OrderItem oi : doc.items) {
			pe.id = oi.id;
			if(pi.read() && pe.tare > 0)
				res += (long)pe.tare * oi.qty; 
		}
		
		return (int)(res/ (Consts.QTY_SCALE * 500));
	}
	
	@Override
	protected void onStop() {
		pi.close();
		super.onStop();
	}
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if( DocType.getCurDoc() instanceof OrderDoc) {
			long sum = 0;
			int weight = 0;
			int count = 0;

			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				
				if(d != null){
					sum += getDocSum(d);
					weight += ((OrderImplBase<?>)d).weight();
					count += countDocTare((OrderEx) d.getData());
				}
			}
			
			DocType.getCurDoc().updateTotalSum(this, sum, weight, count, R.id.tvDocSum);
			return;
		}
		super.refreshTotalSum(useFilter);
	}

	@Override
	public String getCountText() {
		return "бут.";
	}
}
