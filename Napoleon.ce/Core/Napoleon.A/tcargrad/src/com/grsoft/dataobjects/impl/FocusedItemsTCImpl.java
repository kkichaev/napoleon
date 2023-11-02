package com.grsoft.dataobjects.impl;

import java.util.HashSet;

import com.grsoft.dataobjects.FocusedItemTC;
import com.grsoft.dataobjects.FocusedItemsTC;

public class FocusedItemsTCImpl extends DbObject<FocusedItemsTC> {
	
	public static void loadItems(HashSet<String> items, String type, boolean recommended) {
		FocusedItemsTCImpl fii = new FocusedItemsTCImpl();
		FocusedItemsTC fi = fii.getData();
		fi.type = ( !recommended ) ? "None|" + type : type;
		if( fii.read() ) {
			for(FocusedItemTC i : fi.items)
				items.add(i.id);
		}
		fii.close();
	}
}
