/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Payment(Оплата)
 *
 * kki   19/11/2010   creating
 */

package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="Payment", keyFields = "id,number")
public class Payment extends DocDataObject
{
	@Scale(value=100)
	public long sum;
	
	public String number = "";
	
	public String userid = "";
}
