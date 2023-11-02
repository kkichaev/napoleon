package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashSet;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DebetIncassImpl;
import com.grsoft.dataobjects.impl.ISReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.modules.print.DebtDocList;

public class DebetDocListEx extends DebtDocList {

	DocList isrets, rets;
	
	public DebetDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}
	
	@Override
	public void close() {
		isrets.close();
		rets.close();
		
		super.close();
	}
	
	@Override
	protected void loadPKO(String where, Date lastPayDate, HashSet<String> usedNumbers) {
		int i;
		String wherestr= (where != null && where.length() > 0) ? where : "";
		
		if (wherestr.length() > 0)
			wherestr += " and ";
		
		wherestr +=  "(([params] & " + ParamState.ofExported + " ) == " +  ParamState.ofExported + ")";
			
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
					item.number = pko.number;
					items.add(item);
	
					ids.add((long) ids.size());
//				}
			}
		}
		
		loadRets(where);
	}

	protected void loadRets(String where) {
		Date lastDate = null;
		HashSet<String> usedRets = new HashSet<String>();
		
		int i;
		isrets = new DocList(ISReturnImpl.class, where, null);
		for( i=0; i<isrets.getCount(); i++ ) {
			ISReturnImpl ir = (ISReturnImpl)isrets.get(i);
			
			usedRets.add(ir.getData().number);
			
			DebetItem item = new DebetItem();
			item.docs = isrets;
			item.index = i;
			item.isDelivery = false;
			item.date = ir.getDate();
			item.number = ir.getData().number;
			items.add(item);

			if( lastDate == null || lastDate.compareTo(item.date) < 0)
				lastDate = item.date;
				
			ids.add((long) ids.size());
		}
		
		String wherestr= (where != null && where.length() > 0) ? where : "";
		if( lastDate != null ) {
			if( wherestr.length() >  0 ) wherestr += " and ";
			wherestr += "date >= " + Long.toString(lastDate.getTime());
		}
		
		rets = new DocList(ReturnImplEx.class, wherestr, null);
		for( i=0; i<rets.getCount(); i++ ) {
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
}
