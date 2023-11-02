package com.grsoft.napoleon;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgNacenItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

public class CostStrategyEx extends CostStrategy {
	static FolderTree folders;
	static OrgEx org;

	public CostStrategyEx() {}
	
	public static void resetCache() {
		folders = null;
		org = null;
	}
	
	void refreshCach(String id) {
		if( org == null || org.id.equals(id) == false ) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx)oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
		if(folders == null) {
			folders = new FolderTree();
			folders.load();
		}
	}

	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);
		if(doc != null) {
			
			boolean updated = false;
			refreshCach(doc.getId());
			
			int nac = org.nac;
			Folder fld = folders.getFolder(p.folderID);
			while(fld != null) {
				for(OrgNacenItem ni : org.nacen) {
					if(fld.fid.equals(ni.id)) {
						nac = ni.nac;
						break;
					}
				}
				fld = folders.getParent(fld);
			}
			cost = costWithDiscount(cost, -nac, Consts.SUM_SCALE);
			
			if(((PriceEx)p).noDsc == 0 ) {
				for(OrgDiscountItem odi : org.price) {
					if(odi.id.equals(p.id)) {
						if(odi.discount != 0)
							cost = costWithDiscount(cost, odi.discount, Consts.SUM_SCALE);
						updated = true;
						break;
					}
				}
				if( updated == false ) {
					fld = folders.getFolder(p.folderID);
					while(!updated && fld != null) {
						for(OrgDiscountItem odi : org.discount) {
							if(fld.fid.equals(odi.id)) {
								cost = costWithDiscount(cost, odi.discount, Consts.SUM_SCALE);
								updated = true;
								break;
							}
						}
						fld = folders.getParent(fld);
					}
				}
			}
		}
		cost = ((cost + 9) / 10) * 10; 
		return cost;
	}
}
