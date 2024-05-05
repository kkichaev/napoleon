package com.grsoft.napoleon;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
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
	public long getCostInt(Price p, Document<?> doc, int sumType) {
		long cost = super.getCostInt(p, doc, sumType);
		if( ((PriceEx)p).noDiscount != 1 && doc != null) {
			boolean updated = false;
			refreshCach(doc.getId());
			Folder fld = folders.getFolder(p.folderID);
			while(fld != null) {
				for(OrgDiscountItem odi : org.folderDsc) {
					if(fld.fid.equals(odi.id)) {
						cost = costWithDiscount(cost, odi.discount, Consts.SUM_SCALE);
						updated = true;
						break;
					}
				}
				fld = folders.getParent(fld);
			}			
			if( !updated )
				cost = costWithDiscount(cost, org.discount, Consts.SUM_SCALE);

			for(OrgDiscountItem odi : org.priceDsc) {
				if(odi.id.equals(p.id)) {
					cost = costWithDiscount(cost, odi.discount, Consts.SUM_SCALE);
					break;
				}
			}
		}
		return cost;
	}
}
