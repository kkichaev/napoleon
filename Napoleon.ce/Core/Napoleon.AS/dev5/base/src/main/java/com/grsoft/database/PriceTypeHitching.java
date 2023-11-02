package com.grsoft.database;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.PriceType;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceTypeHitching extends Hitching {
    String str = "";
    public PriceTypeHitching() {
        super(PriceType.class);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        PriceType pt = rawObject.createDataObject(dataObject);
        if(str.length() > 0) str += ";";
        str += pt.name + "\t" + pt.id ;
    }

    @Override
    public void onEnd() {
        super.onEnd();

        ConfigImpl ci = new ConfigImpl();
        Config cfg = ci.getData();
        cfg.key = PriceType.CFG_KEY;
        cfg.value = str;
        ci.write();
        ci.close();
    }
}
