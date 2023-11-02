package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.WhOrder;
import com.grsoft.dataobjects.impl.WhOrderImpl;
import com.grsoft.napoleon.R;

public class WhOrderDoc extends OrderDoc {
    static WhOrderDoc docInstance;

    public static WhOrderDoc instance() {
        if(docInstance == null) {
            docInstance = new WhOrderDoc();
        }
        return docInstance;
    }

    WhOrderDoc() {
        super("WhOrder", "WhRequest", WhOrderImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.wh_order;
    }

    @Override
    public int getResurceId() {
        return R.drawable.wh_req_doc;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.wh_req_doc_2;
    }
}
