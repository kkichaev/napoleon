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

public class SalesImplEx extends SalesImpl {

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

    public boolean addBarcode(String bc) {
        if (isCompleete() || isExported())
            return false;

        boolean ret = false;

        BarcodeData bd = new BarcodeData(bc);
        if (bd.havError)
            return ret;

        PriceEx pe = new PriceEx();
        DbReader r = new DbReader();
        boolean have = r.select(pe, pe.getTableName(), "barcode='" + bd.itemBC + "'");
        r.close();
        if (have) {
            int itemCost = CostStrategy.getInstance(this.getClass()).getItemCost(pe, this);
            int cost = bd.isItemCode ? bd.cost : (int) ((long) bd.cost * Consts.QTY_SCALE / pe.qtyInPack);
            if (cost != itemCost) {
                return false;
            }

            for (OrderItem oi : data.items) {
                SalesItemEx se = (SalesItemEx) oi;
                if (se.id.equals(pe.id)) {
                    if (!se.haveBC(bc) && se.canAdd(bd)) {
                        se.barcodes.add(new ScannedItems((bc)));
                        setCompleete();
                        write();
                        ret = true;
                    }
                    break;
                }
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
    public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
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
