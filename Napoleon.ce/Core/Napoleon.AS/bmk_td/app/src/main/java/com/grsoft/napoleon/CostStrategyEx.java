package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Agreement;
import com.grsoft.dataobjects.AgreementItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgCosTypeItem;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	static OrgEx orgEx;

	static String agreementId = "";
	static Map<String, Integer> agrCost = new HashMap<>();

	//static FolderTree folders;

	public CostStrategyEx() {
		refreshCash();
	}

	static void loadAgr(String agrId) {
		if(!agreementId.equals(agrId)) {
			agreementId = agrId;
			agrCost.clear();
			for(Agreement a : DbReader.fetch(Agreement.class, String.format("id='%s'", agreementId))) {
				for(AgreementItem ai : a.items) {
					agrCost.put(ai.id, ai.cost);
				}
			}
		}
	}

	static void loadOrg(String orgId) {
		if(orgEx == null || orgEx.id.equals(orgId) == false) {
			OrgImpl oi = new OrgImpl();
			orgEx = (OrgEx) oi.getData();
			orgEx.id = orgId;
			oi.read();
			oi.close();
		}
	}

	public static void refreshCash() {
		agreementId = "";
		orgEx = null;
		folders = null;
	}

	@Override
	public long getItemCost(Price p, Document<?> doc) {
		int result = 0;
		int sumType = doc != null ? doc.getSumType() : 0;
		int discount = 0;

		if (doc != null) {
			if(doc instanceof OrderImpl) {
				String aid = ((OrderEx)doc.getData()).agrCode;
				loadAgr(aid);
				Integer c = agrCost.get(p.id);
				if (c != null) {
					return c;
				}
			}

			loadOrg(doc.getId());
			discount = findDiscount((OrgEx) orgEx, p.folderID);
			sumType = findSumType((OrgEx) orgEx, p.folderID, sumType);
		}

		result = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? p.cost
				.get(sumType).cost : 0;

		if (discount <= 0)
			result = (int)costWithDiscount(result, -discount, Consts.SUM_SCALE);
		else{
			double c = (double)result / Consts.SUM_SCALE;
			double d = (double)discount / Consts.SUM_SCALE;
			double v = c / (d / 100 + 1);
			result = (int) Math.round(v * Consts.SUM_SCALE);
		}

		// Округление до десятков копеек
		// int rem = result % 10;
		//
		// if(rem > 0){
		// int ost = result / 10;
		//
		// if (rem >= 5)
		// result = (ost + 1) * 10;
		// else
		// result = ost * 10;
		// }

		return result;
	}

	public static int findDiscount(OrgEx org, int fid) {
		if (org.discount == null)
			return 0;

		getFolders();

		Folder fld = folders.getFolder(fid);

		while (fld != null) {
			for (OrgDiscountItem od : org.discount)
				if (od.folderID == fld.id)
					return od.discount;
			fld = folders.getParent(fld);
		}

		return org.disc;
	}

	public static int findSumType(OrgEx org, int fid, int sumType) {
		if (org.discount == null)
			return 0;

		getFolders();

		Folder fld = folders.getFolder(fid);

		while (fld != null) {
			for (OrgCosTypeItem od : org.costypes)
				if (od.folderID == fld.id)
					return od.sumtype;

			fld = folders.getParent(fld);
		}

		return sumType;
	}

//	public static FolderTree getFolders() {
//		if (folders == null) {
//			folders = new FolderTree();
//			folders.load();
//		}
//
//		return folders;
//	}
}
