package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.documents.OrderDoc;
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
		if( position >= 0 && position < data.items.size() ){
			ScriptItem item = data.items.get(position);
			if (item.state == DOC_BLOCKED)
				return false;
		}
		
		return super.IsEnabled(position, defDoc);
	}
}

