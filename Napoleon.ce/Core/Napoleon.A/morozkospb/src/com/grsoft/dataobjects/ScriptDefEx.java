package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.script.dataobjects.ScriptDef;

@TableInfo(name="ScriptDef",keyFields="id", indexes="suppl")
public class ScriptDefEx extends ScriptDef {
	public String suppl = "";
}
