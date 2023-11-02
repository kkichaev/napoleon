package com.grsoft.dataobjects.impl;

import android.content.Context;
import android.text.format.DateUtils;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AutoWaybill;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.dostavka.AutoWaybillEdit;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import java.util.List;

public class AutoWaybillImpl extends CreatableDocument<AutoWaybill> {
    @Override
    public void open(Context context) {
        AutoWaybillEdit.open(context, getRowid());
    }

    @Override
    public boolean init(Context context, String autoId, GpsCoord coord) {
        boolean res = true;

        if (!initFromLast(autoId))
            res = super.init(context, autoId, coord);

        if (res)
            open(context);

        return res;
    }

    private boolean initFromLast(String id) {
        String where = String.format("closed=0 and id='%s' order by created desc", id);

//        DbReader reader = new DbReader();
//        boolean res = reader.select(getData(), getTableName(), where);
//        reader.close();

        List<Long> ids = DbReader.readIds(getTableName(), where, null);

        if (ids.size() > 0)
            return read(ids.get(0), false);

        return false;
    }

    public boolean isClosed(){
        return true;
    }
}
