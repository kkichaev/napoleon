package com.ksoft.dms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ksoft.dms.database.CalendarIconHelper;
import com.ksoft.dms.database.DBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Notes extends AppCompatActivity{
    final int EXPORT_PERMISSION_REQUEST = 1;
    final int IMPORT_PERMISSION_REQUEST = 2;
    DBHelper helper;
    private List<NoteEntry> data = new ArrayList<>();
    private List<NoteEntry> filtered = new ArrayList<>();
    private Adapter adapter;
    public static int TIMER_DELAY = 500;
    Timer timer = null;

    public static class NoteEntry {
        public int color;
        String id = "";
        String title = "";
        boolean isMenu = false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes);
        MaterialToolbar mtb = ((MaterialToolbar)findViewById(R.id.topAppBar));
        mtb.setOnMenuItemClickListener(menuitem ->
        {
            if (menuitem.getItemId() == R.id.calendar){
                Intent i = new Intent(getApplicationContext(), TaskCalendarActivity.class);
                startActivity(i);
            }else if (menuitem.getItemId() == R.id.calc){
                Intent i = new Intent(getApplicationContext(), Calculator.class);
                startActivity(i);
            }else if (menuitem.getItemId() == R.id.task){
                Intent i = new Intent(getApplicationContext(), Tasks.class);
                startActivity(i);
            }else if(menuitem.getItemId() == R.id.importData){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    promptPermission(IMPORT_PERMISSION_REQUEST);
                else
                    doImport();
            }else if (menuitem.getItemId() == R.id.exportData){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    promptPermission(EXPORT_PERMISSION_REQUEST);
                else
                    doExport();
            }

            return false;
        });

        CalendarIconHelper.init(mtb);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) mtb.getMenu().findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (timer != null)
                    timer.cancel();

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timer.cancel();
                        timer = null;
                        doSearch(newText);
                    }
                }, TIMER_DELAY);

                return true;
            }
        });

        RecyclerView list = findViewById(R.id.list);
        list.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new DividerItemDecoration(list.getContext(), ((LinearLayoutManager) layoutManager).getOrientation()));

        helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"id", "title", "color"}, null, null, null, null, "created DESC");

        while (c.moveToNext()){
            NoteEntry nd = new NoteEntry();
            nd.id = c.getString(c.getColumnIndex("id"));
            nd.title = c.getString(c.getColumnIndex("title"));
            nd.color = c.getInt(c.getColumnIndex("color"));

            data.add(nd);
        }

        filtered.addAll(data);
        c.close();

        adapter = new Adapter(this);
        list.setAdapter(adapter);
        adapter.onItemClick = new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                NoteEdit.open(v.getContext(), filtered.get(pos).id);
            }

            @Override
            public void onEditClick(View v, int pos) {
                editNoteDlg(filtered.get(pos), (d,vv)->{
                    AlertDialog dlg = (AlertDialog) d;
                    String title = ((EditText)dlg.findViewById(R.id.edNote)).getText().toString();

                    if (title != null && title.trim().length() > 0) {
                        editNote(filtered.get(pos).id, title);
                        filtered.get(pos).title = title;
                        filtered.get(pos).isMenu = false;
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onDeleteClick(View v, int pos) {
                new MaterialAlertDialogBuilder(Notes.this)
                        .setTitle(R.string.alert)
                        .setMessage(R.string.delete_confirm)
                        .setNeutralButton(R.string.cancel, (d,dv)->{d.cancel();})
                        .setPositiveButton(R.string.ok, (d,dv)->{
                            SQLiteDatabase db = helper.getWritableDatabase();
                            db.delete("notes", "id=?", new String[]{filtered.get(pos).id});
                            filtered.remove(pos);
                            adapter.notifyDataSetChanged();
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onSelectColorClick(View v, int pos) {
                filtered.get(pos).isMenu = false;
                selectColorDlg(filtered.get(pos));
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchCallback());
        touchHelper.attachToRecyclerView(list);

        findViewById(R.id.addNote).setOnClickListener((v)->{addNoteDlg();});
    }

    private void doExport() {
        new ExportHelper(this).exportData();
    }

    private void doImport() {
        new ExportHelper(this).importData();
    }

    private void promptPermission(int code){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
                this.startActivityForResult(intent, code);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, }, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_PERMISSION_REQUEST || requestCode == EXPORT_PERMISSION_REQUEST)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, }, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EXPORT_PERMISSION_REQUEST){
            for(int i = 0; i < permissions.length; i++){
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    doExport();
            }
        }else if (requestCode == IMPORT_PERMISSION_REQUEST){
            for(int i = 0; i < permissions.length; i++){
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    doImport();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void doSearch(String text) {
        filtered.clear();

        if (text.trim().length() == 0)
            filtered.addAll(data);
        else
            for (NoteEntry e : data)
                if (e.title.toUpperCase().contains(text.toUpperCase()))
                    filtered.add(e);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void editNote(String id, String title) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);

        SQLiteDatabase db = helper.getWritableDatabase();
        db.update("notes", cv, "id=?", new String[]{id});
    }

    private void addNoteDlg() {
        editNoteDlg(null, (d,v)->{
            AlertDialog dlg = (AlertDialog) d;
            String title = ((EditText)dlg.findViewById(R.id.edNote)).getText().toString();

            if (title != null && title.trim().length() > 0)
                addNote(title);
        });
    }

    private void editNoteDlg(NoteEntry note, DialogInterface.OnClickListener callback){
        View view = View.inflate(this, R.layout.input_note_dlg, null);

        if (note != null)
            ((EditText)view.findViewById(R.id.edNote)).setText(note.title);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.input_folder_title)
                .setView(view)
                .setNeutralButton(R.string.cancel, (d,v)->{d.cancel();})
                .setPositiveButton(R.string.ok, callback)
                .setCancelable(false)
                .show();
    }

    private void selectColorDlg(NoteEntry note){
        ViewGroup view = (ViewGroup) View.inflate(this, R.layout.select_color_dialog, null);

        final Dialog dlg =new  MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_color)
                .setView(view)
                .create();

        for (int i = 0; i < view.getChildCount(); i++)
            view.getChildAt(i).setOnClickListener(x -> {
                Drawable background = x.getBackground();
                if (background instanceof ColorDrawable) {
                    note.color = ((ColorDrawable) background).getColor();
                    adapter.notifyDataSetChanged();
                    dlg.dismiss();

                    ContentValues cv = new ContentValues();
                    cv.put("color", note.color);

                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.update("notes", cv, "id=?", new String[]{note.id});
                }
            });

        dlg.show();
    }

    private void addNote(String title) {
        NoteEntry nd = new NoteEntry();
        nd.id =  UUID.randomUUID().toString();
        nd.title = title;

        filtered.add(0, nd);
        data.add(0, nd);

        ContentValues cv = new ContentValues();
        cv.put("id",nd.id);
        cv.put("title", nd.title);
        cv.put("created", new Date().getTime());

        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert("notes", null, cv);

        adapter.notifyDataSetChanged();
    }

    interface OnItemClickListener{
        void onItemClick(View v, int pos);
        void onEditClick(View v, int pos);
        void onDeleteClick(View v, int pos);
        void onSelectColorClick(View v, int pos);
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public Adapter(Notes notes){
            this.notes = notes;
        }
        private final int SHOW_MENU = 1;
        private final int HIDE_MENU = 2;

        public OnItemClickListener onItemClick;
        Notes notes;

        public void showMenu(int adapterPosition) {
            notes.filtered.get(adapterPosition).isMenu = !notes.filtered.get(adapterPosition).isMenu;
            notifyDataSetChanged();
        }

        public abstract class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textView;
            public String key;
            public int position;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textView = itemView.findViewById(R.id.tvTitle);
            }
        }

        public class EntryViewHolder extends ViewHolder{
            public EntryViewHolder(View v) {
                super(v);
                itemView.setOnClickListener(vv->{
                    onItemClick.onItemClick(vv, position);
                });
            }
        }

        public class MenuViewHolder extends EntryViewHolder{
            public MenuViewHolder(@NonNull View view) {
                super(view);
                view.findViewById(R.id.delete).setOnClickListener(vv->{
                    onItemClick.onDeleteClick(view, position);
                });

                view.findViewById(R.id.edit).setOnClickListener(vv->{
                    onItemClick.onEditClick(view, position);
                });

                view.findViewById(R.id.palette).setOnClickListener(vv->{
                    onItemClick.onSelectColorClick(view, position);
                });
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;

            if (viewType == SHOW_MENU) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row_menu, parent, false);
                return  new MenuViewHolder(view);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, parent, false);
                return new EntryViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (EntryViewHolder) holder;
            viewHolder.textView.setText(notes.filtered.get(position).title);
            viewHolder.key = Integer.toString(position);
            viewHolder.position = position;
            int color = notes.filtered.get(position).color;
            viewHolder.textView.setTextColor(color != 0 ? color : Color.BLACK) ;
        }

        @Override
        public int getItemCount() {
            return notes.filtered.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(notes.filtered.get(position).isMenu){
                return SHOW_MENU;
            }else{
                return HIDE_MENU;
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
            adapter.showMenu(viewHolder.getAdapterPosition());
        }
    }
}
