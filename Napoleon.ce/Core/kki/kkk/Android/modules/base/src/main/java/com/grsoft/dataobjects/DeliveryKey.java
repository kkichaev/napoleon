package com.grsoft.dataobjects;

import java.util.Date;

public class DeliveryKey {
	public Date date;
	public String number;
	public Delivery delivery;
	
	public DeliveryKey(Delivery d) {
		date = d.date;
		number = d.number;
		delivery = d;
	}
	
	public DeliveryKey(Date date, String number) {
		this.date = date;
		this.number = number;
	}
	
	protected DeliveryKey() { }
	
	@Override
	public int hashCode() {
		return (date.toString() + number).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DeliveryKey) {
			DeliveryKey ref = (DeliveryKey)o;
			return date.equals(ref.date) && number.equals(ref.number);
		}
		return false;
	}
}
