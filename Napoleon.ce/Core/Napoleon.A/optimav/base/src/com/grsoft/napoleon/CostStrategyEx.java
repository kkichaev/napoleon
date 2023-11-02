package com.grsoft.napoleon;

import java.util.Hashtable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	static Hashtable<String, Hashtable<Integer, CostData>> items = null;
	static FolderTree folders = null;
	
	public static void clearCache() {
		items = null;
		folders = null;
	}
	
	static void loadCache() {
		if( items == null ) {
			items = new Hashtable<String, Hashtable<Integer,CostData>>();
			OrgDiscount od = new OrgDiscount();
			String table = DataObjectInfo.getInstance().getTableName(od.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(od, table, null);
			while( bdo ) {
				Hashtable<Integer,CostData> v = new Hashtable<Integer, CostData>();
				for(OrgDiscountItem i : od.items)
					v.put(i.folderID, new CostData(i));
	
				items.put(od.id, v);
				bdo = r.selectNext(od);
			}
		}
		
		if( folders == null ) {
			folders = new FolderTree();
			folders.load();
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		loadCache();
		String id = (doc == null) ? "" : doc.getId();
		Hashtable<Integer,CostData> v = items.get(id);
		if( v != null ) {
			Folder fld = folders.getFolder(p.folderID);
			while( fld != null ) {
				CostData cd = v.get(fld.id);
				if( cd != null )
					return cd.getCost(p);
				fld = folders.getParent(fld);
			}
		}
		return super.getItemCost(p, doc);
	}
}

class CostData {
	public CostData(OrgDiscountItem item) {
		costype = item.costype;
		discount = item.discount;
	}
	public int getCost(Price p) {
		int cost =  (p.cost.size() > costype && costype >= 0) ? p.cost.get(costype).cost : 0;			
		if( discount != 0 ) {
			cost = CostStrategy.costWithDiscount(cost, discount, Consts.SUM_SCALE);
//			cost = cost - (int)(((long)cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		}
		return cost;
	}
	public int costype;
	public int discount;
}