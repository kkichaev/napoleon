package com.grsoft.dataobjects.impl;

import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


public class ScriptImplEx extends ScriptImpl {
	public static final int DOC_BLOCKED = 4;
	
	public void setBlocked(int position) {
		if( position >= 0 && position < data.items.size()  ){
			ScriptItem item = data.items.get(position);
			item.state = DOC_BLOCKED;
			write();
		}
	}
	
	public boolean IsEnabled(int position, ScriptDef defDoc) {
		boolean result = position == 0;
		
		if( position > 0 && position < data.items.size() ){
			ScriptItem item = data.items.get(position);
			result = item.state == ScriptItem.DOC_INITED;
		
			if(!result && item.state != DOC_BLOCKED){
				ScriptItem prevItem = data.items.get(position-1);
				result = prevItem.isCompleete() || prevItem.state == DOC_BLOCKED;
				if( !result ) {
					boolean canSkip = true;
					for(int i=position-1; i>=0; i-- ) {
						ScriptDefItem prevDef = defDoc.items.get(i);
						if( !prevDef.canSkip() ) {
							prevItem = data.items.get(i);
							canSkip = prevItem.isCompleete();
							break;
						}
					}
					result = canSkip;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public boolean isComplete() {
		boolean result = true;
		
		for(ScriptItem item : data.items){
			result = item.state == ScriptItem.DOC_INITED || item.state == ScriptItem.DOC_SKIPPED || item.state == DOC_BLOCKED;
			
			if(!result)
				break;
		}
		
		return result;
	}

}

