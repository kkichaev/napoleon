package com.grsoft.napoleon;

import android.content.SharedPreferences;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
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

    @Override
    protected void onResume() {
        super.onResume();

        DocExportListener toExp = OrderDoc.instance().getDirtyDocuments();

        if(toExp != null){
            boolean haveBadOrder = false;
            OrgImpl oi = new OrgImpl();

            DocList ords = toExp.getDocuments();

            for(Document<?> d : ords){
                OrderImpl ord = (OrderImpl)d;

                long s = ord.sum();

                if(oi.read("id", ord.getId())) {
                    if (((OrgEx)oi.getData()).limitsum > 0) {
                        DeliveryInfo deliveryInfo = DeliveryInfo.collectDelivery(oi.getData().id);

                        if (deliveryInfo.count > 0 && (s + deliveryInfo.sum > ((OrgEx) oi.getData()).limitsum)) {
                            haveBadOrder = true;
                            break;
                        }
                    }
                }
            }

            oi.close();
            if(haveBadOrder)
                OrderListM.openOrdList(this);
        }
    }
}
