package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.napoleon.documents.DebtDocEx.DebtDbObject;
import com.grsoft.types.Scale;

public class DeliveryEx extends Delivery 
implements DebtDbObject{

	@Scale(value=100)
	public int exp;

	@Override
	public int getSum() {
		return (int)sumD;
	}

	@Override
	public int getExp() {
		return exp;
	}

	@Override
	public String getDescr() {
		return number;
	}

	@Override
	public Date getDate() {
		return date;
	}
}
