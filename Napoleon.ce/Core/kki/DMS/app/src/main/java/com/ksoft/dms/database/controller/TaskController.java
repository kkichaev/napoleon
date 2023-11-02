package com.ksoft.dms.database.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ksoft.dms.database.DBHelper;
import com.ksoft.dms.database.entity.Task;
import com.ksoft.dms.database.entity.TaskItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskController {
    Context context;

    public TaskController(Context context) {
        this.context = context;
    }

    public Task read(String id) {
        Task task = null;
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        Cursor c = db.query(getTaskTable(), getTaskProjection(), "id=?", new String[]{id}, null, null, null);

        if (c.moveToFirst()) {
            task = createTask(c);
            c.close();
            c = db.query(getTaskItemsTable(), getTaskItemsProjection(), "taskid=?", new String[]{task.id}, null, null, "pos");

            while (c.moveToNext()) {
                TaskItem item = createTaskItem(c);
                task.items.add(item);
            }
            c.close();
        }
        return task;
    }

    private TaskItem createTaskItem(Cursor c) {
        TaskItem item = new TaskItem();
        item.id = c.getString(c.getColumnIndex("id"));
        item.text = c.getString(c.getColumnIndex("text"));
        item.status = c.getInt(c.getColumnIndex("status"));
        item.pos = c.getInt(c.getColumnIndex("pos"));
        return item;
    }

    private Task createTask(Cursor c) {
        Task task;
        task = new Task();
        task.id = c.getString(c.getColumnIndex("id"));
        task.created = c.getLong(c.getColumnIndex("created"));
        task.date = c.getLong(c.getColumnIndex("date"));
        task.schedule = c.getString(c.getColumnIndex("schedule"));
        task.text = c.getString(c.getColumnIndex("text"));
        task.status = c.getInt(c.getColumnIndex("status"));
        task.finish = c.getLong(c.getColumnIndex("finish"));
        task.alarmid = c.getInt(c.getColumnIndex("alarmid"));
        task.items = new ArrayList<>();
        return task;
    }

    public List<Task> readList(Date date){
        List<Task> result = new ArrayList<>();

        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date finish = cal.getTime();
        Cursor c = db.query(getTaskTable(), getTaskProjection(),
                "(date >= ? and date < ?) or date = 0",
                new String[]{Long.toString(start.getTime()), Long.toString(finish.getTime())},
                null, null, "status, date , created desc");

        while (c.moveToNext()){
            Task task = createTask(c);
            result.add(task);

            Cursor c2 = db.query(getTaskItemsTable(), getTaskItemsProjection(), "taskid=?", new String[]{task.id}, null, null, "pos");

            while(c2.moveToNext()) {
                TaskItem item = createTaskItem(c2);
                task.items.add(item);
            }

            c2.close();
        }

        c.close();
        return result;
    }

    private String[] getTaskItemsProjection() {
        return new String[]{"id", "text", "status", "pos"};
    }

    private String getTaskItemsTable() {
        return "task_items";
    }

    private String getTaskTable() {
        return "task";
    }

    private String[] getTaskProjection() {
        return new String[]{"id", "created", "date", "schedule", "text", "status", "finish", "alarmid"};
    }

    public void insert(Task task){
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", task.id);
            cv.put("created", new Date().getTime());
            cv.put("date", task.date);
            cv.put("schedule", task.schedule);
            cv.put("text", task.text);
            cv.put("status", 0);
            cv.put("alarmid", task.alarmid);

            db.insert("task", null, cv);

            for(TaskItem i : task.items){
                cv = new ContentValues();
                cv.put("id", i.id);
                cv.put("taskid", task.id);
                cv.put("text", i.text);
                cv.put("status", 0);
                cv.put("pos", i.pos);

                db.insert("task_items", null, cv);
            }

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    public void update(Task task){
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", task.id);
            cv.put("created", new Date().getTime());
            cv.put("date", task.date);
            cv.put("schedule", task.schedule);
            cv.put("text", task.text);
            cv.put("status", task.status);
            cv.put("finish", task.finish);
            cv.put("alarmid", task.alarmid);

            db.update("task", cv, "id=?", new String[]{task.id});
            db.delete("task_items", "taskid=?", new String[]{task.id});

            for(TaskItem i : task.items){
                cv = new ContentValues();
                cv.put("id", i.id);
                cv.put("taskid", task.id);
                cv.put("text", i.text);
                cv.put("status", i.status);
                cv.put("pos", i.pos);

                db.insert("task_items", null, cv);
            }

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    public void delete(Task task){
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.delete(getTaskTable(), "id=?", new String[]{task.id});
    }

    public void update(TaskItem item) {
        ContentValues cv = new ContentValues();
        cv.put("text", item.text);
        cv.put("status", item.status);
        cv.put("pos", item.pos);

        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.update(getTaskItemsTable(), cv, "id=?", new String[]{item.id});
    }

    public int getTaskCount(Date date){
        int res = 0;
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date finish = cal.getTime();
        Cursor c = db.rawQuery("select count(*) from task where date >= ? and date < ?", new String[]{Long.toString(start.getTime()), Long.toString(finish.getTime())});

        if (c.moveToFirst())
            res = c.getInt(0);

        c.close();

        return res;
    }
}
