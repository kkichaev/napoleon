package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

import java.util.Date;

@TableInfo(name="BankIncass", keyFields="created")
public class BankIncass extends Incass {
	public Date visitDoc = new Date(1000);
}
