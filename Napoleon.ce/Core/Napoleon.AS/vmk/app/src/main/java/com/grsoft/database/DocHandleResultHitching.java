package com.grsoft.database;

import com.grsoft.dataobjects.DocHandleResult;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DocHandleResultHitching extends Hitching {

    public static DocHandleResult Result = new DocHandleResult();

    public DocHandleResultHitching() {
        super(DocHandleResult.class, "DocResult");
    }

    @Override
    public void onStart() {
        super.onStart();
        Result.message = "";
        Result.status = DocHandleResult.STATUS_OK;
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        Result = (DocHandleResult) rawObject.createDataObject(Result.getClass());
    }
}
