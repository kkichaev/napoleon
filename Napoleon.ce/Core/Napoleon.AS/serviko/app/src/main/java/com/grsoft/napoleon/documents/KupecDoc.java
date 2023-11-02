package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.KupecImpl;

public class KupecDoc extends DocType{
    public static KupecDoc instance = null;

    static public DocType instance() {
        if( instance == null )
            instance = new KupecDoc();
        return instance;
    }

    protected KupecDoc() {
        super("Kupec", "Kupec", KupecImpl.class);
    }
}
