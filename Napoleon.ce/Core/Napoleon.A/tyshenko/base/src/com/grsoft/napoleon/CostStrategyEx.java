package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderDsc;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

@SuppressLint("UseSparseArrays")
public class CostStrategyEx extends CostStrategy {
	
	protected static final Object COMMON_ID = "1";
	static Map<Integer, Integer> dsc = new HashMap<Integer, Integer>();
	static Map<Integer, Integer> commonDsc = new HashMap<Integer, Integer>();
	static String orgId = "";
	static int orgDiscount;
	
	 public static void resetCache() {
		 dsc.clear();
		 commonDsc.clear();
		 orgId = "";
	 }
	 
	 static void load(String id) {
		 if(orgId.equals(id))
			 return;
		 
		 final Map<String, Integer> folders = new HashMap<String, Integer>();
		 DataTraveler.travel(Folder.class, new DataTraveler.Travel<Folder>() {

			@Override
			public boolean travel(DataTraveler<Folder> item) {
				folders.put(item.data.fid, item.data.id);
				return true;
			}
		}, "");
		 
		 OrgImpl oi = new OrgImpl();
		 final OrgEx oe = (OrgEx)oi.getData();
		 oe.id = id;
		 oi.read();
		 oi.close();
		 orgId = id;
		 orgDiscount = oe.discount;
		 
		 dsc.clear();
		 DataTraveler.travel(FolderDsc.class, new DataTraveler.Travel<FolderDsc>() {

			@Override
			public boolean travel(DataTraveler<FolderDsc> item) {
				Integer fid = folders.get(item.data.id);
				if(fid == null)
					return true;
				
				
				if(commonDsc.size() == 0 && item.data.type.equals(COMMON_ID)) {
					commonDsc.put(fid, item.data.discount);
				}
				if(item.data.type.equals(oe.orgType))
					dsc.put(fid, item.data.discount);
				return true;
			}
		}, "");

		 if(commonDsc.size() == 0) {
			 
		 }
	 }
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);
		if(doc == null || doc.getId().length() == 0)
			return cost;
		
		String id = "";
		if(doc != null)
			id = doc.getId();
		load(id);
		

		PriceEx pe = (PriceEx)p;
		
		int discount = 0;
		if( doc instanceof OrderImpl ) {
			if( pe.discount != 0 )
				discount = pe.discount;
			else {
				Integer dval1 = dsc.get(p.folderID);
				Integer dval2 = commonDsc.get(p.folderID);
				if(dval1 != null)
					discount = dval1;
				else {
					if(dval2 == null)
						dval2 = 0;
					if(dval2 == 0)
						discount = orgDiscount;
					else
						discount = Math.min(dval2, orgDiscount);
					if(discount == 0)
						discount = ((OrderEx)doc.getData()).discount;
				}
			}
		}
		if(discount != 0)
			cost = costWithDiscount(cost, discount, Consts.SUM_SCALE);
		return cost;
	}
}
