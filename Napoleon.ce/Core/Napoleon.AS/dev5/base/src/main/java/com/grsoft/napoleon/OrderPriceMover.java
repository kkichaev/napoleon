package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;

public class OrderPriceMover implements PriceMover {

	ArrayList<Long> items = new ArrayList<Long>();
	
	public OrderPriceMover(OrderImplBase<? extends Order> doc) {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();

		for(OrderItem oi : doc.getData().items) {
			p.id = oi.id;
			if( pi.read() )
				items.add(pi.getRowid());
		}
		
		pi.close();
	}
	
	@Override
	public PriceImpl move(PriceImpl price, boolean next) {
		int id = items.indexOf(price.getRowid());
		if( id < 0 )
			return null;
		
		if( next ) id++;
		else id--;
		if( id < 0 || id >= items.size() )
			return null;
		
		PriceImpl pi = new PriceImpl();
		pi.read(items.get(id));
		return pi;
	}
}
