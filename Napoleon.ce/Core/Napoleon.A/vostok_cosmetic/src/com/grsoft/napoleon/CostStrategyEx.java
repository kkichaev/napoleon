package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.NetUser;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FolderDiscountImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	static int maxDiscount;
	static int discount;
	
	static OrgImpl org = null;
	static int folderID = -1;
	
	static FolderTree folders;
	static boolean isNetUser = false, netUserUndef = true;
	
	public static void clearCache() {
		folders = null;
		org = null;
		folderID = -1;
		netUserUndef = true;
	}
	
	public static boolean isNetUser() {
		if( netUserUndef ) {
			NetUser user = new NetUser();
			String table = DataObjectInfo.getInstance().getTableName(user.getClass());
			DbReader r = new DbReader();
			isNetUser = r.select(user, table, "id=userid");
			r.close();
			netUserUndef = false;
		}
		return isNetUser;
	}
	
	public static DiscountData getDiscountData(String id, int folderID) {
	
		if( org == null || !org.getData().id.equals(id) || CostStrategyEx.folderID != folderID ) {
			if( org == null )
				org = new OrgImpl();
			
			OrgEx o = (OrgEx) org.getData(); 
			if( o.id.equals(id) == false ) {
				o.id = id;
				org.read();
				org.close();
			}
			
			FolderImpl fi = new FolderImpl();
			Folder fe = (Folder)fi.getData();
			fe.id = folderID;
			fi.read();
			
			FolderDiscountImpl fdsc = new FolderDiscountImpl();
			fdsc.getData().fid = fe.fid;
			fdsc.read();
			fdsc.close();
			maxDiscount = fdsc.getData().discount;
			CostStrategyEx.folderID = folderID;
		
			Integer fd = findDiscount(o, fe.fid);
			if( fd == null )
				discount = -o.discount;
			else {
				discount = -fd;
				if( isNetUser() )
					maxDiscount = fd;
			}
		}
		
		DiscountData dd = new DiscountData();
		dd.discount = discount;
		dd.maxDiscount = maxDiscount;
		return dd;
	}
	
//	обычные - по умолчанию DSC, если нет записи в DSC по группе - из O. ћаксимальна€ из FDSC
//	сетевые - по умолчанию DSC, она же максимальна€. ≈сли в DSC нет записи, тогда из O и максимальна€ из FDSC.
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		if( doc != null && doc instanceof OrderImpl ) {
			DiscountData dd = getDiscountData(doc.getId(), p.folderID);
			
			int ldiscount = dd.discount;

			OrderItemEx oei = (OrderItemEx) ((OrderImpl)doc).findItem(p.id);
			if( oei != null)
				ldiscount = oei.discount;
			
			if( -ldiscount > maxDiscount )
				ldiscount = -maxDiscount;
			
			if( ldiscount != 0 )
				cost += (int)(((long)cost * ldiscount - Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		}
		return cost;
	}
	
	public static FolderTree getFolders() {
		if(folders == null) {
			folders = new FolderTree();
			folders.load();
		}
		
		return folders;
	}
	
	public static Integer findDiscount(OrgEx org, String fid) {
		if(org.fldDsc == null)
			return null;

		getFolders();
		
		Folder fld = folders.getFolder(fid);
		
		while(fld != null) {
			for(OrgDiscount od : org.fldDsc)
				if( fld.fid.equals(od.fid) )
					return od.discount;
			fld = folders.getParent(fld);
		}
		
		return null;
	}
	
//	public static int getDiscount(OrgEx org, int folderID) {
//		int discount = org.discount;
//		
//		if(org.fldDsc == null)
//			return discount;
//
//		Integer fd = findDiscount(org, folderID);
//		return (fd==null) ? discount : fd;
//	}

}
