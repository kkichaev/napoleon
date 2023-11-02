package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.Set;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org2Ex;
import com.grsoft.dataobjects.OrgSegmentItem;
import com.grsoft.dataobjects.Price2Ex;
import com.grsoft.dataobjects.PriceSegmentItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SkladHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

import android.widget.BaseAdapter;

public class Warehouse2Ex extends WarehouseEx {
	public Set<String> orgSegments = new HashSet<String>();
	private String orgid = null;
	private static DocType prevDocType = null;
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = createAdapterInstance();
		if (Features.SHOW_ZERO_FILTER)
			ret.putFilter(createZeroPositionFilter());
		
		if (prevDocType != null && 
				((prevDocType != OrderDoc.instance() && DocType.getCurDoc() == OrderDoc.instance())) || 
				((prevDocType == OrderDoc.instance() && DocType.getCurDoc() != OrderDoc.instance()))
				) 
			FoldersAdapter.resetCache();

		prevDocType = DocType.getCurDoc();
		
		if(DocType.getCurDoc() == OrderDoc.instance() &&
				document.getRowid() != ExtrasConst.INVALID_ROWID) {
			if(orgid == null || !orgid.equals(document.getId())) {
				FoldersAdapter.resetCache();
				orgid = document.getId();
			}
			
			if (SkladHelper.useDiscount(((OrderEx)document.getData()).whCode)) 
				ret.putFilter(new Filter("SEGMENT") {
					
					@Override
					public boolean inset(long priceRowID, String id) {
						boolean result = false;
						price.read(priceRowID);
						for(String osgmid : CostStrategyEx.getOrgSegments(document.getId())) {
							for(PriceSegmentItem psi : ((Price2Ex) price.getData()).segments) {
								result = CostStrategyEx.haveDiscount(osgmid, psi.sgmid);
								
								if (result)
									break;
							}
							
							if (result)
								break;
						}
						
						return result;
					}
				});
		}
		
		return ret;
	}
	
	@Override
	protected void postDocInited() {
		super.postDocInited();
		
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		
		orgSegments.clear();
		
		for(OrgSegmentItem i : ((Org2Ex)org.getData()).segments)
			orgSegments.add(i.sgmid);
	}
}
