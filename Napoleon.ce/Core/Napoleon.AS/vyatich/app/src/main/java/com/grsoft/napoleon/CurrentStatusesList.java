package com.grsoft.napoleon;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentStatusesList {
    private static List<String> list = new ArrayList<String>();

    public static void resetCash(){
        list.clear();
    }

    public static List<String> getList(){
        if (list.size() > 0)
            return  list;

        DbWriter.checkDBTable(Order.class);

        SQLiteDatabase db = DataBaseManager.getDataBase();
        Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Order.class), new String[]{"podRemark"}, null, null, null, null, null);

        while (c.moveToNext()){
            String pod = c.getString(c.getColumnIndex("podRemark"));

            if (!list.contains(pod)){
                list.add(pod);
            }
        }

        Collections.sort(list);
        return  list;
    }
}
