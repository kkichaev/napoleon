package com.grsoft.script.dataobjects;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;

@TableInfo(name="ScriptDef",keyFields="id")
public class ScriptDef extends DataObject {
	@Scale(value=1)
	public int id;
	
	/**
	 * Имя сценария, если нет - берем название первого документа
	 */
	public String name="";
	
	public List<ScriptDefItem> items = new ArrayList<ScriptDefItem>();
	
	public boolean canSkip(int itemPos) { 
		for(ScriptDefItem i : items) {
				if(i.pos == itemPos && i.canSkip())
				return true;
		}
		return false;
	}
}
