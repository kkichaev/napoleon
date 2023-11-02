package com.ksoft.dms.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.ksoft.dms.database.DBHelper;
import com.ksoft.dms.database.entity.NoteItemPart;
import com.ksoft.dms.database.entity.TaskItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteItemPartController {
    public Context context;

    public NoteItemPartController(Context context){
        this.context = context;
    }

    public void insert(NoteItemPart part){
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", part.id);
            cv.put("text", part.text);
            cv.put("note_item_id", part.note_item_id);
            cv.put("pos", part.pos);
            cv.put("type", part.type);

            db.insert("notes_item_parts", null, cv);

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    public List<NoteItemPart> readList(String noteItemID){
        List<NoteItemPart> result = new ArrayList<>();

        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        Cursor c = db.query(getTable(), getProjection(), "note_item_id = ?", new String[]{noteItemID}, null, null, "pos");

        while (c.moveToNext()){
            result.add(createItem(c));
        }
        return result;
    }

    private NoteItemPart createItem(Cursor c) {
        NoteItemPart p = new NoteItemPart();
        p.id = c.getString(c.getColumnIndex("id"));
        p.note_item_id = c.getString(c.getColumnIndex("note_item_id"));
        p.text = c.getString(c.getColumnIndex("text"));
        p.pos = c.getInt(c.getColumnIndex("pos"));
        p.type = c.getInt(c.getColumnIndex("type"));

        return p;
    }

    private String getTable() {
        return "notes_item_parts";
    }

    private String[] getProjection() {
        return new String[]{"id", "note_item_id", "text", "pos", "type"};
    }

    public void replaceList(List<NoteItemPart> items, String itemID) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        db.beginTransaction();

        try {
            db.delete(getTable(), "note_item_id = ?", new String[]{itemID});
            SQLiteStatement insert = db.compileStatement("insert into " + getTable() + " values(?,?,?,?,?)");

            for(NoteItemPart i : items){
                insert.bindString(1, i.id);
                insert.bindString(2, i.note_item_id);
                insert.bindString(3, i.text);
                insert.bindLong(4, i.pos);
                insert.bindLong(5, i.type);

                insert.executeInsert();
            }

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }
}
