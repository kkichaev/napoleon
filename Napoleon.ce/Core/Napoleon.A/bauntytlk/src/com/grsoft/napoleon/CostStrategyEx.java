package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderDiscount;
import com.grsoft.dataobjects.OrgPriceCost;
import com.grsoft.dataobjects.PayType;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PayTypeImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	private static HashMap<DiscountKey, Integer> discountCash = new HashMap<DiscountKey, Integer>();
	static FolderTree folders;
	
	static OrgEx org = null;

	public CostStrategyEx() {
		refreshCash();
	}

	public static void refreshCash() {
		org = null;
		refreshDiscounts();
	}
	
	static void refreshDiscounts() {
		discountCash.clear();

		FolderDiscount fdsc = new FolderDiscount();
		DbReader r = new DbReader();
		boolean bdo = r.select(fdsc, fdsc.getTableName(), null);
		while (bdo) {
			DiscountKey key = new DiscountKey(fdsc.folderID, fdsc.category);
			discountCash.put(key, fdsc.discount);

			bdo = r.selectNext(fdsc);
		}
		r.close();
	}
	
	static void refreshOrg(String id) {
		if(org == null || org.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx) oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
	}

	public int getBaseCost(Price p, Document<?> doc) {
		return super.getItemCost(p, doc);
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);

		if (doc != null) {
			refreshOrg(doc.getId());
			int cs = getFixedOrgCost(p, org, result);
			if(cs != 0)
				return cs;
			
			result = getCostWhithoutPayType(p, org, result);
			PayTypeImpl pt = new PayTypeImpl();
			PayType ptd = pt.getData();
			String t = org.paytype;
			ptd.id = t;
			ptd.category = org.category;
			if(pt.read() && ptd.discount != 0) {
				int baseCost = super.getItemCost(p, doc);
				result += (costWithDiscount(baseCost, ptd.discount, Consts.SUM_SCALE) - baseCost);
			}
			pt.close();
		}
		return result;
	}

	static int findOrgDiacount(int fid, OrgEx o) {
		int dsc = 0;
		
		getFolders();
		Folder fld = folders.getFolder(fid);

		while (fld != null) {
			
			fid = fld.id;
			for(OrgFolderDiscount ofd : o.fldDsc) {
				if(ofd.folderID == fid) {
					return ofd.discount;
				}
			}
			
			fld = folders.getParent(fld);
		}
		
		
		return dsc;
	}
	
	public int getFixedOrgCost(Price p, OrgEx org, int baseCost) {
		for(OrgPriceCost opc : org.prcCost) {
			if(opc.id.equals(p.id)) {
				return opc.cost;
			}
		}
		
		int dsc = findOrgDiacount(p.folderID, org);
		if(dsc == 0)
			return 0;
		return costWithDiscount(baseCost, dsc, Consts.SUM_SCALE);
	}
	
	public int getCostWhithoutPayType(Price p, OrgEx org, int baseCost) {
		int result = baseCost;

		DiscountKey dk = new DiscountKey(p.folderID, org.category);
		Integer dsc = discountCash.get(dk);
		if(dsc == null) {
			dsc = findDiscount(p.folderID, org.category);
			discountCash.put(dk, dsc);
		}
		dsc += org.discount;

		if(dsc != 0)
			result = costWithDiscount(result, dsc, Consts.SUM_SCALE);
		
		return result;
	}
	
	static int findDiscount(int fid, String category) {
		getFolders();
		Folder fld = folders.getFolder(fid);

		while (fld != null) {
			DiscountKey dk = new DiscountKey(fld.id, category);
			Integer dsc = discountCash.get(dk);
			if(dsc != null)
				return dsc;
			fld = folders.getParent(fld);
		}

		return 0;
	}

	static FolderTree getFolders() {
		if (folders == null) {
			folders = new FolderTree();
			folders.load();
		}

		return folders;
	}
}

class DiscountKey {
	int folderID;
	String category;
	
	public DiscountKey(int folder, String category) {
		this.folderID = folder;
		this.category = category;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof DiscountKey) )
				return false;
		
		DiscountKey other = (DiscountKey)o;
		return folderID == other.folderID && category.equals(other.category);
	}
	
	@Override
	public int hashCode() {
		return folderID ^ category.hashCode();
	}
}
