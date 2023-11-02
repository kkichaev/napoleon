package com.grsoft.database;

import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceCostRcv;
import com.grsoft.dataobjects.PriceType;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceCostHitching extends Hitching {
    Map<String, PriceCost> data = new HashMap<>();
    List<String> priceTypes = new ArrayList<>();

    public PriceCostHitching() {
        super(PriceCostRcv.class);
    }

    @Override
    public void onStart() {
        StringBuilder sb = new StringBuilder();
        ConfigImpl ci = new ConfigImpl();
        if(ci.getValue(sb, PriceType.CFG_KEY)) {
            List<KeyValue> vals = new ArrayList<>();
            DialogHelper.makeListWithKey(sb.toString(), vals, "");
            for(KeyValue kv : vals)
                priceTypes.add(kv.key.toString());
        }
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        PriceCostRcv rcv = rawObject.createDataObject(dataObject);
        PriceCost pc = data.get(rcv.idItem);
        if(pc == null) {
            pc = new PriceCost();
            pc.id = rcv.idItem;
            int size = priceTypes.size();

            pc.cost = new int[size];
            data.put(rcv.idItem, pc);
        }

        int index = priceTypes.indexOf(rcv.id);
        if(index >= 0) {
            pc.cost[index] = rcv.cost;
        }
    }

    @Override
    public void onEnd() {
        DbWriter.eraseTable(PriceCost.class);
        DbWriter w = new DbWriter();

        for(PriceCost pc : data.values()) {
            w.insertRecord(pc);
        }

        w.close();
    }
}
