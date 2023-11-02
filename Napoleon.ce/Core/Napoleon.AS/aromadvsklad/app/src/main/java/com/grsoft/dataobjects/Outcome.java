package com.grsoft.dataobjects;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.BarcodeData;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@TableInfo(name="Outcome", keyFields = "created", indexes = "number")
@ServerInfo(name="OutDoc")
public class Outcome extends CreateDocDataObject {
    public String number = "";
    public List<OutcomeItem> items = new ArrayList<>();

    public List<PriceEx> makeIntersect(List<PriceEx> pitems) {
        List<PriceEx> ret = new ArrayList<>();

        for(PriceEx p : pitems) {
            for(OutcomeItem oi : items) {
                if(oi.id.equals(p.id))
                    ret.add(p);
            }
        }

        return ret;
    }

    public interface AddEvents {
        void added(OutcomeItem item, BarcodeData data);
        void fail(BarcodeData data, String bc);
        void needSelect(List<PriceEx> items, BarcodeData data, String bc);
    }

    public int compleete = 0;

    public String getText() {
        if( DocumentUtils.isExported(params))
            return "отправлен";
        return isCompleete() ? "завершен" : "в работе";
    }

    public boolean isCompleete() {
        if(compleete == 0) {
            for (OutcomeItem oi : items) {
                if (!oi.isCompleete())
                    return false;
            }
        }
        compleete = 1;
        writeDoc();
        return  true;
    }

    void writeDoc() {
        DbWriter w = new DbWriter();
        w.insertRecord(this);
        w.close();
    }

    public void initFrom(Context outcomeEdit, Delivery d) {
        GpsCoord gpsCoord = GPSUtilNew.getLastKnownLocation();

        date = Util.getDate();
        created = Util.getDateTime();

        id = d.id;
        latitude = gpsCoord.latitude;
        longitude = gpsCoord.longitude;
        stltime = gpsCoord.time;
        params = 0;

        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        timeZone = -tz.getOffset(now.getTime()) / (60*1000);

        number = d.number;
        for(DeliveryItem i : d.items) {
            DeliveryItemEx ii = (DeliveryItemEx) i;
            OutcomeItem oi = new OutcomeItem();

            oi.id = ii.id;
            oi.qty = ii.qty;
            oi.packQty = ii.packQty;
            oi.boxQty = ii.boxQty;

            items.add(oi);
        }

        writeDoc();
    }

    public boolean isEditable() { return (params & ParamState.ofExported) == 0;   }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    public void tryAddBC(PriceEx pe, BarcodeData bd, String bc, AddEvents handler) {
        boolean added = false;
        for (OutcomeItem oi : items) {
            if (oi.id.equals(pe.id)) {
                if (!oi.haveBC(bc) && oi.canAdd(bd)) {
                    oi.barcodes.add(new ScannedItems(bc));
                    if(bd.isBox)
                        oi.inputBoxQty += Consts.QTY_SCALE;
                    else if(bd.isItemCode)
                        oi.inputQty += Consts.QTY_SCALE;
                    else
                        oi.inputPackQty += Consts.QTY_SCALE;

                    if(!isCompleete())
                        writeDoc();
                    handler.added(oi, bd);
                    added = true;
                }
                break;
            }
        }
        if(!added) {
            handler.fail(bd, bc);
        }
    }

    public void addBarcode(String bc, AddEvents handler) {
        if(isCompleete()) {
            handler.fail(null, bc);
            return;
        }

        BarcodeData bd = new BarcodeData(bc);
        if (bd.haveError) {
            handler.fail(bd, bc);
            return;
        }

        PriceEx pe = new PriceEx();
        DbReader r = new DbReader();
        boolean have = false;

        String where = "bcBox LIKE '%" + bd.itemBC + "%'";
        if(bd.mayByBoxed) {
            final List<PriceEx> items = new ArrayList<>();
            DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>(true) {
                @Override
                public boolean travel(DataTraveler<PriceEx> item) {
                    items.add(item.data);
                    return true;
                }
            }, where);

            if(items.size() > 0) {
                bd.isItemCode = false;
                bd.isBox = true;

                if(items.size() == 1) {
                    tryAddBC(items.get(0), bd, bc, handler);
                } else {
                    handler.needSelect(items, bd, bc);
                }

                return;
            }
        }

        boolean bdo = false;
        if(bd.isBox)
            bdo = r.select(pe, pe.getTableName(), where);
        if(bdo) {
            have = true;
        }

        if(!have) {
            List<String> ids = new ArrayList<>();
            ids.add(bd.itemBC);
            if(bd.checkItemCode.length() > 0)
                ids.add(bd.checkItemCode);

            for(String idbc : ids) {
                List<PriceEx> data = findPriceItem(bd, idbc, r);
                if (data.size() > 1) {
                    handler.needSelect(data, bd, bc);
                    r.close();
                    return;
                }
                if (data.size() == 1) {
                    pe = data.get(0);
                    have = true;
                    break;
                }
            }
        }
        r.close();

        if(!have) {
            handler.fail(bd, bc);
        } else {
            tryAddBC(pe, bd, bc, handler);
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

//    public OutcomeItem addBarcode(String bc) {
//        if(isCompleete())
//            return null;
//
//        BarcodeData bd = new BarcodeData(bc);
//        if (bd.haveError)
//            return null;
//
//        PriceEx pe = new PriceEx();
//        DbReader r = new DbReader();
//        boolean have = false;
//
//        boolean bdo = false;
//        if(bd.isBox)
//            bdo = r.select(pe, pe.getTableName(), "bcBox LIKE '%" + bd.itemBC + "%'");
//        if(bdo) {
//            have = true;
//        }
//
//        if(!have) {
//            have = findPriceItem(bd, pe, bd.itemBC, r);
//            if(!have && bd.checkItemCode.length() > 0)
//                have = findPriceItem(bd, pe, bd.checkItemCode, r);
//        }
//        r.close();
//
//        OutcomeItem ret = null;
//        if (have) {
//            for (OutcomeItem oi : items) {
//                if (oi.id.equals(pe.id)) {
//                    if (!oi.haveBC(bc) && oi.canAdd(bd)) {
//                        oi.barcodes.add(new ScannedItems(bc));
//                        if(bd.isBox)
//                            oi.inputBoxQty += Consts.QTY_SCALE;
//                        else if(bd.isItemCode)
//                            oi.inputQty += Consts.QTY_SCALE;
//                        else
//                            oi.inputPackQty += Consts.QTY_SCALE;
//
//                        if(!isCompleete())
//                            writeDoc();
//                        ret = oi;
//                    }
//                    break;
//                }
//            }
//        }
//        return ret;
//    }
}
