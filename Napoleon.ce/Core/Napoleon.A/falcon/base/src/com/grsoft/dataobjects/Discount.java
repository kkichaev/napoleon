package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="discount", keyFields="id")
public class Discount extends DataObject {
	/**
	 * это id договора
	 */
	public String id;
	
	public List<DiscountItem> items;
	
	public DiscountItem find(String discID) {
		for(DiscountItem i : items) {
			if( i.id.equals(discID) )
				return i;
		}
		return null;
	}
}
