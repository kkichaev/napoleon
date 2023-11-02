package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.HashSet;
import java.util.Hashtable;

import com.grsoft.dataobjects.Pko;

public class BalanceSales extends SalesImpl {
	
	private static Hashtable<String, PkoSumData> pkoData = new Hashtable<String, PkoSumData>();
	
	public static void clearPkoData() { pkoData.clear(); }
	
	public static boolean addPayment(Pko pko) {
		if( pko.salesnumber.length() > 0 ) {
			PkoSumData pd = pkoData.get(pko.salesnumber);
			if( pd == null ) {
				pd = new PkoSumData();
				pkoData.put(pko.salesnumber, pd);
			}
			pd.add(pko);
			return true;
		}
		
		return false;
	}

	@Override
	public long sum() {
		long pkoSum = 0;
		PkoSumData pd = pkoData.get(data.number);
		if( pd != null )
			pkoSum = pd.getSum();
		return super.sum() - pkoSum;
	}
}

class PkoSumData {
	long pkoSum = 0;
	HashSet<String> usedPkos = new HashSet<String>();
	
	public void add(Pko pko) {
		if( usedPkos.contains(pko.number) )
			return;
		
		pkoSum += pko.sum;
		usedPkos.add(pko.number);
	}
	
	public long getSum() { return pkoSum; }
}