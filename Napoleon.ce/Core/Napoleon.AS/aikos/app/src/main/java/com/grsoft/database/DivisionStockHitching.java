package com.grsoft.database;

import com.grsoft.dataobjects.AikosDivision;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DivisionStock;
import com.grsoft.dataobjects.DivisionStockItem;
import com.grsoft.dataobjects.DivisionStockRcv;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.HashMap;
import java.util.Map;

public class DivisionStockHitching extends Hitching{

    Map<String, Integer> dvsn = new HashMap<>();
    Map<String, DivisionStock> stock = new HashMap<>();

    public DivisionStockHitching() {
        super(DivisionStockRcv.class, "DivisionStock");
    }

    @Override
    public void prepareReading() {
        DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        DivisionStockRcv data = rawObject.createDataObject(dataObject);
        Integer idx = dvsn.get(data.division);
        if(idx == null) {
            idx = dvsn.size() + 1;
            dvsn.put(data.division, idx);
        }
        DivisionStock ds = stock.get(data.id);
        if(ds == null) {
            ds = new DivisionStock();
            ds.id = data.id;
            stock.put(data.id, ds);
        }
        DivisionStockItem dsi = new DivisionStockItem();
        dsi.division = idx;
        dsi.qty = data.qty;
        ds.items.add(dsi);
    }

    @Override
    public void onEnd() {
        DbWriter w = new DbWriter();
        DbWriter.checkDBTable(DivisionStock.class);
        for(DivisionStock ds : stock.values()) {
            w.insertRecord(ds);
        }
        w.close();
        DbWriter.checkDBTable(AikosDivision.class);
        w = new DbWriter();
        for(Map.Entry<String, Integer> kv : dvsn.entrySet()) {
            AikosDivision ad = new AikosDivision();
            ad.id = kv.getValue();
            ad.name = kv.getKey();
            w.insertRecord(ad);
        }
        w.close();

        super.onEnd();
    }
}
