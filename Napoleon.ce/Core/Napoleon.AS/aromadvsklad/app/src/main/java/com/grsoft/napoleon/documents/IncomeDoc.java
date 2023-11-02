package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.IncomeImpl;

public class IncomeDoc extends DocType {
    static IncomeDoc instance;

    public static IncomeDoc instance() {
        if(instance == null)
            instance = new IncomeDoc();
        return instance;
    }

    IncomeDoc() {
        super("Income", "Income", IncomeImpl.class);
    }
}
