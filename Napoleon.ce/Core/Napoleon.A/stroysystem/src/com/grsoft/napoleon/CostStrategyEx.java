package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

public class CostStrategyEx extends CostStrategy {
	
	static FolderTree tree = null;
	static HashMap<String, Integer> itemDiscount = null;
	static HashMap<String, Integer> folderDiscount = null;
	static String curId = "";
	
	public static void clearCache() {
		tree = null;
		itemDiscount = null;
		folderDiscount = null;
		curId = "";
	}
	
	void load(String id) {		
		if(tree == null) {
			tree = new FolderTree();
			tree.load();
		}
		
		if(itemDiscount == null)
			itemDiscount = new HashMap<String, Integer>();
		if(folderDiscount == null)
			folderDiscount = new HashMap<String, Integer>();
		
		if(curId.equals(id))
			return;
		
		curId = id;
		DataTraveler.travel(OrgDiscount.class, new DataTraveler.Travel<OrgDiscount>() {

			@Override
			public boolean travel(DataTraveler<OrgDiscount> item) {
				if(item.data.isFolder == 0) {
					itemDiscount.put(item.data.idItem, item.data.discount);
				} else {
					folderDiscount.put(item.data.idItem, item.data.discount);
				}
				return true;
			}
		}, "id='" + id + "'");
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int res = super.getItemCost(p, doc);
		int discount = getDiscount(p, doc);
		if(discount != 0)
			res = costWithDiscount(res, discount, Consts.SUM_SCALE);
		return res;
	}
	
	public int getCostWODiscount(Price p, Document<?> doc) { return super.getItemCost(p, doc); }
	
	public int getDiscount(Price price, Document<?> doc) {
		if(doc == null)
			return 0;
		
		load(doc.getId());
		Integer dsc = itemDiscount.get(price.id);
		if(dsc != null)
			return dsc;
		
		int pos = tree.findFolder(price.folderID);
		if(pos>=0) {
			Folder f = tree.get(pos);
			do {
				dsc = folderDiscount.get(f.fid);
				if(dsc != null)
					return dsc;
				f = tree.getParent(f);
			}while(f != null);
		}
		
		return 0;
	}
}
