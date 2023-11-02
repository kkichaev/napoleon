package com.grsoft.napoleon;

import com.grsoft.dataobjects.IncassDebDistr;

public class IncassDebDistrEditEx extends IncassDebDistrEdit{
    @Override
    protected boolean isDocCanProcessed(IncassDebDistr ie) {
        return !(ie.items == null || ie.items.size() == 0 || doc.sum() == 0);
    }
}
