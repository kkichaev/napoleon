package com.grsoft.napoleon.documents;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.ReturnDoc;

public class ReturnDocEx extends ReturnDoc {
    public static void init() {
        instance = new ReturnDocEx();
    }
    public ReturnDocEx() {
        super(DOC_NAME, "ReturnRequest", ReturnImplEx.class);
    }
}
