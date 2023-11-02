package com.grsoft.database;

import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ActionHitching extends RcvNewHitching {
    public ActionHitching() {
        super(Action.class);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        DataObject dobj = rawObject.createDataObject(dataObject);
        ((Action) dobj).srchName = ((Action) dobj).name.toUpperCase();
        dbProxy.insertRecord(dobj);
        postRead(dobj);
    }
}
