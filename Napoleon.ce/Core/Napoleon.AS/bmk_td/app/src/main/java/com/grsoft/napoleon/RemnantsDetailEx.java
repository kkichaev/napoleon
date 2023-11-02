package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.network.DocExportListener;

import java.util.List;

public class RemnantsDetailEx extends RemnantsDetail{
    @Override
    protected void send() {
        List<DocExportListener> sends = DocType.getDocuments(true, true);
        new DocumentSender(this, findViewById(R.id.btnSend), sends, null).execute((Void[]) null);
    }
}
