package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	int discount = 0;
	String id = "";

	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = 0;
		if (doc instanceof ReturnImpl) {
			ReturnEx re = (ReturnEx) doc.getData();
			DeliveryImpl dlv = new DeliveryImpl();
			Delivery d = dlv.getData();
			d.id = re.id;
			d.number = re.dlvNum;
			
			if(dlv.read())
				for(DeliveryItem i: d.items)
					if(i.id.equals(p.id))
						cost = ((DeliveryItemEx)i).cost;
			
			dlv.close();
		} else {

			String docId = (doc == null) ? "" : doc.getId();
			if (docId.compareTo(id) != 0) {
				OrgImpl o = new OrgImpl();
				OrgEx oe = (OrgEx) o.getData();
				oe.id = docId;
				if (o.read())
					discount = -oe.discount;
				else
					discount = 0;
				id = docId;
			}

			cost = super.getItemCost(p, doc);
			if (discount != 0) {
				int sign = (int) Math.signum(discount);
				cost += ((int) ((long) cost * Math.abs(discount) + Consts.SUM_SCALE
						* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE))
						* sign;
			}

		}

		return cost;
	}

}
