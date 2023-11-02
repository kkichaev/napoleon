package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.GatherItem;
import com.grsoft.napoleon.GatherEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class GatherImpl extends CreatableDocument<Gather> {

	public void open(Context context) {
		GatherEdit.open(context, getRowid());
	}
	
	public boolean isInWork() { return ((data.params & Gather.IN_WORK) == Gather.IN_WORK); }
	
	public boolean isComplete(){
		return ((data.params & Gather.COMPLEETE) == Gather.COMPLEETE);
	}
	
	public boolean checkCompleete() {
		boolean done = true;
		for(GatherItem gi : data.items){
			if( gi.used < 2 ) {
				done = false;
				break;
			}
		}
		
		if( done )
			data.params |= Gather.COMPLEETE;
		else
			data.params &= (~Gather.COMPLEETE);
		return done;
	}
}
