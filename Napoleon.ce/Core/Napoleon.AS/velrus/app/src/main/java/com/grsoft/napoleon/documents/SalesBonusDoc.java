package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SalesBonusImpl;
import com.grsoft.napoleon.R;

public class SalesBonusDoc extends OrderDoc {
    static public final String DOC_NAME = "Бонуч";
    static public final String OBJ_NAME = "SalesBonus";
    static SalesBonusDoc instance;

    SalesBonusDoc() { super(DOC_NAME, OBJ_NAME, SalesBonusImpl.class); }

    static public SalesBonusDoc instance() {
        if(instance == null) {
            instance = new SalesBonusDoc();
        }

        return (SalesBonusDoc) instance;
    }

    @Override
    public int getDocTitle() {
        return R.string.sales_bouns;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.bonus_doc_2;
    }

    @Override
    public int getResurceId() {
        return R.drawable.bonus_doc;
    }
}
