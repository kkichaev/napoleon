package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

public class CostStrategyEx extends CostStrategy {
	static String id = "";
	static Map<String, Integer> folderDiscounts = new HashMap<String, Integer>();
	static Map<String, Integer> priceDiscounts = new HashMap<String, Integer>();
	static FolderTree folders = null;
	
	static void restCache() {
		id = "";
		folders = null;
	}
	
	void loadData(String oid) {
		if(folders == null) {
			folders = new FolderTree();
			folders.load();
		}
		
		if(id.equals(oid) == false) {
			OrgImpl oi = new OrgImpl();
			OrgEx org = (OrgEx)oi.getData();
			org.id = oid;
			oi.read();
			oi.close();
			
			id = oid;
			
			folderDiscounts.clear();
			priceDiscounts.clear();
			
			for(OrgDiscount od : org.discounts) {
				if(od.type == OrgDiscount.FOLDER_TYPE) {
					folderDiscounts.put(od.id, od.discount);
				} else {
					priceDiscounts.put(od.id, od.discount);
				}
			}
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc); 
		if(doc != null) {
			loadData(doc.getId());
			Integer dsc = priceDiscounts.get(p.id);
			if(dsc == null) {
				Folder f = folders.getFolder(p.folderID);
				while(f != null) {
					dsc = folderDiscounts.get(f.fid);
					if(dsc != null)
						break;
					f = folders.getParent(f);
				}
			}
			if(dsc != null)
				cost = costWithDiscount(cost, dsc, Consts.SUM_SCALE);
		}
		return cost;
	}
}
