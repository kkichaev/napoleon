package com.ksoft.dms.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ksoft.dms.database.DBHelper;

public class IDNumberController {
    public int generateID(Context context){
        int res = 0;
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

        Cursor c = db.query("sequence", new String[]{"number"},"id='id'",null,null,null,null);

        if (c.moveToFirst()){
            res = c.getInt(c.getColumnIndex("number"));
            res += 1;

            ContentValues cv = new ContentValues();
            cv.put("number", res);

            db.update("sequence", cv, "id='id'", null);
        }else{
            ContentValues cv = new ContentValues();
            cv.put("id", "id");
            cv.put("number", res);

            db.insert("sequence", null, cv);
        }

        c.close();

        return res;
    }
}
