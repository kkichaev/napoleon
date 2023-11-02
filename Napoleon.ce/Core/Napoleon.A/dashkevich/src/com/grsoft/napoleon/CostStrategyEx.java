package com.grsoft.napoleon;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
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
		if( doc != null) {
			int discount = 0;
			refreshCach(doc.getId());
			
			for(OrgDiscountItem pdi : org.priceDsc) {
				if(pdi.id.equals(p.id)) {
					discount = pdi.discount;
					break;
				}
			}
			
			if(discount == 0) {
				Folder fld = folders.getFolder(p.folderID);
				while(fld != null) {
					for(OrgDiscountItem odi : org.folderDsc) {
						if(fld.fid.equals(odi.id)) {
							discount = odi.discount;
							break;
						}
					}
					fld = folders.getParent(fld);
				}
			}
			if( discount != 0 )
				cost = costWithDiscount(cost, discount, Consts.SUM_SCALE);
		}
		return cost;
	}
}
