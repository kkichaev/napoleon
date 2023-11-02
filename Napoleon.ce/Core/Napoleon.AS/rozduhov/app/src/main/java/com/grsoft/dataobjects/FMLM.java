package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="FMLM", keyFields="id")
@ServerInfo(name="FMLM")
public class FMLM extends DataObject {
	public String id = "";
	
	public List<ItemObject> items = new ArrayList<ItemObject>();

	public boolean haveItem(PriceEx pe) {
		for(ItemObject i : items)
			if(i.id.equals(pe.id))
				return true;
		return false;
	}
}
