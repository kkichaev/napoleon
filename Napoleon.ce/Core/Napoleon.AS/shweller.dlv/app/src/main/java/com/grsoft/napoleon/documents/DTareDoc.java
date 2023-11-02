package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DTareImpl;

public class DTareDoc extends DocType {
    static final String OBJECT_NAME = "DRetTareDoc";
    static DTareDoc instance;

    public static DTareDoc instance() {
        if(instance == null) {
            instance = new DTareDoc();
        }
        return instance;
    }

    DTareDoc() {
        super(OBJECT_NAME, OBJECT_NAME, DTareImpl.class);
    }
}
