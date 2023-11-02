package com.grsoft.napoleon;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.AliantaOffer;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.impl.PriceImpl;

public class OfferPrintData {
	
	public String agentPhone = "";
	
	public List<OfferItemDS> items = new ArrayList<OfferItemDS>();
	
	public OfferPrintData(Context context, AliantaOffer src) {
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx) pi.getData();


		for(OfferItem oi : src.items) {
			pe.id = oi.id;
			pi.read();

			OfferItemDS ods = new OfferItemDS(new OfferItemPrintData(context, pe, oi));
			items.add(ods);
		}
		
		AgentInfo ai = new AgentInfo();
		DbReader r = new DbReader();
		if(r.select(ai,ai.getTableName(),""))
			agentPhone = ai.phone;
		r.close();
		
		pi.close();
	}
}
