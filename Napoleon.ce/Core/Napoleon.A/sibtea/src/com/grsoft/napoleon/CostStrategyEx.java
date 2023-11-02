package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderDiscountItem;
import com.grsoft.dataobjects.OrgPriceDiscountItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgFolderDiscountImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgItemDiscountImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {

	OrgImpl oi = new OrgImpl();
	int discount = 0;
	
	HashMap<String, Integer> items = new HashMap<String, Integer>();
	HashMap<Integer, Integer> folders = new HashMap<Integer, Integer>();
		
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		if( doc != null ) {
			String docID = doc.getId();
			OrgEx oe = (OrgEx)oi.getData(); 
			if( oe.id.equals(docID) == false)  {
				String ido = oe.ido;
				oe.id = docID;
				oi.read();
				oi.close();
				if( oe.ido.equals(ido) == false ) 
					loadDiscount(oe.ido);
			}
			
			int dsc = getDiscount(p);
			if( dsc != 0 ) {
				int sign = (dsc < 0) ? -1 : 1;
				cost += (int)(((long)cost * dsc  + sign * Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
			}
		}
		return cost;
	}
	
	ArrayList<Folder> loadFolders() {
		ArrayList<Folder> fa = new ArrayList<Folder>();
		
		String table = DataObjectInfo.getInstance().getTableName(Folder.class);
		DbReader r = new DbReader();
		Folder fdata = new Folder();
		boolean bdo = r.select(fdata, table, "", "id");
		while( bdo ) {
			fa.add(fdata);
			fdata = new Folder();
			bdo = r.selectNext(fdata);
		}
		
		return fa;
	}

	private void putDescendantFolders(ArrayList<Folder> fa, OrgFolderDiscountItem fi) {
		int i = 0, level = -1;
		
		for( ; i < fa.size(); i++ ) {
			Folder fdata = fa.get(i);
			if( fdata.id == fi.folder ) {
				level = fdata.level;
				i++;
				break;
			}
		}
		
		for( ; i < fa.size(); i++ ) {
			Folder fdata = fa.get(i);
			if( fdata.level <= level )
				break;
			if( folders.containsKey(fdata.id) == false )
				folders.put(fdata.id, fi.nac);
		}
	}

	private void loadDiscount(String ido) {
		ArrayList<Folder> fa = loadFolders();
		
		discount = 0;
		items.clear();
		folders.clear();
		
		
		OrgFolderDiscountImpl ofi = new OrgFolderDiscountImpl();
		ofi.getData().id = ido;
		if( ofi.read() ) {
			for(OrgFolderDiscountItem fi : ofi.getData().items ) {
				folders.put(fi.folder, fi.nac);
				putDescendantFolders(fa, fi);
			}
		}
		ofi.close();
		
		OrgItemDiscountImpl opi = new OrgItemDiscountImpl();
		opi.getData().id = ido;
		if( opi.read() ) {
			for(OrgPriceDiscountItem pi : opi.getData().items ) {
				if( pi.id.equals("0"))
					discount =  pi.nac;
				else
					items.put(pi.id, pi.nac);
			}
		}
		opi.close();
	}

	private int getDiscount(Price p) {
		Integer d = items.get(p.id);
		if( d != null )
			return d;
		
		d = folders.get(p.folderID);
		if( d != null )
			return d;
		return discount;
	}
}
