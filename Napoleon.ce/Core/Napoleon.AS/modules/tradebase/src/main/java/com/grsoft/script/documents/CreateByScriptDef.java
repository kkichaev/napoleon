package com.grsoft.script.documents;

import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;

public interface CreateByScriptDef {
	public Document<?> create(ScriptDef def,  ScriptDefItem item);
}
