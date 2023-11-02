package com.grsoft.napoleon;

import android.content.SharedPreferences;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;

public class MainEx extends Main {
    @Override
    protected void openSync(SharedPreferences pref) {
        boolean canOpen = true;

        DocExportListener del = OrderDoc.instance().getDirtyDocuments();
        DocList dl = del.getDocuments();
        for(Document<?> d : dl) {
            if(!((OrderImplEx)d).isGood()) {
                d.open(this);
                canOpen = false;
                break;
            }
        }

        if(canOpen)
            super.openSync(pref);
    }
}
