package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.CellsAuditItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.VandSell;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.dataobjects.impl.CellsAuditImpl;


public class CellData {
	public String id;
	
	public int cell;
	public int cost;
	public int limit;
	public int rest;

	static public List<CellData> getVandData(String id) {
		return getVandData(id, null);
	}
	
	static public List<CellData> getVandData(String id, Date docDate) {
		final List<CellData> ret = new ArrayList<CellData>();
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
			
			
			StringBuilder where=new StringBuilder();
			
			where.append("created > ").append(lastDoc.getData().created.getTime()).append(" and id ='").append(id).append("'");
			
			if (docDate!=null)
				where.append(" and created < ").append(docDate.getTime());
		
			DataTraveler.travel(VandSell.class, new DataTraveler.Travel<VandSell>() {

				@Override
				public boolean travel(DataTraveler<VandSell> item) {
					VandSell vs = item.data;
					for(VandSellItem vi : vs.items) 
						for(CellData cd : ret) 
							if( cd.cell == vi.cell ) {
								cd.rest += (vi.load - vi.chek - vi.unload);
//								if( cd.rest < 0 )
//									cd.rest = 0;
								break;
							}
					return true;
				}

			}, where.toString());
		}
		return ret;
	}
}
