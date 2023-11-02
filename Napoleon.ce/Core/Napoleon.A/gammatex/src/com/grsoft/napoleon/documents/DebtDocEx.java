package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashSet;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Util;


public class DebtDocEx extends com.grsoft.napoleon.modules.print.DebtDoc  {
	
	@Override
	protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery) {
		return new com.grsoft.napoleon.modules.print.DebtDocList(where, order, LoadDelivery){
			DocList rets;
			
			@Override
			protected void loadPKO(String where, Date lastPayDate, HashSet<String> usedNumbers) {
				//super.loadPKO(where, lastPayDate, usedNumbers);
				loadRets(where);
			}
			
			@Override
			protected void loadSales(String where, Date lastDlvDate, HashSet<String> usedNumbers) {
				//super.loadSales(where, lastDlvDate, usedNumbers);
			}
			
			protected void loadRets(String where) {
				HashSet<String> usedRets = new HashSet<String>();
				
				String wherestr= (where != null && where.length() > 0) ? where : "";
				if( wherestr.length() >  0 ) wherestr += " and ";
				wherestr += "date >= " + Util.getDate().getTime();
				
				rets = new DocList(ReturnImplEx.class, wherestr, null);
				for(int i=0; i<rets.getCount(); i++ ) {
					ReturnImplEx ret = (ReturnImplEx)rets.get(i);
					if( usedRets.contains(ret.getData().number) )
						continue;
					
					DebetItem item = new DebetItem();
					item.docs = rets;
					item.index = i;
					item.isDelivery = false;
					item.date = ret.getDate();
					item.number = ret.getData().number;
					items.add(item);
					ids.add((long) ids.size());
				}
			}	

		};
	}
}
