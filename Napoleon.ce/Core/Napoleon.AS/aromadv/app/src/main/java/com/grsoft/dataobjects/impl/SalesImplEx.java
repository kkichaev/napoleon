package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.ScannedItems;
import com.grsoft.napoleon.BarcodeData;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.PriceCountTabak;
import com.grsoft.napoleon.SalesDetailTabak;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

import java.util.ArrayList;
import java.util.List;

public class SalesImplEx extends SalesImpl {

    public interface AddEvents {
        void added(SalesItemEx item, BarcodeData data);
        void fail(BarcodeData data, String bc);
        void needSelect(List<PriceEx> items, BarcodeData data, String bc);
    }

    @Override
    public void open(Context context) {
        if (((SalesEx) data).tabak > 0)
            SalesDetailTabak.open(context, this);
        else
            super.open(context);
    }

    @Override
    public void editItem(long itemRowid, Context context) {
        if (((SalesEx) data).tabak > 0)
            PriceCountTabak.open(context, itemRowid, this);
        else
            super.editItem(itemRowid, context);
    }

//    @Override
//    public boolean delete() {
//        if (data.items != null && data.items.size() > 0)
//            return true;
//        return super.delete();
//    }

    @Override
    public boolean initSilent(String orgId, GpsCoord coord) {
        return super.initSilent(orgId, coord);
    }

    public boolean isCompleete() {
        return ((SalesEx) data).tabak > 0 ? (((SalesEx) data).compleete > 0) : (data.items.size() > 0);
    }

    public boolean addBarcode(String bc, AddEvents handler) {
        if (isCompleete() || isExported()) {
            handler.fail(null, bc);
            return false;
        }

        boolean ret = false;

        BarcodeData bd = new BarcodeData(bc);
        if (bd.haveError) {
            handler.fail(bd, bc);
            return ret;
        }

        PriceEx pe = new PriceEx();
        DbReader r = new DbReader();
        boolean have = false;

        List<String> ids = new ArrayList<>();
        ids.add(bd.itemBC);
        if(bd.checkItemCode.length() > 0)
            ids.add(bd.checkItemCode);

        for(String idbc : ids) {
            List<PriceEx> data = findPriceItem(bd, idbc, r);
            if (data.size() > 1) {
                handler.needSelect(data, bd, bc);
                r.close();
                return true;
            }
            if (data.size() == 1) {
                pe = data.get(0);
                have = true;
                break;
            }
        }
        r.close();

        if(!have) {
            handler.fail(bd, bc);
        } else {
            tryAddBC(pe, bd, bc, handler);
        }

        return ret;
    }

    public void tryAddBC(PriceEx pe, BarcodeData bd, String bc, AddEvents handler) {
        boolean added = false;
        for (OrderItem oi : data.items) {
            SalesItemEx se = (SalesItemEx) oi;
            if (se.id.equals(pe.id)) {
                if (!se.haveBC(bc) && se.canAdd(bd)) {
                    se.barcodes.add(new ScannedItems(bc, bd));
                    setCompleete();
                    write();
                    added = true;
                    handler.added(se, bd);
                }
                break;
            }
        }
        if(!added) {
            handler.fail(bd, bc);
        }
    }

    private List<PriceEx> findPriceItem(BarcodeData bd, String bc, DbReader r) {
        List<PriceEx> ret = new ArrayList<>();
        PriceEx pe = new PriceEx();
        boolean bdo = r.select(pe, pe.getTableName(), "barcode like '%" + bc + "%'");
        while (bdo) {
            int inpack = pe.qtyInPack;
            if (inpack == 0)
                inpack = Consts.QTY_SCALE;
            int checkCost = bd.isItemCode ? bd.cost : (int) ((long) bd.cost * Consts.QTY_SCALE / inpack);
            int itemCost = pe.mrc;
            if (checkCost == itemCost) {
                ret.add(pe);
                pe = new PriceEx();
            }
            bdo = r.selectNext(pe);
        }
        return ret;
    }

    public List<PriceEx> makeIntersect(List<PriceEx> pitems) {
        List<PriceEx> ret = new ArrayList<>();

        for(PriceEx p : pitems) {
            for(OrderItem oi : data.items) {
                if(oi.id.equals(p.id))
                    ret.add(p);
            }
        }

        return ret;
    }

    private void setCompleete() {
        for (OrderItem oi : data.items) {
            SalesItemEx se = (SalesItemEx) oi;
            if (se.factPack() != se.packQty || se.factQty() != se.itemQty) {
                ((SalesEx) data).compleete = 0;
                return;
            }
        }
        ((SalesEx) data).compleete = 1;
    }

    @Override
    public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
        if (!isEditable())
            return true;

//        // нельзя удалить последнюю позицию
//        if (data.items != null && data.items.size() == 1 && qty == 0)
//            return true;

        boolean ret =  super.updateQty(priceImpl, qty, cost, inPack);
        setCompleete();
        return ret;
    }
}
