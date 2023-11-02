package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MLM")
@ServerInfo(name="MLM")
public class MLM extends DataObject {
	public String format = "";
	
	public List<ItemObject> items = new ArrayList<ItemObject>();
	public List<ItemObject> marks = new ArrayList<ItemObject>();
	public List<ItemObject> groups = new ArrayList<ItemObject>();
	
	public boolean isSelected(Folder f, PriceEx pe) {
		for(ItemObject i : groups) {
			if(i.id.equals(f.fid))
				return true;
		}

		for(ItemObject i : marks) {
			if(i.id.equals(pe.tradeMark))
				return true;
		}

		for(ItemObject i : items) {
			if(i.id.equals(pe.id))
				return true;
		}
		
		return false;
	}
}
