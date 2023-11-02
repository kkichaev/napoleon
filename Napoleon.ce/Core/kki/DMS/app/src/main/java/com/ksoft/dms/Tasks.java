package com.ksoft.dms;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ksoft.dms.database.CalendarIconHelper;
import com.ksoft.dms.database.controller.TaskController;
import com.ksoft.dms.database.entity.Task;
import com.ksoft.dms.database.entity.TaskItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tasks extends AppCompatActivity {
    public static final String TASK = "task";
    public static final String DATA = "data";
    RecyclerView list;
    private Adapter adapter;
    TaskController controller;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    Date date = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks);

        list = findViewById(R.id.list);
        findViewById(R.id.addTask).setOnClickListener((v)->{addTaskDlg();});

        date = (Date) getIntent().getSerializableExtra(DATA);

        if (date == null)
            date = Calendar.getInstance().getTime();

        MaterialToolbar mtb = findViewById(R.id.topAppBar);
        mtb.setTitle("Задачи на: " + sdf.format(date));

        mtb.setOnMenuItemClickListener(menuitem ->
        {
            if (menuitem.getItemId() == R.id.calendar){
                Intent i = new Intent(getApplicationContext(), TaskCalendarActivity.class);
                startActivity(i);
            }

            return  false;
        });

        CalendarIconHelper.init(mtb);

        controller = new TaskController(this);
        adapter = new Adapter(this);
        adapter.action = new Adapter.AdapterAction() {
            @Override
            public void edit(Adapter.ViewHolder v) {
                Intent i = new Intent(getApplicationContext(), TaskEdit.class);
                Task task = adapter.data.get(v.position);
                i.putExtra(TASK, task);
                startActivity(i);
            }

            @Override
            public void ok(Adapter.ViewHolder v) {
                Task task = adapter.data.get(v.position);

                if (task.status == Task.NOT_SET) {
                    task.status = Task.OK;

                    for(TaskItem i : task.items)
                        i.status = Task.OK;

                    task.finish = Calendar.getInstance().getTimeInMillis();

                }else {
                    task.status = Task.NOT_SET;

                    for (TaskItem i : task.items)
                        i.status = Task.NOT_SET;

                    task.finish = 0;
                }

                task.finish = Calendar.getInstance().getTimeInMillis();
                controller.update(task);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void delete(Adapter.ViewHolder h) {
                new MaterialAlertDialogBuilder(Tasks.this)
                        .setTitle("Внимание")
                        .setMessage("Задача будет удалена, удалить?")
                        .setNeutralButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, (d,v)->{
                            Task task = adapter.data.get(h.position);
                            controller.delete(task);

                            adapter.data.remove(h.position);
                            adapter.notifyDataSetChanged();
                            d.cancel();
                        })
                        .setCancelable(false)
                        .show();
            }
        };

        list.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
    }

    private void addTaskDlg() {
        Intent i = new Intent(this, TaskEdit.class);
        startActivity(i);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        date = (Date) intent.getSerializableExtra(DATA);

        if (date == null)
            date = Calendar.getInstance().getTime();

        MaterialToolbar mtb = findViewById(R.id.topAppBar);
        mtb.setTitle("Задачи на: " + sdf.format(date));
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.reload(this);
    }

    static class Adapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public AdapterAction action;
        private Tasks tasks;

        interface  AdapterAction{
            void edit(ViewHolder v);
            void ok(ViewHolder v);
            void delete(ViewHolder v);
        }

        List<Task> data = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{

            public TextView tvText;
            public String key;
            public int position;
            public ViewGroup container;
            public TextView created;
            public View view;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                view = itemView;
                tvText = itemView.findViewById(R.id.tvText);
                container = itemView.findViewById(R.id.container);
                created = itemView.findViewById(R.id.created);

                if (action != null) {
                    itemView.findViewById(R.id.edit).setOnClickListener((v)->{
                        action.edit(this);
                    });

                    itemView.findViewById(R.id.ok).setOnClickListener((v)->{
                        action.ok(this);
                    });

                    itemView.findViewById(R.id.delete).setOnClickListener((v)->{
                        action.delete(this);
                    });
                }
            }
        }

        public Adapter(Tasks tasks){
            this.tasks = tasks;
        }

        public void reload(Tasks form) {
            data.clear();
            data.addAll(form.controller.readList(form.date));
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder h = (ViewHolder) holder;
            Task task = data.get(position);
            h.tvText.setText(task.text);
            h.created.setText(createText(task.date));
            h.container.removeAllViews();
            h.position = position;

            for(TaskItem item : task.items){
                CheckBox cb = new CheckBox(tasks);
                cb.setChecked(item.status == Task.OK);
                cb.setOnCheckedChangeListener((v,c)->{
                    item.status = c ? Task.OK : 0;
                    tasks.setItemStatus(task,item);
                    notifyDataSetChanged();
                });
                cb.setText(item.text);

                if (item.status == Task.OK)
                    cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    cb.setPaintFlags(0);

                h.container.addView(cb);
            }

            if (task.status == Task.OK)
                h.view.setBackgroundColor(tasks.getColor(R.color.green));
            else
                h.view.setBackgroundColor(tasks.getColor(R.color.common_bkg));
        }

        private String createText(long exec) {
            String res = "";

            if (exec > 0){
                Calendar c = Calendar.getInstance();
                int d1 = c.get(Calendar.DAY_OF_YEAR);
                c.setTimeInMillis(exec);
                int d2 = c.get(Calendar.DAY_OF_YEAR);

                long days = d2 - d1;

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yy HH:mm");
                switch ((int)days){
                    case -1:
                        res = "вчера " + sdf.format(new Date(exec));
                        break;
                    case 0:
                        res = sdf.format(new Date(exec));
                        break;
                    case 1:
                        res = "завтра " + sdf.format(new Date(exec));
                        break;
                    case 2:
                        res = "послезавтра " + sdf.format(new Date(exec));
                        break;
                    default:
                        res = sdf2.format(new Date(exec));
                        break;
                }
            }

            return res;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private void setItemStatus(Task task, TaskItem i) {
        controller.update(i);

        boolean ready = true;

        for(TaskItem item : task.items)
            if (item.status == 0){
                ready = false;
                break;
            }

        task.status = ready ? Task.OK : Task.NOT_SET;
        task.finish = ready ? Calendar.getInstance().getTimeInMillis() : 0;

        controller.update(task);
    }
}
