package com.grsoft.napoleon;

import com.grsoft.dataobjects.Party;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.PriceImpl;

public class VanRestReportEx extends VanRestReport {
	@Override
	protected void buildData() {
		try{
			Cursor<Price> c = new Cursor<Price>(new PriceImpl(),"","name");
			
			while(c.moveNext()){
				PriceImpl p = (PriceImpl) c.current();
				PriceEx pe = (PriceEx)p.getData();
				
				if (pe.party != null && pe.party.size() > 0){
					RestData d = new RestData();
					d.name = pe.name;
					
					for(Party par: pe.party)
						d.qty += par.qty;
					
					data.add(d);
				}
			}
				
			c.close();
			super.buildData();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
