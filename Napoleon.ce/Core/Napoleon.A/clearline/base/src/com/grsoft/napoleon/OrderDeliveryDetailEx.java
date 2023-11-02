package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override
	protected void init() {
		DeliveryImpl delivery = new DeliveryImpl();
		Delivery delData = delivery.getData();

		Order ordData = doc.getData();
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = ordData.id;
		oi.read();
		oi.close();
		
		String where = "ido='" + oe.ido + "' and number='" + ordData.number + "'";
		List<Long> ids = DbReader.readIds(delData.getTableName(), where, "");
		
		if( ids.size() > 0) {
			delivery.read(ids.get(0));
			items = delivery.getData().items;
		}
	}
}
