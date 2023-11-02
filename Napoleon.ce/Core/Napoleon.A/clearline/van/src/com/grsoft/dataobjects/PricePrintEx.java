package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PricePrintEx extends PricePrint {
	public String gost = "";
	public String cert = "";
	public String stcond = "";
	public String bestBfr = "";
	public String barcode = "";
	public String barcodePack = "";
	public String barcodeType = "";
	
	public List<PriceSalesQty> party = new ArrayList<PriceSalesQty>();
	
	public int partyQty() {
		int vq = 0;
		for(PriceSalesQty psq : party)
			vq += psq.qty;
		
		return vq;
	}
	
	public List<PriceSalesQty> distrubuteFIFO(int qty) {
		List<PriceSalesQty> ret = new ArrayList<PriceSalesQty>();

		if(qty > 0) {
			for(PriceSalesQty psq : party) {
				if(psq.qty == 0 || psq.isFake())
					continue;
				int rmvQty = psq.qty < qty ? psq.qty : qty;
				PriceSalesQty addPsq = new PriceSalesQty();
				addPsq.date = psq.date;
				addPsq.qty = rmvQty;
				ret.add(addPsq);
				
				qty -= rmvQty;
				if(qty <= 0)
					break;
			}
		}
		return ret;
	}

	public void remove(List<PriceSalesQty> rmv) {
		List<PriceSalesQty> rmvParty = new ArrayList<PriceSalesQty>();
		for(PriceSalesQty psq : rmv) {
			for(int i=0; i<party.size(); i++) {
				PriceSalesQty dest = party.get(i);
				int cmp = dest.date.compareTo(psq.date);
				if(cmp == 0) {
					dest.qty -= psq.qty;
					if(dest.qty <= 0) 
						rmvParty.add(dest);
					break;
				}
			}
		}
		party.removeAll(rmvParty);
		Collections.sort(party);
		vanQty = partyQty();
	}

	public void add(List<PriceSalesQty> rmv) {
		for(PriceSalesQty psq : rmv) {
			int i = 0;
			boolean updated = false;
			for(; i<party.size(); i++) {
				PriceSalesQty dest = party.get(i);
				int cmp = dest.date.compareTo(psq.date);
				if(cmp == 0) {
					dest.qty += psq.qty;
					updated = true;
					break;
				} else if(cmp > 0) { 
					break;
				}
			}
			if(!updated) {
				PriceSalesQty addPsq = new PriceSalesQty();
				addPsq.date = psq.date;
				addPsq.qty = psq.qty;
				party.add(addPsq);
			}
		}
		Collections.sort(party);
		vanQty = partyQty();
	}
}
