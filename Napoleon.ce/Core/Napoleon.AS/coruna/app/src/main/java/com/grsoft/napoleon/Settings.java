package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings extends AppCompatActivity {

    MediaPlayer mediaPlayer = null;
    public static void open(Context context) {
        Intent i = new Intent(context, Settings.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        List<RingToneData> values = getNotifications();

        ListView lv = findViewById(R.id.good_sound);
        Adapter a = new Adapter(values, Config.goodUri);
        lv.setAdapter(a);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            a.setSelected(((RingToneData)a.getItem(position)).uri);
        });

        lv = findViewById(R.id.bad_sound);
        Adapter a2 = new Adapter(values, Config.badUri);
        lv.setAdapter(a2);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            a2.setSelected(((RingToneData)a.getItem(position)).uri);
        });
    }

    private void playSound(String uri) {
        if(uri.length() > 0) {
            Uri u = Uri.parse(uri);
            if(u != null) {
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                }

                mediaPlayer = MediaPlayer.create(this, u);
                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer = null;
                    mp.reset();
                    mp.stop();
                });
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public List<RingToneData> getNotifications() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();

        List<RingToneData> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            list.add(new RingToneData(notificationUri,notificationTitle));
        }

        return list;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ListView lv = findViewById(R.id.good_sound);
        Config.goodUri = ((Adapter)lv.getAdapter()).getSelected();

        lv = findViewById(R.id.bad_sound);
        Config.badUri = ((Adapter)lv.getAdapter()).getSelected();

        Config.save(this);
    }

    class Adapter extends BaseAdapter {
        List<RingToneData> data;
        String selected;
        public Adapter(List<RingToneData> data, String selected) {
            this.data = data;
            this.selected = selected;
        }

        public String getSelected() { return selected; }

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
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(Settings.this, R.layout.config_row, null);
            }

            RingToneData item = (RingToneData) getItem(position);
            TextView tv = view.findViewById(R.id.sound_uri);
            tv.setText(item.title);

            ImageView iv = view.findViewById(R.id.uri_select);
            iv.setImageResource(item.uri.equals(selected) ? R.drawable.ic_checked : R.drawable.ic_ellipse);
            return view;
        }

        public void setSelected(String uri) {
            selected = uri;
            notifyDataSetChanged();
            playSound(uri);
        }
    }

    static class RingToneData {
        public String uri;
        public String title;

        public RingToneData(String u, String t) {
            uri = u;
            title = t;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
