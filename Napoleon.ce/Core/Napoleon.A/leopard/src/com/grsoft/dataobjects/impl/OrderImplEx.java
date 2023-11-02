package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PackItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sklad;

public class OrderImplEx extends OrderImpl {
	SkladImpl skladImpl = new SkladImpl();
	
	@Override
	public int getItemValue(Price item) {
		int result = 0;
		String whCode = ((OrderEx)data).whCode;
		
		if (whCode.trim().length() == 0)
			whCode = getDafaultSkladId();
		
		if (whCode.trim().length() > 0)
			for(PackItem pi : ((PriceEx)item).packs)
				if(pi.warehouse.equals(whCode))
					result += pi.qty;
					
		return result;
	}
	
	public static String getDafaultSkladId(){
		String result = "";
		Cursor<Sklad> cursor = null;
		try{
			cursor = new Cursor<Sklad>(new SkladImpl());
			if(cursor.getCount() > 0){
				SkladImpl skladImpl = (SkladImpl) cursor.get(0);
				
				if (skladImpl != null)
					result = skladImpl.getData().id;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor != null)
				cursor.close();
		}
		
		return result;
	}
	
	public int getItemValue(Price item, String pack) {
		int result = 0;
		PackItem packItem = null;
		
		packItem = getPackItem(item, pack);
		
		if(packItem != null)
			result = packItem.qty;
		
		return result;
	}

	public PackItem getPackItem(Price item, String pack) {
		PackItem result = null;
		String whCode = ((OrderEx)data).whCode;
		boolean whInPacks = false;
		
		if (whCode.trim().length() == 0)
			whCode = getDafaultSkladId();
		else{
			skladImpl.getData().id = whCode;
			skladImpl.read();
			skladImpl.close();
			whInPacks = skladImpl.isCheckPack();
		}
		
		
		for(PackItem pi : ((PriceEx)item).packs){
			if(pi.warehouse.equals(whCode)){
				if(whInPacks && pack.equals(pi.pack) ||(!whInPacks && 
						((pi.flags & PackItem.MAIN) == PackItem.MAIN))){
					result = pi;
					break;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost,
			boolean inPack) {
		OrderItemEx item = (OrderItemEx) findUpdateItem(priceImpl.getData());
		return updateQtyEx(priceImpl, qty, cost, inPack, item.pack);
	}
	
	@Override
	public boolean delete() {
		ArrayList<OrderItem> copy = new ArrayList<OrderItem>();
		copy.addAll(data.items);
		PriceImpl priceImpl = new PriceImpl();
		
		for(OrderItem item : copy){
			priceImpl.getData().id = item.id;
			if(priceImpl.read())
				updateQty(priceImpl, 0, 0, false);
		}
		
		priceImpl.close();
		
		return true;
	}
	
	public boolean updateQtyEx(PriceImpl priceImpl, int qty, int cost, boolean inPack,
			String pack) {	
		PriceEx price = (PriceEx) priceImpl.getData();
		boolean ret = true;
		OrderItemEx item = (OrderItemEx) findUpdateItem(price);
		PackItem packItem = getPackItem(priceImpl.getData(), pack);
		
		if (packItem != null){
			int priceUpdate = 0;
			if( checkPriceQty() ) {
				if( packItem.qty < qty ) {
					ret = false;			
					qty = packItem.qty;
				}
			}
	
			boolean needUpdate = true;
			if( item == null ) // new item
			{
				if( qty > 0 )
				{
					item = new OrderItemEx();
				
					item.cost = cost;
					item.id = price.id;
					item.qty = qty;
					item.pack = pack;
					
					if(inPack) item.flags |= OrderItem.IN_PACK;
			
					data.items.add(item);
					priceUpdate = - qty;
				} else
					needUpdate = false;
			} else
			{
				priceUpdate = item.qty;
				
				if( qty == 0 ) {
					data.items.remove(item);
				}
				else {
					if( item.qty != qty ) {
						item.qty = qty;
						item.cost = cost;
						item.pack = pack;
						if(inPack) item.flags |= OrderItem.IN_PACK;
						else item.flags &= (~OrderItem.IN_PACK);
						
						priceUpdate -= qty;
					} else if( item.cost != cost ) {
						item.cost = cost;					
					} else
						needUpdate = false;
				}
			}
			
			if( needUpdate ) {
				if( qty != 0 )
					beforeItemWrite(item, price);
				
				write();
				
				if( priceUpdate != 0 && checkPriceQty() ){
					packItem.qty += priceUpdate;
					priceImpl.write();
				}
				
				// refresh sum after writing
				getDocumentType().refreshDocSum(data.id);
			}
		}
		return ret;
	}
}
