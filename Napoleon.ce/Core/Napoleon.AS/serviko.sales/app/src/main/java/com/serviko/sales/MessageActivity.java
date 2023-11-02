package com.serviko.sales;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.serviko.database.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageActivity extends BaseActivityOld {
    private Adapter adapter;
    private ListView listView;

    public static void open(Context context) {
        Intent i = new Intent(context, MessageActivity.class);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.message_activity;
    }

    @Override protected int getBottomMenuID() {
        return 0; //R.id.itMessages;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = findViewById(R.id.list);

        adapter = new Adapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter.Data data = (Adapter.Data) parent.getItemAtPosition(position);

                if (data.readed == 0){
                    data.readed = 1;

                    SQLiteDatabase db = new DBHelper(getApplicationContext()).getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("readed", 1);
                    db.update("message", cv,"rowid=?", new String[] {Long.toString(data.rowid)});

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(msgRcv, new IntentFilter(FCMListener.NEW_MSG_RECIEVED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(msgRcv);
    }

    BroadcastReceiver msgRcv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                adapter.reload();
                adapter.notifyDataSetChanged();
            }
        }
    };

    public static class Adapter extends BaseAdapter{
        Context context;

        static class Data {
            long rowid = 0;
            String text = "";
            long date = 0;
            int readed = 0;
        }

        public List<Data> data = new ArrayList<>();

        public Adapter(Context context){
            this.context = context;
            reload();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(context, R.layout.message, null);

            Data msg = (Data)getItem(position);

            TextView tv = convertView.findViewById(R.id.tvMessage);
            tv.setText(msg.text);

            if (msg.readed == 0)
                tv.setTypeface(null, Typeface.BOLD);
            else
                tv.setTypeface(null, Typeface.NORMAL);

            tv = convertView.findViewById(R.id.tvDate);
            tv.setText(new SimpleDateFormat().format(new Date(msg.date)));

            return convertView;
        }

        public void reload() {
            data.clear();
            SQLiteDatabase db = new DBHelper(context).getReadableDatabase();

            Cursor c = db.query("message", new String[]{"rowid", "text", "date", "readed"}, null, null, null, null, "date DESC");

            if (c != null){
                while (c.moveToNext()){
                    Data d = new Data();
                    d.rowid = c.getLong(c.getColumnIndex("rowid"));
                    d.text = c.getString(c.getColumnIndex("text"));
                    d.date = c.getLong(c.getColumnIndex("date"));
                    d.readed = c.getInt(c.getColumnIndex("readed"));
                    data.add(d);
                }

                c.close();
            }
        }
    }
}
