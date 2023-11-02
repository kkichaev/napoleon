package com.grsoft.dataobjects;

import android.annotation.SuppressLint;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.List;

@TableInfo(name="MemoType", keyFields = "id")
@ServerInfo(name="MemoType")
public class MemoType extends DataObject {
    public final static int TYPE_UNLOCK = 0;
    public final static int TYPE_INVOICE = 1;
    public final static int TYPE_EMAIL = 2;

    public String id = "";
    public int type = 2;

    public boolean sendingInvoice() { return type == TYPE_INVOICE; }
    public boolean unlock() { return type == TYPE_UNLOCK; }

    public static MemoType getType(int type) {
        @SuppressLint("DefaultLocale")
        List<MemoType> ret = DbReader.fetch(MemoType.class, String.format("type=%d", type));
        return ret.size() > 0 ? ret.get(0) : new MemoType();
    }
}
