package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	DeliveryImpl dlv; 
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof ReturnImplEx) {
			ReturnEx re = (ReturnEx)doc.getData();
			if( dlv == null || dlv.getData().number.equals(re.dlvNum) == false ) {
				dlv = new DeliveryImpl();
				Delivery d = dlv.getData();
				d.id = re.id;
				d.number = re.dlvNum;
				dlv.read();
				dlv.close();
			}
			
			for(DeliveryItem di : dlv.getData().items)
				if(di.id.equals(p.id))
					return (int)(((long)di.sum * Consts.QTY_SCALE / di.qty));
		}
		return super.getItemCost(p, doc);
	}
}
