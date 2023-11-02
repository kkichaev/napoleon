package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BankImpl;

public class BankDoc extends DocType{
    static public final String OBJ_NAME = "Bank";
    private static BankDoc instance = null;

    static public DocType instance() {
        if( instance == null )
            instance = new BankDoc();

        return instance;
    }

    protected BankDoc() {
        super(OBJ_NAME, OBJ_NAME, BankImpl.class);
    }
}
