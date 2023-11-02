package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
    @Override
    protected void adjustViewForDocType(DocType docType) {
        if(docType == DebtDoc.instance()) {
            BalanceView.open(this, org.getData().id);
            finish();
        }
        super.adjustViewForDocType(docType);
    }
}
