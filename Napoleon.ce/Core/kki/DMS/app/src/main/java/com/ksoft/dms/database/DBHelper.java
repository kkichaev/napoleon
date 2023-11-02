package com.ksoft.dms.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final int version = 12;
    public static final String name = "dms.db";

    public DBHelper(@Nullable Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table 'notes' ('id' text, 'note' text, 'title' text, 'created' integer, 'color' integer, primary key ('id'));");
        db.execSQL("create index 'notes_created' on 'notes' ('created');");

        db.execSQL("create table 'record' ('id' text, 'noteid' text, 'file' text, primary key ('id') , constraint fk_record foreign key ('noteid') references 'notes' ('id') on delete cascade);");
        db.execSQL("create index fki_record on 'record' ('noteid');");

        db.execSQL("create table 'notes_items' ('id' text, 'noteid' text, 'note' text, 'note_html' text, 'title' text, 'created' integer, 'color' integer, primary key ('id'), constraint fk_notes_items foreign key ('noteid') references 'notes' ('id') on delete cascade);");
        db.execSQL("create index 'idx_notes_items_created' on 'notes' ('created');");
        db.execSQL("create index 'fki_notes_items' on 'notes_items' ('noteid');");

        db.execSQL("create table 'task' ('id' text, 'created' integer, 'date' integer, 'schedule' text, 'text' text, 'status' integer, 'finish' integer, 'alarmid' integer, primary key('id'));");
        db.execSQL("create table 'task_items'('id' text, 'taskid' text, 'text' text, 'status' integer, 'pos' integer, primary key('id'), constraint fk_task_items foreign key ('taskid') references 'task' ('id') on delete cascade);");
        db.execSQL("create index 'fki_task_items' on 'task_items' ('taskid');");
        db.execSQL("create table 'sequence'('id' text, 'number' integer, primary key('id'))");

        db.execSQL("create table 'notes_item_parts' ('id' text, 'note_item_id' text, 'text' text, 'pos' integer, 'type' integer, primary key ('id'), constraint fk_notes_item_parts foreign key ('note_item_id') references 'notes_items' ('id') on delete cascade);");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2){
            db.execSQL("alter table 'notes' add column title text;");
        }

        if (oldVersion < 3) {
            db.execSQL("alter table 'notes' add column created integer;");
            db.execSQL("create index 'notes_created' on 'notes' ('created');");
        }

        if (oldVersion < 4) {
            db.execSQL("alter table 'notes' add column color integer;");
        }

        if (oldVersion < 5){
            db.execSQL("create table 'record' ('id' text, 'noteid' text, 'file' text, primary key ('id') , constraint fk_record foreign key ('noteid') references 'notes' ('id') on delete cascade);");
            db.execSQL("create index fki_record on 'record' ('noteid');");
        }

        if (oldVersion < 6){
            db.execSQL("create table 'notes_items' ('id' text, 'noteid' text, 'note' text, 'title' text, 'created' integer, 'color' integer, primary key ('id'), constraint fk_notes_items foreign key ('noteid') references 'notes' ('id') on delete cascade);");
            db.execSQL("create index 'idx_notes_items_created' on 'notes' ('created');");
            db.execSQL("create index 'fki_notes_items' on 'notes_items' ('noteid');");
        }

        if (oldVersion < 7) {
            db.execSQL("alter table 'notes_items' add column 'note_html' text;");
        }

        if (oldVersion < 10) {
            db.execSQL("create table 'task' ('id' text, 'created' integer, 'date' integer, 'schedule' text, 'text' text, 'status' integer, 'finish' integer, primary key('id'))");
            db.execSQL("create table 'task_items'('id' text, 'taskid' text, 'text' text, 'status' integer, 'pos' integer, primary key('id'), constraint fk_task_items foreign key ('taskid') references 'task' ('id') on delete cascade);");
            db.execSQL("create index 'fki_task_items' on 'task_items' ('taskid');");
        }

        if (oldVersion < 11){
            db.execSQL("create table 'sequence'('id' text, 'number' integer, primary key('id'))");
            db.execSQL("alter table 'task' add column 'alarmid' integer;");
        }

        if (oldVersion < 12){
            db.execSQL("create table 'notes_item_parts' ('id' text, 'note_item_id' text, 'text' text, 'pos' integer, 'type' integer, primary key ('id'), constraint fk_notes_item_parts foreign key ('note_item_id') references 'notes_items' ('id') on delete cascade);");
        }

    }
}
