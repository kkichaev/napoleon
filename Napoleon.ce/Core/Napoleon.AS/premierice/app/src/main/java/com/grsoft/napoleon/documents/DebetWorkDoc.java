package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DebetWorkImpl;
import com.grsoft.napoleon.R;

public class DebetWorkDoc extends DateDocType {
    static DebetWorkDoc instance = null;

    public static DebetWorkDoc instance() {
        if(instance == null)
            instance = new DebetWorkDoc();
        return instance;
    }

    DebetWorkDoc() {
        super("Работа с ПДЗ", "DebetWork", DebetWorkImpl.class);
    }
    @Override public int getResurceId() { return R.drawable.debet_wrk; }
    @Override public int getResurce2Id() { return R.drawable.debet_wrk_2; }
}
