package com.grsoft.database;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
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

    Map<Object, PriceEx> data;
    List<String> stores = new ArrayList<>();

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
        data = DbReader.fetchDic(PriceEx.class, "id");
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        StoreQty sq = rawObject.createDataObject(dataObject);
        int idx = stores.indexOf(sq.id);
        if(idx < 0)
            return;

        Price el = data.get(sq.idItem);
        if(el == null)
            return;

        if(idx == 0)
            el.qty =sq.qty;
        else {
            while(el.whQty.size() < idx) {
                el.whQty.add(new PriceQtyItem());
            }
            el.whQty.get(idx-1).qty = sq.qty;
        }
    }

    @Override
    public void onEnd() {
        DbWriter w = new DbWriter();
        for(Price el : data.values()) {
            if(el.qty != 0 || el.whQty.size() > 0) {
                el.updateWhState();
                w.insertRecord(el);
            }
        }
        w.close();
    }
}
