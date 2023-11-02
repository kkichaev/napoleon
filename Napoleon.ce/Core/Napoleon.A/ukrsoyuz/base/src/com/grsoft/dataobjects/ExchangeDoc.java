package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="ExchDoc", keyFields="created")
public class ExchangeDoc extends CreateDocDataObject {
	public List<ExchangeItem> items;
}
