package com.grsoft.database;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.StoreQty;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreQtyHitching extends Hitching {

    Map<String,PriceEx> loaded = new HashMap<>();
    List<String> stores = new ArrayList<>();
    DbReader r = new DbReader();

    public StoreQtyHitching() {
        super(StoreQty.class);
    }

    @Override
    public void onStart() {
        StringBuilder sb = new StringBuilder();
        ConfigImpl ci = new ConfigImpl();
        if(ci.getValue(sb, Store.CFG_KEY)) {
            List<KeyValue> vals = new ArrayList<>();
            DialogHelper.makeListWithKey(sb.toString(), vals, "");
            for(KeyValue kv : vals)
                stores.add(kv.key.toString());
        }

        try{
            String stmt = "update [" + new Price().getTableName() + "] set qty=0, whQty=null, whStates=0";
            DataBaseManager.getDataBase().execSQL(stmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean debug = false;
    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        StoreQty sq = rawObject.createDataObject(dataObject);
        int idx = stores.indexOf(sq.id);
        if(idx < 0)
            return;

        if(sq.idItem.indexOf("00022509") > 0) {
            debug = true;
        }
        PriceEx el = loaded.get(sq.idItem);
        if(el == null) {
            el = new PriceEx();
            if(!r.select(el , el.getTableName(), String.format("id='%s'", sq.idItem))) {
                return;
            }
            loaded.put(sq.idItem, el);
        }

        if(idx == 0) {
            el.qty = sq.qty;
            el.date = sq.date;
            el.arrival = sq.arrival;
        } else {
            while(el.whQty.size() < idx) {
                el.whQty.add(new PriceQtyEx());
            }
            PriceQtyEx pi = (PriceQtyEx) el.whQty.get(idx-1);
            pi.qty = sq.qty;
            pi.date = sq.date;
            pi.arrival = sq.arrival;
        }
    }

    @Override
    public void onEnd() {
        DbWriter w = new DbWriter();
        for(Price el : loaded.values()) {
            el.updateWhState();
            w.insertRecord(el);
        }
        w.close();
        r.close();
    }
}
