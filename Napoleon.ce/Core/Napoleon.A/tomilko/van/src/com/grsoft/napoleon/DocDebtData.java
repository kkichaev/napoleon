package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;

public class DocDebtData implements Comparable<DocDebtData> {
	public String number;
	
	public Date date;
	public Date payDate;
	
	public long sum;
	public long sumD;
	
	public int index;
	
	public boolean isDelivery;
	
	public DocDebtData(DeliveryEx dlv, int index) {
		number = dlv.number;
		date = dlv.date;
		payDate = dlv.payDate;
		
		sum = dlv.sum();
		sumD = sum;
		
		this.index = index;
		isDelivery = true;
	}

	public DocDebtData(Payment pay, int index) {
		number = pay.number;
		date = pay.date;
		payDate = pay.date;
		
		sum = pay.sum;
		sumD = sum;
		
		this.index = index;
		isDelivery = false;
	}
	
	public DocDebtData(Sales dlv, int index) {
		number = dlv.number;
		date = dlv.date;
		// берем отсрочку 5 дней
		int delay = dlv.delay;
		if( delay == 0 )
			delay = 5;
		payDate = new Date(dlv.date.getTime() + delay * 24 * 3600 * 1000) ;
		
		sum = 0;
		for(OrderItem i : dlv.items)
			sum += ((SalesItem)i).sum;
		
		sumD = sum;
		
		this.index = index;
		isDelivery = true;
	}

	public DocDebtData(IncassEx pay, int index) {
		number = pay.number;
		date = pay.date;
		payDate = pay.date;
		
		sum = -pay.sum;
		sumD = sum;
		
		this.index = index;
		isDelivery = false;
	}

	public boolean isOutOfPayLimit() {
		return sumD > 0 && (Calendar.getInstance().getTime().getTime() > (payDate.getTime() + 24*3600*1000));
	}

	@Override
	public int compareTo(DocDebtData arg0) {
		return date.compareTo(arg0.date);
	}
}
