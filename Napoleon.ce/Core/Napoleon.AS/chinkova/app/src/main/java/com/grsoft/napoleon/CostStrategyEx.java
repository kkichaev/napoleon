package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceData;
import com.grsoft.dataobjects.PriceDataItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static PriceData data = null;
	
	static public void clearCache() {
		data = null;
	}
	
	static void loadCache(String priceType) {
		if( data == null || data.idType.equals(priceType) == false ) {
			DbReader r = new DbReader();
			data = new PriceData();
			r.select(data, data.getTableName(), "idType='" + priceType + "'");
			r.close();
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(doc instanceof OrderImplEx || doc instanceof ReturnImplEx) {
			String priceType = "";
			if(doc instanceof OrderImplEx)
				priceType = ((OrderEx)((OrderImplEx)doc).getData()).priceType;
			else 
				priceType = ((ReturnImplEx)doc).getData().prcType;
			
			loadCache(priceType);
			for(PriceDataItem pdi : data.items) {
				if(pdi.id.equals(p.id))
					return pdi.cost;
			}
		} 
		return super.getItemCost(p, doc);
	}
}
