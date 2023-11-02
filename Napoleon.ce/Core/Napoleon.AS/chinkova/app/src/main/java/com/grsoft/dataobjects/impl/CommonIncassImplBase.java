package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.util.Util;

import android.content.Context;

public abstract class CommonIncassImplBase<T extends CommonIncass> extends DbObject<T> {
	public abstract void open(Context context);
	
	public void init(){
		data.created = Util.getDateTime();
		write();
	}

	public boolean isExported() {
		return DocumentUtils.isExported(data.params);
	}
	
	public boolean isEditable() { return !isExported(); }

	public int sum() {
		int result = 0;
		
		for(CommonIncassItem i : data.items)
			result += i.sum;
		
		return result;
	}
	
	public CommonIncassItem findItem(String id){
		CommonIncassItem result = null;
		
		for (CommonIncassItem item : data.items)
			if (item.id.equals(id)) {
				result = item;
				break;
			}
		
		return result;
	}
	
	public boolean hasEmptyItems(){
		boolean result = false;
		
		for (CommonIncassItem item : data.items)
			if (item.sum == 0) {
				result = true;
				break;
			}
		
		return result;
	}
}
