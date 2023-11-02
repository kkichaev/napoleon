package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;

@TableInfo(name="agentPlans", keyFields="dateStart")
public class AgentSalesPlan extends DataObject {
	public String name;
	
	public Date dateStart;
	public Date dateEnd;
	
	public List<AgentPlanItem> items;
	
	/**
	 * Планы задаются в упаковках - здесь, значение умножаем на QtyInPack
	 * @param id
	 * @return
	 */
	public int getItemQty(String id) {
		int qty = 0;
		boolean plan = false;
		if( items != null ) {
			for(AgentPlanItem i : items)
				if( i.id.equals(id) ){
					qty += i.qty;
					
					if(!plan)
						plan = true;
				}
		}
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		p.id = id;
		pi.read();
		pi.close();
		
		return plan ? (int)((long)qty * p.qtyInPack / Consts.QTY_SCALE) : -1;
	}
}
