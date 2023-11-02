package com.grsoft.dataobjects;

import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.script.dataobjects.ScriptItem;

public class ScriptItemEx extends ScriptItem {
    @Override
    public boolean isCompleete() {
        return super.isCompleete() || state == ScriptImplEx.DOC_BLOCKED;
    }
}
