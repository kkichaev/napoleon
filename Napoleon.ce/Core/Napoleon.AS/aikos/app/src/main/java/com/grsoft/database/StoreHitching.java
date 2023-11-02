package com.grsoft.database;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Store;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class StoreHitching extends Hitching {
    String str = "";

    public StoreHitching() {
        super(Store.class);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        Store data = rawObject.createDataObject(dataObject);
        if(str.length() > 0) str += ";";
        str += data.name + "\t" + data.id ;
    }

    @Override
    public void onEnd() {
        super.onEnd();

        ConfigImpl ci = new ConfigImpl();
        Config cfg = ci.getData();
        cfg.key = Store.CFG_KEY;
        cfg.value = str;
        ci.write();
        ci.close();
    }
}
