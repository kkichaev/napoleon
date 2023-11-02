package com.grsoft.napoleon;

import java.util.HashMap;
import android.annotation.SuppressLint;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer, Integer> discountCash = new HashMap<Integer, Integer>();
	static String id = null;

	public CostStrategyEx() { }

	public static void refreshCash(String orgId) {
		if( orgId.equals(id))
			return;
		
		id = orgId;
		discountCash.clear();

		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = orgId;
		oi.read();
		oi.close();
		
		final HashMap<String, Integer> folders = new HashMap<String, Integer>();
		DataTraveler.travel(Folder.class, new DataTraveler.Travel<Folder>() {

			@Override
			public boolean travel(DataTraveler<Folder> item) {
				folders.put(item.data.fid, item.data.id);
				return true;
			}
		}, null);
		
		for (OrgDiscountItem i : org.discount) {
			Integer id = folders.get(i.fid);
			if( id != null)
				discountCash.put(id, i.discount);
		}
	}

	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);
		if( doc != null)
			refreshCash(doc.getId());

		Integer dsc = discountCash.get(p.folderID);
		if( dsc != null )
			result -= (int) (((long) result * dsc + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		
		return result;
	}
}
