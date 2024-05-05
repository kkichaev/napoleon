package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Selling;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

public class BSellingImpl extends SellingImpl {

    @Override
    public void initDoc(Context context, GpsCoord gpsCoord, ScriptImpl owner, ScriptDefItem item) {
        super.initDoc(context, gpsCoord, owner, item);
        data.bmark = 1;
        data.title = item.name;
        data.payType = Selling.PAY_TYPE_CASH;
    }
}
