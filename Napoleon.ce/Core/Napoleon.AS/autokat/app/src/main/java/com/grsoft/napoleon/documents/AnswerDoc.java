package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.AnswerImplEx;
import com.grsoft.napoleon.R;

public class AnswerDoc extends DocType{
    static AnswerDoc instance  = null;

    public static AnswerDoc instance() {
        if(instance == null)
            instance = new AnswerDoc();
        return instance;
    }

    AnswerDoc() {
        super("", "AnswerDoc", AnswerImplEx.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.selling_title;
    }

    @Override
    public int getResurceId() {
        return R.drawable.ic_sell;
    }
}
