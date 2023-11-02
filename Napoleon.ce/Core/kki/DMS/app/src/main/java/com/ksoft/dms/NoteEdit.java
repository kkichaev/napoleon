package com.ksoft.dms;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ksoft.dms.database.DBHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NoteEdit extends AppCompatActivity {
    public static final String UPDATE_ACTION_ROW = "update_action_row";

    private static final int RECORD_RESULT = 0;
    private static final int PERMISSION_REQUEST = 1;
    private String noteID = "";
    private static final String NOTE_ID = "note_id";
    private SpeechRecognizer speech;
    private LinearLayout layout;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM hh:mm");;

    private Adapter adapter;

    public static void open(Context context, String id){
        Intent i = new Intent(context, NoteEdit.class);
        i.putExtra(NOTE_ID, id);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_edit);
        layout = findViewById(R.id.container);

        noteID = getIntent().getStringExtra(NOTE_ID);

        ((MaterialToolbar)findViewById(R.id.topAppBar)).setOnMenuItemClickListener(menuitem ->
        {
            if (menuitem.getItemId() == R.id.mic){
                checkAudioPermission();
            }

            if (menuitem.getItemId() == R.id.create){
                Long created = new Date().getTime();
                String itemid = UUID.randomUUID().toString();
                ContentValues cv = new ContentValues();
                cv.put("id", itemid);
                cv.put("noteid", noteID);
                cv.put("note", "");
                cv.put("created", created);

                SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
                db.insert("notes_items", null, cv);

                Data d = new Data();
                d.created = created;
                d.key = itemid;
                d.note = "";

                adapter.add(d, 0);

                Intent i  = new Intent(this, NoteItemEdit.class);
                i.putExtra(NoteItemEdit.ITEM_ID, itemid);
                startActivity(i);
            }

            return false;
        });

        adapter = new Adapter(this);
        adapter.onItemClick = new Notes.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(NoteEdit.this, NoteItemEdit.class);
                intent.putExtra(NoteItemEdit.ITEM_ID, adapter.data.get(pos).key);
                startActivity(intent);

                String transitionName = "transitionName";

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(NoteEdit.this,
                                v.findViewById(R.id.text),
                                transitionName
                        );
                ActivityCompat.startActivity(NoteEdit.this, intent, options.toBundle());
            }

            @Override
            public void onEditClick(View v, int pos) {

            }

            @Override
            public void onDeleteClick(View v, int pos) {

            }

            @Override
            public void onSelectColorClick(View v, int pos) {

            }
        };

        RecyclerView list = findViewById(R.id.list);
        list.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new NoteEdit.TouchCallback());
        touchHelper.attachToRecyclerView(list);

        registerReceiver(updateRcv, new IntentFilter(UPDATE_ACTION_ROW));
    }

    private void createCard(String id, String title, String path) {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.play_record_view, null);

        TextView tv = view.findViewById(R.id.title);
        tv.setText(title);

        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            FileInputStream fis = new FileInputStream(new File(path));
            player.setDataSource(fis.getFD());
            player.prepare();

            View v = view.findViewById(R.id.play);
            v.setTag(path);
            v.setOnClickListener(x->{
                try {
                    player.start();

                    Handler prgHandler = new Handler();
                    Runnable update = new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar progress = view.findViewById(R.id.progress);

                            if (progress.getMax() <= player.getCurrentPosition())
                                prgHandler.removeCallbacks(this);
                            else {
                                progress.setProgress(player.getCurrentPosition());
                                prgHandler.postDelayed(this, 0);
                            }
                        }
                    };

                    prgHandler.postDelayed(update, 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            ProgressBar progress = view.findViewById(R.id.progress);
            progress.setMax(player.getDuration());

            v = view.findViewById(R.id.delete);
            v.setOnClickListener(x->{
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.alert)
                        .setMessage(R.string.delete_confirm)
                        .setNeutralButton(R.string.cancel, (d,dv)->{d.cancel();})
                        .setPositiveButton(R.string.ok, (d,dv)->{
                            layout.removeView(view);

                            SQLiteDatabase db = new DBHelper(getApplicationContext()).getWritableDatabase();
                            db.delete("record", "id=?", new String[]{id});
                        })
                        .setCancelable(false)
                        .show();
            });

            v = view.findViewById(R.id.share);
            v.setOnClickListener(x->{
                Intent intent = new Intent(Intent.ACTION_SEND);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", new File(path));
                intent.setType(URLConnection.guessContentTypeFromName(new File(path).getName()));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        int m = (int) getResources().getDimension(R.dimen.card_margins);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(m,m,m,m);
        layout.addView(view, params);
    }

    private void checkAudioPermission(){
        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            else
                promtRecord();
        }
    }

    private void promtRecord() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);
        startActivityForResult(intent, RECORD_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RECORD_RESULT && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
            Uri audioUri = data.getData();
            ContentResolver contentResolver = getContentResolver();
            try {
                BufferedInputStream in = new BufferedInputStream(contentResolver.openInputStream(audioUri));
                File dir = new File(Environment.getExternalStorageDirectory(), "DMSShare");

                if (!dir.exists())
                    dir.mkdirs();

                File file = new File(dir, String.format("%s.amr",new Date().toString()));
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len = 0;

                while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                }

                in.close();
                out.flush();
                out.close();

                String id = UUID.randomUUID().toString();
                ContentValues cv = new ContentValues();
                cv.put("id", id);
                cv.put("noteid", noteID);
                cv.put("file", file.getAbsolutePath());

                SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
                db.insert("record", null, cv);

                createCard(id, file.getName(), file.getAbsolutePath());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
        if(rc == PERMISSION_REQUEST) {
            for (int i = 0; i < result.length; i++)
                if (result[i] != PackageManager.PERMISSION_GRANTED && permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
                    return;
                }else if (result[i] != PackageManager.PERMISSION_GRANTED && permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                    return;
                }

            promtRecord();
        }
    }

    BroadcastReceiver updateRcv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String item_id = intent.getStringExtra(NoteItemEdit.ITEM_ID);
            SQLiteDatabase db = new DBHelper(getApplicationContext()).getReadableDatabase();
            Cursor c = db.query("notes_items", new String[]{"note", "created"}, "id=?",
                    new String[]{item_id}, null, null, null);

            if (c.moveToFirst()) {
                adapter.map.get(item_id).note = c.getString(c.getColumnIndex("note"));
                adapter.notifyDataSetChanged();
            }

            c.close();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing())
            unregisterReceiver(updateRcv);
    }

    public static class Data{
        long created;
        String key;
        String note;
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.VH>{
        private List<Data> data = new ArrayList<>();
        private Map<String, Data> map = new HashMap<>();
        public Notes.OnItemClickListener onItemClick;

        public void removeItem(int pos) {
            map.remove(data.get(pos).key);
            data.remove(pos);
        }

        public void add(Data data){
            this.data.add(data);
            this.map.put(data.key, data);
        }

        public void add(Data data, int pos){
            this.data.add(pos, data);
            this.map.put(data.key, data);
        }

        public Adapter(Context context){
            Cursor c = new DBHelper(context).getReadableDatabase().query("notes_items",
                    new String[]{"id", "note", "created"}, "noteid=?", new String[]{noteID}, null, null, "created desc");
            while (c.moveToNext()) {
                Long created = c.getLong(c.getColumnIndex("created"));

                Data d = new Data();
                d.created = created;
                d.key = c.getString(c.getColumnIndex("id"));
                d.note = c.getString(c.getColumnIndex("note"));

                add(d);
            }

            c.close();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_note_view,parent,false);
            return new VH(view);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Data d = data.get(position);
            holder.title.setText(simpleDateFormat.format(new Date(d.created)));
            holder.text.setText(d.note);
            holder.pos = position;
        }

        public class VH extends RecyclerView.ViewHolder{
            public TextView title;
            public TextView text;
            public int pos;

            public VH(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                text = itemView.findViewById(R.id.text);

                itemView.setOnClickListener(x->{
                    onItemClick.onItemClick(itemView, pos);
                });
            }
        }
    }

    class TouchCallback extends ItemTouchHelper.SimpleCallback{
        public TouchCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            new MaterialAlertDialogBuilder(NoteEdit.this)
                .setTitle(R.string.alert)
                .setMessage(R.string.delete_confirm)
                .setNeutralButton(R.string.cancel, (d,dv)->{
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                })
                .setPositiveButton(R.string.ok, (d,dv)->{
                    SQLiteDatabase db = new DBHelper(NoteEdit.this).getWritableDatabase();
                    Data data = adapter.data.get(viewHolder.getAdapterPosition());

                    db.delete("notes_items", "id=?", new String[]{data.key});

                    adapter.removeItem(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                })
                .setCancelable(false)
                .show();
        }
    }
}
