package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.network.DocExportListener;

import java.util.List;

public class OrderDetailEx extends OrderDetail{
    @Override
    public void send() {
        List<DocExportListener> sends = DocType.getDocuments(true, true);
        new DocumentSender(this, findViewById(R.id.btnSend), sends, this).execute((Void[])null);
    }
}
