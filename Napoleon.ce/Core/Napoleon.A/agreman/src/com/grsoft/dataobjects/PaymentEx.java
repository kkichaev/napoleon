package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.napoleon.documents.DebtDocEx.DebtDbObject;
import com.grsoft.types.Scale;

public class PaymentEx extends Payment 
implements DebtDbObject{
	@Scale(value=100)
	public int exp;

	@Override
	public int getSum() {
		return (int)sum;
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
