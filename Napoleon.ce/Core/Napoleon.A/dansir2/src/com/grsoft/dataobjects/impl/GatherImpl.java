package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.GatherEdit;

public class GatherImpl extends DbObject<Gather> {
	public void setExported(boolean value){
		if (value)
			data.params |= ParamState.ofExported;
		else
			data.params &= ~ParamState.ofExported;
		
		write();
	}	
	
	public boolean isExported(){
		return (data.params & ParamState.ofExported) == ParamState.ofExported; 
	}

	public void open(Context context) {
		GatherEdit.open(context, getRowid());
		
	}
	
	public boolean isInWork() { return ((data.params & Gather.IN_WORK) == Gather.IN_WORK); }
	
	public boolean isComplete(){
		return ((data.params & (Gather.IN_WORK|Gather.COMPLEETE)) == Gather.COMPLEETE);
//		boolean result = false;
//		
//		if(data.items != null && data.items.size() > 0){
//			result = true;
//			for(GatherItem i: data.items){
//				if(i.newQty <= 0){
//					result = false;
//					break;
//				}
//			}
//		}
//		
//		return result;
	}
}
