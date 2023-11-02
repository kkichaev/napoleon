package com.grsoft.database;

import android.database.Cursor;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.FBTransfer;
import com.grsoft.dataobjects.FBTransferCommit;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FBTransferCommitHitching extends Hitching {

    static Date CHECK_DATE = new Date(10 * 24 * 3600 * 1000); // 10 jan 1970

    Set<String> numbers = null;
    public FBTransferCommitHitching(String name) {
        super(FBTransferCommit.class, name);
    }

    @Override
    public void onEnd() {
        super.onEnd();
    }

    void loadNumbers() {
        if(numbers != null)
            return;

        numbers = new HashSet<>();
        try {
            Cursor c = DataBaseManager.getDataBase().rawQuery("select number from " + new FBTransfer().getTableName(), null);
            while(c.moveToNext()) {
                numbers.add(c.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        FBTransferCommit src = (FBTransferCommit) rawObject.createDataObject(dataObject);
        if(src.created.compareTo(CHECK_DATE) < 0) {
            loadNumbers();
            if(!numbers.contains(src.number)) {
                FBTransfer dest = FBTransfer.createFrom(src);
                src.created = dest.created;
                numbers.add(src.number);
            }
        }

        dbProxy.insertRecord(src);
    }
}
