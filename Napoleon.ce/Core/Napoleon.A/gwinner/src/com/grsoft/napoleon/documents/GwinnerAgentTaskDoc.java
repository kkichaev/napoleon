package com.grsoft.napoleon.documents;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.impl.GwinnerAgentTaskImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateDB;
import com.grsoft.napoleon.UpdateDBW;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class GwinnerAgentTaskDoc extends DateDocType {
	static private GwinnerAgentTaskDoc instance = null;
	
	public static GwinnerAgentTaskDoc instance() {
		if(instance == null) {
			instance = new GwinnerAgentTaskDoc();
			
			ScriptDefImpl.docInScript.add(instance);
			ScriptDefImpl.docInScript.remove(TaskDoneDoc.instance());
			
			UpdateDB.addHitchingCtor( new HitchingCtor() {
				@Override public Hitching create() { return new DocumentRestore(instance, instance.objName); }
			}, UpdateDBW.RESTORE_DATA_HITCHING);
		}
		return instance;
	}
	
	GwinnerAgentTaskDoc() {
		super("Задачи", "GwinnerAgentTask", GwinnerAgentTaskImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.taskdoc; }
	
	@Override
	public int getDocTitle() { return R.string.task_doc_title; }
}
