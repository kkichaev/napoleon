package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Gtin;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItemEx;

import android.app.Activity;
import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.ScannedItems;
import com.grsoft.napoleon.BarcodeData;
import com.grsoft.napoleon.PriceCountTabak;
import com.grsoft.napoleon.SalesDetailTabak;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesImplEx extends SalesImpl {
//	@Override
//	protected void beforeItemWrite(OrderItem item, Price p) {
//		((SalesItemEx)item).ido = ((PriceEx)p).ido;
//	}

    public static final int FAIL_NO_ITEM = 1;
    public static final int FAIL_ALREADY_HAVE = 2;
    public static final int FAIL_ITEM_COMPLETE = 3;
    public static final int FAIL_MRC_MISMATCH = 4;
    public static final int FAIL_DOC_FINISHED = 5;
    public static final int FAIL_BC_PARSING = 6;
    public static final int FAIL_QTY_MISMATCH = 7;

    public interface AddEvents {
        void added(SalesItemEx item, BarcodeData data, String bc, Activity cameraActivitys);
        void fail(BarcodeData data, String bc, Activity cameraActivity, int reason);
        void needSelect(List<Pair<PriceEx, Gtin>> items, BarcodeData data, String bc, Activity cameraActivity);
    }

    @Override
    public void open(Context context) {
//        if (((SalesEx) data).tabak > 0)
//            SalesDetailTabak.open(context, this);
//        else
//            super.open(context);
        SalesDetailTabak.open(context, this);
    }

    @Override
    public void editItem(long itemRowid, Context context) {
//        if (((SalesEx) data).tabak > 0)
            PriceCountTabak.open(context, itemRowid, this);
//        else
//            super.editItem(itemRowid, context);
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

    public boolean isComplete() {
        return ((SalesEx) data).compleete > 0;
    }

    String makeItemsIdWhere() {
	    String where = " and id in (";
        for(OrderItem oi : data.items) {
            where += "'" + oi.id + "',";
        }
	    return where + "'')";
    }

    Map<PriceEx, Gtin> loadItems(List<Gtin> src) {
	    String where = "id in(";
	    for(Gtin g : src) {
	        where += "'" + g.id + "',";
        }
	    List<PriceEx> prc = DbReader.fetch(PriceEx.class, where + "'')");
	    Map<PriceEx, Gtin> ret = new HashMap<>();
	    for(PriceEx p : prc) {
	        ret.put(p, Gtin.findGtin(p.id, src));
        }
	    return ret;
    }

    public void addBarcode(String barcode, AddEvents handler, Activity cameraActivity) {
        String bcStrip = barcode.replace("\u001d", "");
        if (isComplete() || isExported()) {
            handler.fail(null, bcStrip, cameraActivity, FAIL_DOC_FINISHED);
            return;
        }

        BarcodeData bd = new BarcodeData(bcStrip);
        if (bd.haveError) {
            handler.fail(bd, bcStrip, cameraActivity, FAIL_BC_PARSING);
            return;
        }

        List<Gtin> gtins = DbReader.fetch(Gtin.class, "barcode='" + bd.itemBC + "'" + makeItemsIdWhere());
        if(gtins.size() == 0) {
            handler.fail(bd, bcStrip, cameraActivity,FAIL_NO_ITEM );
            return;
        }

//        if(gtins.size() == 1) {
//            tryAddBC(gtins.get(0), bd, barcode, handler, cameraActivity);
//            return;
//        }

        Map<PriceEx, Gtin> items = loadItems(gtins);
        List<Pair<PriceEx, Gtin>> mrcMatch = new ArrayList<>();
        for(Map.Entry<PriceEx, Gtin> kv : items.entrySet()) {
            int mrc = (int)((long)kv.getKey().mrc * kv.getValue().qty / Consts.QTY_SCALE);
            if(mrc == bd.cost) {
                mrcMatch.add(new Pair<>(kv.getKey(), kv.getValue()));
            }
        }

        if(mrcMatch.size() == 1) {
            tryAddBC(mrcMatch.get(0).second, bd, barcode, handler, cameraActivity);
            return;
        }

        if(mrcMatch.size() == 0) {
            handler.fail(bd, bcStrip, cameraActivity,FAIL_MRC_MISMATCH);
            return ;
        }

        DbReader r = new DbReader();
        boolean have = false;

        handler.needSelect(mrcMatch, bd, barcode, cameraActivity);
        return;
    }

    public void tryAddBC(Gtin gtin, BarcodeData bd, String bc, AddEvents handler, Activity cameraActivity) {
        boolean added = false;
        int reason = 0;
        for (OrderItem oi : data.items) {
            SalesItemEx se = (SalesItemEx) oi;
            if (se.id.equals(gtin.id)) {
                if(se.haveBC(bc)) {
                    reason = FAIL_ALREADY_HAVE;
                } else if (se.canAdd(gtin)) {
                    se.barcodes.add(new ScannedItems(bc, gtin.qty));
                    setComplete();
                    added = true;
                    handler.added(se, bd, bc, cameraActivity);
                } else {
                    reason = se.scanned() ? FAIL_ITEM_COMPLETE : FAIL_QTY_MISMATCH;
                }
                break;
            }
        }
        if(!added) {
                handler.fail(bd, bc, cameraActivity, reason);
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

    PriceImpl price = new PriceImpl();

    public void setComplete() {
        ((SalesEx) data).compleete = data.items.size() > 0 ? 1 : 0;

        for (OrderItem oi : data.items) {
            price.read("id", oi.id);

            SalesItemEx se = (SalesItemEx) oi;
            if (((PriceEx)price.getData()).marked != 0 && se.factQty() != se.qty) {
                ((SalesEx) data).compleete = 0;

                break;
            }
        }

        price.close();
        write();
    }

    @Override
    public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
        if (!isEditable())
            return true;

//        // нельзя удалить последнюю позицию
//        if (data.items != null && data.items.size() == 1 && qty == 0)
//            return true;

        boolean ret =  super.updateQty(priceImpl, qty, cost, inPack);
        setComplete();
        return ret;
    }

    public int getMarkPlan(){
        int ret = 0;

        for (OrderItem oi : data.items) {
            price.read("id", oi.id);

            SalesItemEx se = (SalesItemEx) oi;
            if (((PriceEx)price.getData()).marked != 0) {
                ret += oi.qty;
            }
        }

        price.close();

        return ret;
    }

    public int getMarkFact(){
        int ret = 0;

        for (OrderItem oi : data.items) {
            price.read("id", oi.id);

            SalesItemEx se = (SalesItemEx) oi;
            if (((PriceEx)price.getData()).marked != 0) {
                ret += ((SalesItemEx) oi).factQty();
            }
        }

        price.close();

        return ret;
    }
}
