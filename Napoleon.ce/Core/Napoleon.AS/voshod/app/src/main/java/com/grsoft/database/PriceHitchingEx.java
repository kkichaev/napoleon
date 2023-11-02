package com.grsoft.database;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.PriceHelper;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceHitchingEx extends PriceHitching {
    @Override
    protected void beforeInsert(Price dobj) {
        super.beforeInsert(dobj);
        PriceHelper.put(dobj.id);
    }

    @Override
    public void onEnd() {
        super.onEnd();
        PriceHelper.save();
    }
}
