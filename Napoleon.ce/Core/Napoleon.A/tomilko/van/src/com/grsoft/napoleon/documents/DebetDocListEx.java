package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashSet;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.DebetIncassImpl;
import com.grsoft.napoleon.modules.print.DebetItem;
import com.grsoft.napoleon.modules.print.DebtDocList;

public class DebetDocListEx extends DebtDocList {

	public DebetDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}

	@Override
	protected void loadPKO(String where, Date lastPayDate, HashSet<String> usedNumbers) {
		int i;
		String wherestr= (where != null && where.length() > 0) ? where : "";
		if( lastPayDate != null ) {
			if( wherestr.length() >  0 ) wherestr += " and ";
			wherestr += "date >= " + Long.toString(lastPayDate.getTime());
		}
		pkos = new DocList(DebetIncassImpl.class, wherestr, null);
		for( i=0; i<pkos.getCount(); i++ ) {
			DebetIncassImpl s = (DebetIncassImpl)pkos.get(i);
			IncassEx pko = (IncassEx)s.getData();
			if( !usedNumbers.contains(pko.number) ) {
//				if( !BalanceSales.addPayment(pko) )  {
					DebetItem item = new DebetItem();
					item.docs = pkos;
					item.index = i;
					item.isDelivery = false;
					item.date = s.getDate();
					items.add(item);
	
					ids.add((long) ids.size());
//				}
			}
		}
	}
}
