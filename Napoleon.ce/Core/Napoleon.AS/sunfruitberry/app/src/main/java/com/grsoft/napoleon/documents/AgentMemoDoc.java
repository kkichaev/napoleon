package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.napoleon.R;

public class AgentMemoDoc extends DateDocType {
	static AgentMemoDoc instance = null;
	
	public static AgentMemoDoc instance() {
		if(instance == null)
			instance = new AgentMemoDoc();
		return instance;
	}
	
	AgentMemoDoc() { super("Служебная записка", "AgentMemo", AgentMemoImpl.class); }

	@Override public int getDocTitle() { return R.string.agent_memo_doc; }
	@Override public int getResurceId() {	return R.drawable.memo_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.memo_doc_2;
	}
}
