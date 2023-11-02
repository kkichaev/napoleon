package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.CfgNplEx;

public class CostStrategyEx extends CostStrategy {
	private HashMap<String, HashMap<Integer, Integer>> discountCash = 
			new HashMap<String, HashMap<Integer,Integer>>();
	static FolderTree folders;
	
	public CostStrategyEx(){
		refreshCash();
	}
	
	public void refreshCash(){
		discountCash.clear();
		
		OrgEx org = new OrgEx();
		DbReader r = new DbReader();
		boolean bdo = r.select(org, DataObjectInfo.getInstance().getTableName(OrgEx.class), null);
		while(bdo){
			HashMap<Integer, Integer> item = null;
			if(discountCash.containsKey(org.id))
				item = discountCash.get(org.id);
			else{
				item = new HashMap<Integer, Integer>();
				discountCash.put(org.id, item);
			}
			
			for(OrgDiscountItem i : org.discount)
				if(!item.containsKey(i.folderID))
					item.put(i.folderID, i.discount);
			
			bdo = r.selectNext(org);
		}
		r.close();
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = 0;
		int sumType = doc != null ? doc.getSumType() : 0;
		int discount = 0;
		boolean haveDiscount = false;
		
		if(doc != null){
			if(discountCash.containsKey(doc.getId())){
				HashMap<Integer, Integer> item = discountCash.get(doc.getId());
				
				if(item.containsKey(p.folderID)){
					discount = item.get(p.folderID);
					haveDiscount = true;
				}
			}
		
			if(!haveDiscount){
				OrgImpl org = new OrgImpl();
				String docid = doc.getId();
				org.getData().id = doc.getId();
				
				if(org.read()){
					discount = findDiscount((OrgEx)org.getData(), p.folderID);
					
					HashMap<Integer, Integer> item = null;
					if(discountCash.containsKey(docid))
						item = discountCash.get(docid);
					else{
						item = new HashMap<Integer, Integer>();
						discountCash.put(docid, item);
					}
					
					item.put(p.folderID, discount);
				}
				
				org.close();
			}
		}
		
		result = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? 
					p.cost.get(sumType).cost : 0;		
		
		result -= (int)(((long)result * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / 
				(Consts.SUM_SCALE * Consts.SUM_SCALE));

		//Округление до десятков копеек, 
		//по последним данным токаревым
		//было сказано, округлять вверх до 10 копеек, если есть копейки. 2013.11.20
		//bug#1118 - добавить чекбокс, который бы и определял метод округления
		CfgNplEx config = (CfgNplEx)ConfigManager.getConfig();
		if (!config.roundPrice){
			int rem = result % 10;
		
			if(rem > 0){
				int ost = result / 10;
				result = (ost + 1) * 10;
			
//				if (rem >= 5)
//					result = (ost + 1) * 10;
//				else
//					result = ost * 10;
			}
		}
		
		return result;
	}
	
	public static int findDiscount(OrgEx org, int fid) {
		if(org.discount == null)
			return 0;

		getFolders();
		
		Folder fld = folders.getFolder(fid);
		
		while(fld != null) {
			for(OrgDiscountItem od : org.discount)
				if( od.folderID == fld.id )
					return od.discount;
			fld = folders.getParent(fld);
		}
		
		return 0;
	}
	
	public static FolderTree getFolders() {
		if(folders == null) {
			folders = new FolderTree();
			folders.load();
		}
		
		return folders;
	}
}
