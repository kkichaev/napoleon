package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PriceEx extends PricePrint {
	public List<Party> party;
	
	public Party[] getPartiesForSale(int qty){
		Party[] result = new Party[party.size()];
		
		Collections.sort(party, new Comparator<Party>(){
			public int compare(Party arg0, Party arg1) {
				return arg0.date.compareTo(arg1.date);
			}
		 });
		
		List<Party> out = new ArrayList<Party>();
		boolean error = false;
		
		for(Party p : party){
			
			if (qty < 0){
				error = true;
				break;	
			}else if (qty == 0)
				break;
				
			if (p.qty > 0){
				Party outParty = null;
				int oldQty = qty;
				
				try{
					outParty = (Party) p.clone();
				}catch(Exception e){
					continue;
				}
				
				if ((p.qty - qty) < 0){
					qty -= p.qty;
					p.qty = 0;
				}
				else{
					p.qty -= qty;
					qty = 0;
				}
				
				outParty.qty = oldQty - qty;
				out.add(outParty);
			}
		}
		
		if (!error)
			out.toArray(result);
		else
			result = null;
		
		return result;
	}
	
	public Party getParty(Date data, String owner){
		Party result = null;
		for(Party p : party){
			if (p.date.equals(data) ) {				
				if( p.owner.equals(owner) ) {
					result = p;
					break;
				}
			}
		}
		
		return result;
	}
}
