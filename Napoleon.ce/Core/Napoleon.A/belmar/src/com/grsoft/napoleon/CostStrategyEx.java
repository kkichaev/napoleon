package com.grsoft.napoleon;

import java.util.Hashtable;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	Hashtable<Integer, Integer> discounts;
	Hashtable<String, Integer> folders;
	String id;
	
	public void resetCache() {
		id = null;
		discounts = null;
		folders =  null;
	}
	
	void loadFolders() {
		if(folders != null)
			return;
		
		folders = new Hashtable<String, Integer>();
		DataTraveler.travel(Folder.class, new DataTraveler.Travel<Folder>() {

			@Override
			public boolean travel(DataTraveler<Folder> item) {
				folders.put(item.data.fid, item.data.id);
				return true;
			}
			
		}, "");
	}
	
	void loadCache(String orgId) {
		if( id != null && id.equals(orgId) ) 
			return;
		
		loadFolders();
		
		discounts = new Hashtable<Integer, Integer>();
		id = orgId;
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		
		for(FolderDiscount fd : oe.discounts) {
			Integer fi = folders.get(fd.fid);
			if( fi != null )
				discounts.put(fi, fd.discount);
		}
		
		oi.close();
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		if( doc == null )
			return cost;
		
		loadCache(doc.getId());
		Integer discount = discounts.get(p.folderID);
		if( discount != null && discount != 0 )
			cost -= (int) (((long) cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));

		return cost;
	}
}
