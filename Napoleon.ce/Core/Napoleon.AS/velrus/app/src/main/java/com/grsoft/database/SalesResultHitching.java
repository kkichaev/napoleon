package com.grsoft.database;

import com.grsoft.dataobjects.SalesResult;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class SalesResultHitching extends Hitching {
    public static String message;
    public static int result;

    public SalesResultHitching() {
        super(SalesResult.class, "SalesResult");
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        SalesResult data = (SalesResult) rawObject.createDataObject(dataObject);
        message = data.message;
        result = data.status;
    }
}
