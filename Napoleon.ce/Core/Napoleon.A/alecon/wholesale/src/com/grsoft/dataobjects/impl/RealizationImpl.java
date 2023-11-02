package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Realization;
import com.grsoft.napoleon.RealizationDetail;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

public class RealizationImpl extends Document<Realization> {

	@Override
	public void open(Context context) {
		RealizationDetail.open(context, this); 
	}
	
	@Override
	public long sum() { return data.sumD; }
	
	public int weight() {
		int res = 0;
		
		if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			Price pd = p.getData();
			for (DeliveryItem item: data.items) {
				pd.id = item.id;
				if( p.read() )
					res += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
			}
			p.close();
		}
		// переведем вес в килограммы
		return res;
	}
	
	@Override
	public String getDescription(Context context) { return data.number; }
}
