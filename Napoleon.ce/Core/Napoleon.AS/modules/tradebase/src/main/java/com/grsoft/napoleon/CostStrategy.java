package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderDiscount;
import com.grsoft.dataobjects.OrgPriceCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

public class CostStrategy {
	public static CostStrategy defaultInstance = new CostStrategy();
	static protected Org org = null;
	private static HashMap<DiscountKey, Integer> discountCash = new HashMap<>();
	static FolderTree folders;

	private static Map<Class<? extends Document<?>>, CostStrategy> strategies = 
		new HashMap<Class<? extends Document<?>>,CostStrategy>();
	
	public static CostStrategy getInstance(Class<? extends Document<?>> doc){
		CostStrategy result = strategies.get(doc);
		
		if (result == null)
			result = defaultInstance;
		
		return result;
	}

	public static void register(Class<? extends Document<?>> doc, CostStrategy strategy){
		strategies.put(doc, strategy);
	}
	
	/**
	 * Чтобы в каждом проекте не считать скидку - добавил в базоый класс
	 * @param cost
	 * @param discount
	 * @param discountScale
	 * @return
	 */
//	public static long costWithDiscount(int cost, long discount, int discountScale) {
//		double cd = (double)cost / Consts.SUM_SCALE;
//		double dsc = (double)discount / (discountScale * 100.0);
//		double sum = (cd * (1.0 - dsc) * Consts.SUM_SCALE) + 0.5;
//		return (long) sum;
////		int scale = discountScale * Consts.SUM_SCALE;
////		return cost - (int) (((long) cost * discount + scale / 2) / scale);
//	}

	public static long costWithDiscount(long cost, long discount, int discountScale) {
		double cd = (double)cost / Consts.SUM_SCALE;
		double dsc = (double)discount / (discountScale * 100.0);
		double sum = (cd * (1.0 - dsc) * Consts.SUM_SCALE) + 0.5;
		return (long) sum;
//		int scale = discountScale * Consts.SUM_SCALE; 
//		return cost - ((cost * discount + scale / 2) / scale);		
	}
	
	public long getItemCost(Price p, Document<?> doc){
		int sumType = doc != null ? doc.getSumType() : 0;
		return getCostInt(p, doc, sumType);
	}

	public static void refreshCash() {
		org = null;
		discountCash.clear();
		folders = null;
	}

	protected boolean refreshOrg(String id) {
		if(org == null || !org.id.equals(id)) {
			refreshCash();

			OrgImpl oi = new OrgImpl();
			org = oi.getData();
			org.id = id;
			oi.read();
			oi.close();
			return true;
		}
		return false;
	}

	public long getCostInt(Price p, Document<?> doc, int sumType) {
		long result = 0;

		if( Features.CAN_CHANGE_COST && doc != null && doc instanceof OrderImplBase<?>) {
			OrderItem oi = (OrderItem)((OrderImplBase<?>)doc).findItem(p.id);
			if( oi != null )
				return oi.cost;
		}
		if( Features.COST_MANAGER != null ) {
			result = Features.COST_MANAGER.getCost(p.id, sumType);
		}
		
		if(result == 0){
			result = getPriceCost(p, sumType, doc);			
		}

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

					dsc = findOrgDiscount(p.folderID, org);
					if(dsc == 0)
						dsc = findDiscount(p.folderID, doc.getSumType());

					discountCash.put(dk, dsc);
				}

				result = costWithDiscount(result, dsc, Consts.SUM_SCALE);
			}
		}

		return result;
	}

	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		return (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? 
				p.cost.get(sumType).cost : 0;
	}

	public static FolderTree getFolders() {
		if (folders == null) {
			folders = new FolderTree();
			folders.load();
		}

		return folders;
	}

	static int findOrgDiscount(int fid, Org o) {
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

	static class DiscountKey {
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
}
