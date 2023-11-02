package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="isreturns", keyFields="ido,number")
public class ISReturn extends Delivery {
	public String ido;
}
