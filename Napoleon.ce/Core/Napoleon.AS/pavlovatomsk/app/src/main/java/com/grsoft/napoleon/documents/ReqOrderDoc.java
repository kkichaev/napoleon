package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.network.DocExportListener;

import java.util.ArrayList;

public class ReqOrderDoc extends OrderDocEx {

    static ReqOrderDoc idoc = null;

    public static ReqOrderDoc instance() {
        if(idoc == null)
            idoc = new ReqOrderDoc();
        return idoc;
    }

    ReqOrderDoc() {
        objName = "ReqOrder";
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        CreatableDocument<?> cd = (CreatableDocument<?>)create();
        DocExportListener dl =  new DocSendListner(getObjectName(),
                (Class<? extends CreatableDocument<?>>) cd.getClass(),
                "params", ParamState.ofExported);

        ArrayList<Long> needRemove = new ArrayList<Long>();
        DocList docs = dl.getDocuments();
        for(Document<?> d : docs) {
            OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>) d;
            if( doc.isEmpty() || !((OrderEx)doc.getData()).needDecision() ) {
                needRemove.add(doc.getRowid());
            }
        }
        docs.removeDocuments(needRemove);
        docs.close();
        return dl;
    }
}
