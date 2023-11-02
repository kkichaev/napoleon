package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TargetImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class TargetDoc extends DocType{
    private static final String OBJ_NAME = "Target";

    static protected TargetDoc instance = null;

    static public TargetDoc instance() {
        if (instance == null) {
            instance = new TargetDoc(OBJ_NAME, TargetImpl.class);
        }
        return instance;
    }

    protected TargetDoc(String name, Class<? extends Document<?>> docClass) {
        super(name, name, docClass);
    }

    @Override
    public boolean outOfScript() {
        return true;
    }

    @Override
    public int getDocTitle() {
        return R.string.target_doc_title;
    }

    @Override
    public int getResurceId() {
        return R.drawable.target_doc;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.target_doc_2;
    }
}
