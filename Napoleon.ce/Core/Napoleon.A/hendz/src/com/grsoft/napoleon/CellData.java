package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CellsAuditItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.VandSell;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.dataobjects.impl.CellsAuditImpl;


public class CellData {
	public String id;
	
	public int cell;
	public int cost;
	public int limit;
	public int rest;

	static public List<CellData> getVandData(String id, Date docDate) {
		List<CellData> ret = new ArrayList<CellData>();
		CellsAuditImpl lastDoc = CellsAuditImpl.getLastDoc(id);
		if( lastDoc != null ) {
			for(CellsAuditItem i : lastDoc.getData().items) {
				CellData cd = new CellData();
				cd.id = i.id;
				cd.cell = i.cell;
				cd.cost = i.cost;
				cd.limit = i.limit;
				cd.rest = i.qty;
				
				ret.add(cd);
			}
			
			VandSell vs = new VandSell();
			String table = DataObjectInfo.getInstance().getTableName(vs.getClass());
			Date lastDocDate =  lastDoc.getData().created;
			String where = "created > " + Long.toString(lastDocDate.getTime()) + " and id ='" + id + "'";
			if (docDate!=null)
				where += " and created < " + Long.toString(docDate.getTime());
			
			DbReader r = new DbReader();
			boolean bdo = r.select(vs, table, where, "created");
			while(bdo) {
				for(VandSellItem vi : vs.items) {
					for(CellData cd : ret) {
						if( cd.cell == vi.cell ) {
							cd.rest += (vi.load - vi.chek - vi.unload);
//							if( cd.rest < 0 )
//								cd.rest = 0;
							break;
						}
					}
				}
				bdo = r.selectNext(vs);
			}
			r.close();
		}
		return ret;
	}
}
