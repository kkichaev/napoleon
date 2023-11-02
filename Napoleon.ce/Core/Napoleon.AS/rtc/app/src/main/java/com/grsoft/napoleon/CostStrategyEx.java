package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderDiscount;
import com.grsoft.dataobjects.OrgPriceCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

public class CostStrategyEx extends CostStrategy {
	private static HashMap<DiscountKey, Integer> discountCash = new HashMap<DiscountKey, Integer>();
	static FolderTree folders;
	
	static OrgEx org = null;

	public CostStrategyEx() {
		refreshCash();
	}

	public static void refreshCash() {
		org = null;
		discountCash.clear();
	}
	
	static void refreshOrg(String id) {
		if(org == null || org.id.equals(id) == false) {
			refreshCash();

			OrgImpl oi = new OrgImpl();
			org = (OrgEx) oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
	}

	@Override
	public long getItemCost(Price p, Document<?> doc) {
		long result = super.getItemCost(p, doc);

		if (doc != null) {
			
			refreshOrg(doc.getId());
			
			if (doc != null) {
				for(OrgPriceCost opc : org.prcCost) {
					if(opc.id.equals(p.id)) {
						return opc.cost;
					}
				}
				
				DiscountKey dk = new DiscountKey(p.folderID, doc.getSumType());
				Integer dsc = discountCash.get(dk);
				if(dsc == null) {
					
					dsc = findOrgDiacount(p.folderID, org);
					if(dsc == 0)
						dsc = findDiscount(p.folderID, doc.getSumType());
					
					discountCash.put(dk, dsc);
				}
				
				result = costWithDiscount(result, dsc, Consts.SUM_SCALE);
			}
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
	

	static int findDiscount(int fid, int costype) {
		getFolders();
		Folder fld = folders.getFolder(fid);

		while (fld != null) {
			DiscountKey dk = new DiscountKey(fld.id, costype);
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
	int costype;
	
	public DiscountKey(int folder, int costype) {
		this.folderID = folder;
		this.costype = costype;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof DiscountKey) )
				return false;
		
		DiscountKey other = (DiscountKey)o;
		return folderID == other.folderID && costype == other.costype;
	}
	
	@Override
	public int hashCode() {
		return folderID ^ costype;
	}
}
