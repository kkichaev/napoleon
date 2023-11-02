package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.impl.ScanLocationImpl;
import com.grsoft.aceteam.R;

public class ScanLocationDoc extends DateDocType {
    public static String OBJ = "ScanLocationDoc";
    private static DocType instance;

    static public DocType instance() {
        if( instance == null )
            instance = new ScanLocationDoc();
        return instance;
    }

    protected ScanLocationDoc() {
        super(OBJ, OBJ, ScanLocationImpl.class);
    }

    @Override public int getDocTitle() { return R.string.orgcoord_title; }
    @Override public int getResurceId() {	return R.drawable.scanlocation_doc; }

    @Override
    public int getResurce2Id() {
        return R.drawable.scanlocation_doc_2;
    }
}
