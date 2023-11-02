package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.NewClientImpl;

public class NewClientDoc extends DocType{
    static DocType instance = null;
    protected NewClientDoc() {
        super("NewClient", "NewClient", NewClientImpl.class);
    }

    static public DocType instance() {
        if( instance == null ) {
            instance = new NewClientDoc();
        }

        return instance;
    }
}
