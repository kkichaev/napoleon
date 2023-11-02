package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="VandSell", keyFields="created")
public class VandSell extends CreateDocDataObject {
	public List<VandSellItem> items;
	public int costype;
	public String agentSklad = "";
}
