package com.grsoft.napoleon.documents;
import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Balance;
import com.grsoft.dataobjects.BalanceDoc;
import com.grsoft.dataobjects.BalanceItem;
import com.grsoft.dataobjects.impl.BalanceDocImpl;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.BalanceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * в ids если id < 0 значит payment иначе delivery
 * @author 1111
 *
 */
public class DebtDocList extends DocList {

	protected List<BalanceDoc> items = new ArrayList<>();

	public DebtDocList(String where, String order, boolean loadDelivery, String openingBalance, String overpayment) {
		init(where, order, loadDelivery, openingBalance, overpayment);
	}

	protected DebtDocList() {}

	protected void init(String where, String order, boolean loadDelivery, String openingBalance, String overpayment) {
		for(Balance b : DbReader.fetch(Balance.class, where)) {
			long sum = b.sum;
			Date date = new Date();
			if(sum < 0) {
				BalanceItem bi = new BalanceItem();
				bi.sum = sum;
				bi.date = new Date();
				bi.number = overpayment;
				BalanceDoc bd = new BalanceDoc(b, bi);
				items.add(bd);
			}
			for(BalanceItem bi : b.documents) {
				BalanceDoc bd = new BalanceDoc(b, bi);
				items.add(bd);
				sum -= bi.sum;
				if(date.compareTo(bi.date) > 0)
					date = bi.date;
			}
			if(sum > 0) {
				BalanceItem bi = new BalanceItem();
				bi.sum = sum;
				bi.date = new Date(date.getTime() - 24 * 3600 * 1000);
				bi.number = openingBalance;
				BalanceDoc bd = new BalanceDoc(b, bi);
				items.add(bd);
			}
		}
		Collections.sort(items);
	}


	@Override
	public void close() {
		super.close();
	}

	@Override public int getCount() { return items.size(); }

	@Override public long getId(int index) { return index; }

	@Override
	public Document<?> get(int index) {
		if( index < 0 || index >= items.size() )
			return null;
		return new BalanceDocImpl(items.get(index));
	}

}