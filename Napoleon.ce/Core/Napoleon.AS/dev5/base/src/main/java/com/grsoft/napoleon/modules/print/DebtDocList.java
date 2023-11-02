package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.impl.BalanceSales;
import com.grsoft.dataobjects.impl.DebtPkoImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.documents.DebetItem;
import com.grsoft.napoleon.documents.DocList;

public class DebtDocList extends com.grsoft.napoleon.documents.DebtDocList {
		
	protected DocList sales;
	protected DocList pkos;
		
	public DebtDocList(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery, "", "");
	}
	protected void init(String where, String order, boolean loadDelivery) {
		BalanceSales.clearPkoData();

//		ids = new ArrayList<Long>();
//
//		deliveries = (loadDelivery) ? new DocList(BalanceDelivery.class, where, order) : null;
//		payments = new DocList(PaymentImpl.class, where, order);
//
//		Date lastDlvDate = null;
//		Date lastPayDate = null;
//		HashSet<String> dlvNumbers = new HashSet<String>();
//		HashSet<String> payNumbers = new HashSet<String>();
//
//		if( loadDelivery )
//			lastDlvDate = loadDeliveries(dlvNumbers);
//
//		lastPayDate = loadPayments(payNumbers);
//
//		if( loadDelivery )
//			loadSales(where, lastDlvDate, dlvNumbers);
//
//		loadPKO(where, lastPayDate, payNumbers);
	}
	
	@Override
	public void close() {
		if( sales != null )
			sales.close();
		if( pkos != null )
			pkos.close();

		super.close();
	}

	protected void loadPKO(String where, Date lastPayDate, HashSet<String> usedNumbers) {
//		int i;
//		String wherestr= (where != null && where.length() > 0) ? where : "";
//		if( lastPayDate != null ) {
//			if( wherestr.length() >  0 ) wherestr += " and ";
//			wherestr += "date >= " + Long.toString(lastPayDate.getTime());
//		}
//		pkos = new DocList(DebtPkoImpl.class, wherestr, null);
//		for( i=0; i<pkos.getCount(); i++ ) {
//			DebtPkoImpl s = (DebtPkoImpl)pkos.get(i);
//			if( !usedNumbers.contains(s.getData().number) ) {
//				Pko pko = s.getData();
//				if( !BalanceSales.addPayment(pko) )  {
//					DebetItem item = new DebetItem();
//					item.docs = pkos;
//					item.index = i;
//					item.isDelivery = false;
//					item.date = s.getDate();
//					item.number = s.getData().number;
//					items.add(item);
//
//					ids.add((long) ids.size());
//				}
//			}
//		}
	}

	protected void loadSales(String where, Date lastDlvDate, HashSet<String> usedNumbers) {
//		int i;
//		String wherestr= (where != null && where.length() > 0) ? where : "";
//		if( lastDlvDate != null ) {
//			if( wherestr.length() >  0 ) wherestr += " and ";
//			wherestr += "date >= " + Long.toString(lastDlvDate.getTime());
//		}
//
//		sales = new DocList(BalanceSales.class, wherestr, null);
//		for( i=0; i<sales.getCount(); i++ ) {
//			BalanceSales s = (BalanceSales)sales.get(i);
//			String number = s.getData().number;
//			if( !usedNumbers.contains(number) ) {
//				DebetItem item = new DebetItem();
//				item.docs = sales;
//				item.index = i;
//				item.isDelivery = true;
//				item.date = s.getDate();
//				item.number = number;
//				items.add(item);
//
//				ids.add((long) ids.size());
//			}
//		}
	}
}