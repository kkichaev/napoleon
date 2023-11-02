package com.grsoft.database;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.dataobjects.OrgTask;

public class OrgTaskHitching extends Hitching{
    public OrgTaskHitching() {
        super(OrgTask.class, "OrgTask");

        DbWriter.checkDBTable(OrgTask.class);
        SQLiteDatabase db =  DataBaseManager.getDataBase();
        db.execSQL("create table if not exists OrgTaskOld (id text primary key);");
        db.execSQL("delete from OrgTaskOld");
        db.execSQL("insert into OrgTaskOld select id from agentorgtask");
    }
}
