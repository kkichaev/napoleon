package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;

public class DocumentsEx extends Documents {
    @Override protected String getNonBlockingMessage() { return ((OrgEx)org.getData()).stopMsg; }
}
