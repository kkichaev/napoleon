package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Payment", keyFields = "id,idDog,number")
public class PaymentEx extends Payment {
	public String idDog = "";
}
