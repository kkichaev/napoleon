package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.manager.R;

public class AgentMemoDoc extends MDocType {
    static protected AgentMemoDoc instance = null;
    public static final String OBJ_NAME = "AgentMemo";

    AgentMemoDoc() {
        super(OBJ_NAME, AgentMemoImpl.class);
    }

    static public MDocType instance() {
        if(instance == null)
            instance = new AgentMemoDoc();
        return instance;
    }

    @Override
    public int getDocTitle() {
        return R.string.agent_memo_doc;
    }
}
