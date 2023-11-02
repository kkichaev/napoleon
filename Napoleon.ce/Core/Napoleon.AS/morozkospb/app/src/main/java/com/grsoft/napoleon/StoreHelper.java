package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.impl.PriceQtyImpl;

public class StoreHelper {
	static HashMap<String, Integer> qtys = null;
	static String curId = "";
	static PriceQtyImpl priceQtyImpl = new PriceQtyImpl();
	
	public static void clearCache() { qtys = null; }
	
	static void loadQtys(String idStore) {
		if( qtys == null || curId.equals(idStore) == false ) {
			String ids = idStore;
			if(ids.length() == 0) 
				ids = getFirstStore();
			
			qtys = new HashMap<String, Integer>();
			DataTraveler.travel(PriceQty.class, new DataTraveler.Travel<PriceQty>() {

				@Override
				public boolean travel(DataTraveler<PriceQty> item) {
					qtys.put(item.data.id, item.data.qty);
					return true;
				}
			}, "idStore='" + ids + "'");
		
			curId = idStore;
		}
	}
	
	private static String getFirstStore() {
		StoreFinder sf = new StoreFinder();
		DataTraveler.travel(Store.class, sf, "", "\"index\"");
		return sf.ret;
	}

	public static int getQty(String idStore, String id) {
		loadQtys(idStore);
		
		Integer val = qtys.get(id);
		return val == null ? 0 : val;
	}
	
	public static void updateQty(String idStore, String id, int qty) {
		if(idStore.length() == 0)
			return;
		
		PriceQty p = priceQtyImpl.getData();
		p.id = id;
		p.idStore = idStore;
		priceQtyImpl.read();
		p.qty += qty;
		priceQtyImpl.write();
	}
}

class StoreFinder extends DataTraveler.Travel<Store> {
	public String ret;
	
	public StoreFinder() { ret = ""; }

	@Override
	public boolean travel(DataTraveler<Store> item) {
		ret = item.data.id;
		return false;
	}
}