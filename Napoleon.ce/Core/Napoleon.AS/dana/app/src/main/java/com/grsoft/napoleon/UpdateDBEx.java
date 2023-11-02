package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;

import java.util.List;

public class UpdateDBEx extends UpdateDB {
    int sendedOrders = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.cbRemains).setVisibility(View.GONE);
    }

    @Override
    protected List<DocExportListener> getExportedDocs(boolean docs, boolean visit) {
        List<DocExportListener> ret = super.getExportedDocs(docs, visit);
        for(DocExportListener de : ret) {
            if(de instanceof DocSendListner && ((DocSendListner)de).getObjectName().equals(OrderDoc.instance().getObjectName())) {
                sendedOrders = de.getDocuments().getCount();
            }
        }
        return ret;
    }

    @Override
    protected String getSyncFinishMessage(int traffic) {
        String ret = super.getSyncFinishMessage(traffic);
        if(sendedOrders > 0) {
            if(sendedOrders == 1) {
                ret += "\n(отправлен " + Integer.toString(sendedOrders) + " документ)";
            } else if(sendedOrders < 5) {
                ret += "\n(отправлено " + Integer.toString(sendedOrders) + " документа)";
            } else {
                ret += "\n(отправлено " + Integer.toString(sendedOrders) + " документов)";
            }
        }
        return ret;
    }
}
