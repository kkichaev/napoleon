package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.BCItem;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ShipmentItem;
import com.grsoft.util.Consts;

import java.util.HashSet;
import java.util.Set;

public class DShipmentImplEx extends DShipmentImpl{
    Integer needScanned = null;

    public static final int ITEM_COMPLETE = -1;
    public static final int ITEM_ADDED = -2;
    public static final int FAIL_NO_ITEM = 1;
    public static final int FAIL_ALREADY_HAVE = 2;
    public static final int FAIL_ITEM_COMPLETE = 3;
    public static final int FAIL_DOC_FINISHED = 5;
    public static final int FAIL_BC_PARSING = 6;

//    @Override
//    public void initWaybillItem(DeliveryItem di, DWaybillDocumentItem i) {
//        super.initWaybillItem(di, i);
//        i.outqty = 0;
//    }

    public boolean isScanned() {
        PriceImpl pi = new PriceImpl();
        PriceEx pe = (PriceEx) pi.getData();

        for(DWaybillDocumentItem di : data.items) {
            pe.id = di.id;
            if(!pi.read() || pe.barcode.length() == 0)
                continue;

            if(!((ShipmentItem)di).isScanned()) {
                pi.close();
                return false;
            }
        }

        pi.close();
        return true;
    }

    public int addBarcode(String barcode) {
        if(barcode.length() < 16)
            return FAIL_BC_PARSING;
        String bcStrip = barcode.replace("\u001d", "");
        String mark = bcStrip.substring(2, 16);
        String where = "substr('00000000' || barcode, -14) = '" + mark + "'";

        Set<String> items = new HashSet<>();
        for(PriceEx pe : DbReader.fetch(PriceEx.class, where) ){
            items.add(pe.id);
        }

        if(items.size() == 0) {
            return FAIL_NO_ITEM;
        }

        int reason = 0;
        for(DWaybillDocumentItem oi : data.items) {
            if(!items.contains(oi.id))
                continue;

            ShipmentItem sie = (ShipmentItem) oi;
            if(sie.isScanned()) {
                if(reason == 0)
                    reason = FAIL_ITEM_COMPLETE;
                continue;
            }
            if(sie.haveBC(barcode)) {
                return FAIL_ALREADY_HAVE;
            }

            BCItem bci = new BCItem();
            bci.mark = barcode;
            sie.barcodes.add(bci);
            int cq = sie.barcodes.size() * Consts.QTY_SCALE;
            if(sie.outqty < cq) {
                sie.outqty = cq;
            }

            reason = sie.isScanned() ? ITEM_COMPLETE : ITEM_ADDED;
            write();
            break;
        }

        return reason == 0 ? FAIL_NO_ITEM : reason;
    }

    public int need_scanned() {
        if(needScanned == null) {
            int q = 0;
            PriceImpl pi = new PriceImpl();
            PriceEx pe = (PriceEx) pi.getData();

            for (DWaybillDocumentItem sie : data.items) {
                pe.id = sie.id;
                if(!pi.read() || pe.barcode.length() == 0)
                    continue;
                q += sie.outqty / Consts.QTY_SCALE;
            }
            pi.close();
            needScanned = q;
        }

        return needScanned;
    }

    public void updateQty(String id, int newQty) {
        for(DWaybillDocumentItem oi : data.items) {
            if(oi.id.equals(id)) {
                oi.outqty = newQty;
                if(oi.inqty != oi.outqty) {
                    oi.cause = "не найдено";
                }
                break;
            }
        }
    }

    public int scanned() {
        int s = 0;
        for(DWaybillDocumentItem oi : data.items) {
            s += ((ShipmentItem)oi).barcodes.size();
        }
        return s;
    }
}
