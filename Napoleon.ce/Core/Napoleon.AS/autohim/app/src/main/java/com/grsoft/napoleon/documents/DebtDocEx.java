package com.grsoft.napoleon.documents;

import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

import java.util.Date;

public class DebtDocEx extends DebtDoc {
    public static void init() {
        instance = new DebtDocEx();
    }

    @Override
    public String getDateDocText(Document<?> doc) {
        String ret =  super.getDateDocText(doc);
        if(doc instanceof DeliveryImpl) {
            Date payDate = ((Delivery)doc.getData()).payDate;
            ret += "\n" + Util.simpleDateFormat.format(payDate);
        }
        return ret;
    }

    @Override
    protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery) {
        return new DocListEx(where, order, LoadDelivery);
    }

    static class DocListEx extends DebtDocList {
        public DocListEx(String where, String order, boolean loadDelivery) {
            super(where, order, loadDelivery);
        }

        @Override
        protected DocList createDeliveryList(String where, String order) {
            if(where.length() > 0) {
                where += " and ";
            }
            where += "sumD > 0";
            return super.createDeliveryList(where, order);
        }
    }
}
