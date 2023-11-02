package com.ksoft.dms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ksoft.dms.database.controller.IDNumberController;
import com.ksoft.dms.database.controller.TaskController;
import com.ksoft.dms.database.entity.Task;
import com.ksoft.dms.database.entity.TaskItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskEdit extends AppCompatActivity {
    public static final String ITEM_ID = "taskid";
    List<TaskItem> items = new ArrayList<>();
    Adapter adapter;
    RecyclerView list;
    long exec = 0;
    String schedule = "";
    EditText edTask;
    Button btnDate;
    Task task;
    TaskController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_edit);

        list = findViewById(R.id.list);
        edTask = findViewById(R.id.edTask);
        btnDate = findViewById(R.id.btnDate);

        task = (Task) getIntent().getSerializableExtra(Tasks.TASK);
        controller = new TaskController(this);

        TextView tv = findViewById(R.id.tvTaskList);
        tv.setOnTouchListener((v, e)->{
                    if (e.getAction() == MotionEvent.ACTION_DOWN)
                        ((TextView)v).setTypeface(Typeface.DEFAULT_BOLD);
                    else
                        ((TextView)v).setTypeface(Typeface.DEFAULT);

                    return false;
                });

        tv.setOnClickListener((v)->{
            ((TextView)v).setTypeface(Typeface.DEFAULT);
            editTaskListItem(null);
        });

        btnDate.setOnClickListener((v)->{
            AlarmDlg dlg = new SetTaskDateDlg();
            dlg.setIAlarmDlg((d)->{
                exec = dlg.dateNotify.getTime();
                updateBtnExec();
            });
            dlg.show(getSupportFragmentManager(), dlg.getTag());
        });

        MaterialToolbar mtb = ((MaterialToolbar)findViewById(R.id.topAppBar));
        mtb.setOnMenuItemClickListener(menuitem ->
        {
            if (menuitem.getItemId() == R.id.create)
                editTaskListItem(null);

            return false;
        });

        adapter = new Adapter(this);
        adapter.adapterAction = new Adapter.AdapterAction() {
            @Override
            public void onItemClick(Adapter.ViewHolder v) {
                editTaskListItem(items.get(v.position));
            }

            @Override
            public void remove(Adapter.ViewHolder v) {
                items.remove(v.position);
                adapter.notifyDataSetChanged();
            }
        };

        list.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        if (task != null){
            edTask.setText(task.text);
            items = task.items;
            exec = task.date;
            updateBtnExec();
        }
    }

    private void updateBtnExec() {
        String text = "...";
        if (exec != 0)
            text = new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(exec));

        btnDate.setText(text);
    }

    private void editTaskListItem(TaskItem item){
        View view = View.inflate(this, R.layout.input_task_item_dlg, null);
        final EditText ed = view.findViewById(R.id.edText);

        if (item != null)
            ed.setText(item.text);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.input_task_item_title)
                .setView(view)
                .setNeutralButton(R.string.cancel, (d,v)->{d.cancel();})
                .setPositiveButton(R.string.ok, (d,v)->{
                    String text = ed.getText().toString().trim();

                    if (text.length() > 0) {
                        if (item == null) {
                            TaskItem i = new TaskItem();
                            i.id = UUID.randomUUID().toString();
                            i.pos = items.size();
                            i.text = text;
                            items.add(i);
                        }else
                            item.text = text;

                        adapter.notifyDataSetChanged();
                    }

                    d.cancel();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String text = edTask.getText().toString().trim();

        if (isFinishing()){
            if (items.size() > 0 || text.length() > 0){
                if (task == null){
                    task = new Task();
                    task.id = UUID.randomUUID().toString();
                    task.created = new Date().getTime();
                    task.status = 0;
                    task.text = text;
                    task.date = exec;
                    task.items = items;
                    task.alarmid = new IDNumberController().generateID(this);
                    controller.insert(task);
                }else {
                    task.text = text;
                    task.date = exec;

                    if (task.alarmid == 0){
                        task.alarmid = new IDNumberController().generateID(this);
                    }

                    controller.update(task);
                }

                if (exec != 0){
                    Intent intent = new Intent(this, AlarmTaskReciever.class);
                    intent.putExtra(TaskEdit.ITEM_ID, task.id);
                    PendingIntent oper = PendingIntent.getBroadcast(this, task.alarmid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, exec, oper);
                }
            }
        }
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final TaskEdit taskEdit;
        public AdapterAction adapterAction;

        interface AdapterAction{
            void onItemClick(ViewHolder v);
            void remove(ViewHolder v);
        }

        public Adapter(TaskEdit taskEdit){
            this.taskEdit = taskEdit;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView tvTitle;
            public TextView tvPos;
            public String key;
            public int position;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPos = itemView.findViewById(R.id.tvPos);

                itemView.setOnClickListener((v)-> {
                    if (adapterAction != null)
                        adapterAction.onItemClick(this);
                });

                itemView.findViewById(R.id.delete).setOnClickListener((v)->{
                    if (adapterAction != null)
                        adapterAction.remove(this);
                });
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TaskItem i = taskEdit.items.get(position);
            viewHolder.tvTitle.setText(i.text);
            viewHolder.tvPos.setText(Integer.toString(position + 1) + ".");
            viewHolder.key = i.id;
            viewHolder.position = position;
        }

        @Override
        public int getItemCount() {
            return taskEdit.items.size();
        }
    }

}
