package com.grsoft.napoleon;

import java.util.HashMap;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgTypeCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	static String id = "";
	int sumType;
	static FolderTree ftree = new FolderTree();
	HashMap<String, Integer> folders = new HashMap<String, Integer>();
	static HashMap<String, CostData> cost = new HashMap<String, CostData>();
	
	public static void clearCache() { cost.clear(); id = ""; ftree.clear(); } 
	
	public CostData getCost(Price p, Document<?> doc) {
		if( ftree.size() == 0 )
			ftree.load();
		
		if( doc == null )
			return null;

		int sumType = doc.getSumType();
		String docid = doc.getId();
		if( id.equals(docid) == false ) {
			id = docid;
			folders.clear();
			
			OrgTypeCost otc = new OrgTypeCost();
			String table = DataObjectInfo.getInstance().getTableName(OrgTypeCost.class);
			DbReader r = new DbReader();
			boolean bdo = r.select(otc, table, "ido='" + id + "'");
			while( bdo ) {
				folders.put(otc.folder, otc.type);
				bdo = r.selectNext(otc);
			}
		}
		
		Folder fe = ftree.getFolder(p.folderID);
		if( fe == null )
			return null;
		do {
			Integer type = folders.get(fe.fid);
			if( type != null ) {
				sumType = type;
				break;
			}
			fe = ftree.getParent(fe);
		} while(fe != null);
		
		if( this.sumType != sumType || cost.size() == 0 ) {
			cost.clear();
			PriceCost pc = new PriceCost();
			String table = DataObjectInfo.getInstance().getTableName(pc.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(pc, table, "type=" + Integer.toString(sumType));
			while(bdo) {
				cost.put(pc.id, new CostData(pc));
				bdo = r.selectNext(pc);
			}
			r.close();
			this.sumType = sumType;
		}
		return cost.get(p.id);
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		CostData cd = getCost(p, doc);
		return (cd != null) ?  cd.cost : super.getItemCost(p, doc);
	}
}
