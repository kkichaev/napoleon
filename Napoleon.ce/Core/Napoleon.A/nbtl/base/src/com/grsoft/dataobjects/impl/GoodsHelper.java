package com.grsoft.dataobjects.impl;

import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

class GoodsHelper
{
	public static <T> void fillDocItems(String orgId, final List<T> items, final Class<?> type){
		OrgImpl org = new OrgImpl();
		org.read("id", orgId);
		String omtx = ((OrgEx)org.getData()).goodsMatrix;
		GoodsMatrixImpl matrix = new GoodsMatrixImpl();
		
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx)pi.getData();
		
		if(omtx.trim().length() > 0 && matrix.read("name", omtx)){
			for(MatrixItem mi : matrix.getData().items) {
				pe.id = mi.id;
				if(pi.read() && pe.my > 0)
					AddNewItem(mi.id, items, type);
			}
		}else{
			DataTraveler.travel(Price.class, new DataTraveler.Travel<Price>(){
				@Override public boolean travel(DataTraveler<Price> item) {
					return AddNewItem(item.data.id, items, type);
				}
			}, "isGoods=1 and my=1");
		}
	
		pi.close();
	}
	
	private static <T>boolean AddNewItem(String id, List<T> items, Class<?> type){
		boolean result = false;
		
		try{
			T i = (T)type.newInstance();
			type.getField("id").set(i, id);
			items.add(i);
			
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}