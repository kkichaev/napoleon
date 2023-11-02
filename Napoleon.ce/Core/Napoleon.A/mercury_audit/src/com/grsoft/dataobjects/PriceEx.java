package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Price", keyFields = "id", indexes="own")
public class PriceEx extends Price {
	public int own = 0;
	public int pos = 0;
}
