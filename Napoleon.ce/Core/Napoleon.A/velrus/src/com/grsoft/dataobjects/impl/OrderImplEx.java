package com.grsoft.dataobjects.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;

public class OrderImplEx extends OrderImpl {
	int whIndex = -1; 
	
	int getWhIndex() {
		int index = -1;
		
		OrgImpl oi = new OrgImpl();
		oi.read("id", data.id);
		oi.close();
		
		index = ((OrgEx)oi.getData()).sklad;
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();
		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		
		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}
	
	private static class Res{
		public boolean val = false;
	}
	
	@Override
	public long write() {
		if(data.id.length() > 0){
			OrgImpl org = new OrgImpl();
			
			if(org.read("id", data.id)){
				OrgEx o = (OrgEx) org.getData();
				
				if(o.matrix == null || o.matrix.size() == 0){
					final Res f = new Res();
					final Map<Integer, FolderEx> folders = new HashMap<Integer, FolderEx>();
					DataTraveler.travel(FolderEx.class, new DataTraveler.Travel<FolderEx>(){

						@Override
						public boolean travel(DataTraveler<FolderEx> item) {
							if(!folders.containsKey(item.data.id)){
								folders.put(item.data.id, item.data);
								
								if(!f.val)
									f.val = item.data.required > 0;
									
								item.data = new FolderEx();	
							}
							return true;
						}}, null);
					
					if(f.val){
						boolean notcomplete = true;
						PriceImpl price = new PriceImpl();
						
						for(OrderItem i : data.items){
							if(price.read("id", i.id) && folders.containsKey(price.getData().folderID))
								notcomplete = folders.get(price.getData().folderID).required == 0;
								
							if (!notcomplete)
								break;
						}
						
						((OrderEx)data).notcomplete = notcomplete ? 1 : 0;
					}
				}
			}
		}
		
		return super.write();
	}
}
