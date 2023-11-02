package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.ISReturn;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.SalesEx;

public class DocDebtData {
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

	public DocDebtData(PaymentEx pay, int index) {
		number = pay.number;
		date = pay.date;
		payDate = pay.date;
		
		sum = pay.sum;
		sumD = sum;
		
		this.index = index;
		isDelivery = false;
	}
	
	public DocDebtData(SalesEx dlv, int index) {
		number = dlv.number;
		date = dlv.date;
		// берем отсрочку 5 дней
		int delay = dlv.delay;
		if( delay == 0 )
			delay = 5;
		payDate = new Date(dlv.date.getTime() + delay * 24 * 3600 * 1000) ;
		
		sum = dlv.sum();
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

	public DocDebtData(ReturnEx doc, int index) {
		number = doc.number;
		date = doc.date;
		payDate = doc.date;
		
		sum = -doc.sum();
		sumD = sum;
		
		this.index = index;
		isDelivery = false;
	}

	public DocDebtData(ISReturn doc, int index) {
		number = doc.number;
		date = doc.date;
		payDate = doc.date;
		
		sum = -doc.sum();
		sumD = sum;
		
		this.index = index;
		isDelivery = false;
	}

	final static long PAY_LIMIT_DAYS = 45l * 3600 * 24 * 1000;
	public boolean isOutOfPayLimit(int limit) {
		if( sumD <= 0 )
			return false;
		
		long now = Calendar.getInstance().getTime().getTime();
		return (now - payDate.getTime()) > limit * 24 * 3600000l;
//		
//		long docTime = date.getTime();
//		long payLimit = Math.abs(payDate.getTime() - docTime) * 2;
//		if( payLimit > PAY_LIMIT_DAYS )
//			payLimit = PAY_LIMIT_DAYS;
//		
//		return ((now - docTime) > payLimit);
	}
}
