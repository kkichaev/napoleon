package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BankIncassImpl;
import com.grsoft.napoleon.R;

public class BankDoc extends DocType {
    static BankDoc instance = null;
    public static DocType instance() {
        if(instance == null)
            instance = new BankDoc();
        return instance;
    }

    private BankDoc() {
        super("Банк", "BankDoc", BankIncassImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.bank_inscass;
    }
}
