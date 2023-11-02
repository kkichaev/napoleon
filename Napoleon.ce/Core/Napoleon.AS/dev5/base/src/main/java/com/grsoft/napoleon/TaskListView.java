package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDone;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.OrgTaskImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeSender;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.util.gps.GPSUtilNew;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskListView extends Activity implements SendResultListener {
    public static Class<? extends  Activity> activity = TaskListView.class;
    private Adapter.Data editRemark;
    private ListView list;
    private TextView tvFilter;

    public static void open(Context context){
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list_view);
        setTitle(R.string.task_list);

        tvFilter = findViewById(R.id.tvFilter);
        tvFilter.setOnClickListener((v)->{filterByOrg(null);});

        list = findViewById(R.id.list);
        list.setAdapter(new Adapter(this));
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                editRemark = ((Adapter.Data)arg0.getItemAtPosition(arg2));
                showDialog(R.id.edEditRemark);
                return true;
            }
        });

        findViewById(R.id.btnFilter).setOnClickListener((v)->showDialog(R.id.select_org_dlg));
        findViewById(R.id.btnSend).setOnClickListener((v)->send());
    }

    private void send() {
        new DocTypeSender(this, findViewById(R.id.btnSend), TaskDoneDoc.instance()).execute((Void[])null);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == R.id.edEditRemark)
            prepareEditRemarkDlg(dialog);
        else
            super.onPrepareDialog(id, dialog);
    }

    private void prepareEditRemarkDlg(Dialog dialog) {
        EditText ed = dialog.findViewById(R.id.edRemark);

        if (ed != null)
            ed.setText(editRemark.remark);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if( id == R.id.edEditRemark) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(View.inflate(this, R.layout.input_remark, null));
            builder.setTitle(R.string.message);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveTaskRemark(((EditText) ((AlertDialog) dialog).findViewById(R.id.edRemark)).getText().toString());
                }
            });

            builder.setNegativeButton(R.string.cancel, null);
            return builder.create();
        }else if (id == R.id.select_org_dlg){
            return OrgSelectDialog.create(this, (o)->{
                filterByOrg(o);
            });
        }

        return super.onCreateDialog(id);
    }

    private void filterByOrg(Org org) {
        ((Adapter)list.getAdapter()).load(org == null ? "" : org.id);
        ((Adapter)list.getAdapter()).notifyDataSetChanged();
        tvFilter.setText(org == null ? "" : org.name);
    }

    protected void saveTaskRemark(String remark) {
        long exec = editRemark.exec;
        OrgTaskExecImpl doc = new OrgTaskExecImpl();

        if( exec == 0 )
            initNewDoc(editRemark, doc);
        else
            doc.read(exec, false);

        if( DocumentUtils.isExported(doc.getData().params) == false ) {
            doc.getData().remark = remark;
            if(doc.getData().items.size() > 0)
                doc.getData().items.get(0).text = remark;

            editRemark.exec = doc.getData().created.getTime();
            editRemark.remark = remark;

            doc.write();
            ((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
        }

        doc.close();
    }

    protected void checkTask(CheckBox cb) {
        Adapter.Data d = (Adapter.Data) cb.getTag();
        OrgTaskExecImpl doc = new OrgTaskExecImpl();

        if( cb.isChecked() ) {
            if(d.exec == 0)
                initNewDoc(d, doc);
        } else {
            if( d.exec > 0 && DocumentUtils.isExported(doc.getData().params) == false ) {
                d.exec = 0;

                doc.read(d.exec, false);
                doc.close();

                ((BaseAdapter)list.getAdapter()).notifyDataSetChanged();

                try {
                    String sql = "DELETE FROM " + doc.getTableName() + " WHERE created = " + Long.toString(doc.getData().created.getTime());
                    DataBaseManager.getDataBase().execSQL(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                cb.setChecked(true);
                Toast.makeText(this, R.string.task_sended, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initNewDoc(Adapter.Data d, OrgTaskExecImpl doc) {
        OrgTaskImpl orgTask = new OrgTaskImpl();
        orgTask.read("id", d.taskid);
        orgTask.close();
        doc.init(this, orgTask.getData(), GPSUtilNew.getLastKnownLocation(this));
    }

    @Override
    public void postSendExecute(boolean result) {
        filterByOrg(null);
    }

    private static class Adapter extends BaseAdapter{
        private Context context;
        private OrgTaskExecImpl execImpl = new OrgTaskExecImpl();

        private static class Data{
            public long start;
            public long finish;
            public String taskid = "";
            public String text = "";
            public String id = "";
            public long created;
            public String name = "";
            public long exec;
            public String remark = "";
        }

        private List<Data> data = new ArrayList();

        public Adapter(Context context){
            this.context = context;
            load("");
        }

        public void load(String id){
            data.clear();
            SQLiteDatabase db = DataBaseManager.getDataBase();
            Cursor c = null;

            try{
                DbWriter.checkDBTable(TaskDone.class);

                String andid = "";

                if (id.length() > 0)
                    andid = String.format(" and t.orgid = '%s' ", id);

                String sql = "select " +
                                "t.created as created, " +
                                "t.start as start, " +
                                "t.finish as finish, " +
                                "t.id as taskid, " +
                                "t.text as text, " +
                                "t.orgid as id, " +
                                "e.created as exec, " +
                                "e.remark as remark, " +
                                "o.name as name " +
                                "from agentOrgTask t left join orgTaskExec e on t.id = e.idTask " +
                                "left join org o on t.orgid = o.id " +
                                "where e.created is null " +
                                andid +
                                " order by t.created desc ";

                c = db.rawQuery(sql, null);

                while (c.moveToNext()){
                    Data d = new Data();

                    d.created = c.getLong(c.getColumnIndex("created"));
                    d.start = c.getLong(c.getColumnIndex("start"));
                    d.finish = c.getLong(c.getColumnIndex("finish"));
                    d.taskid = c.getString(c.getColumnIndex("taskid"));
                    d.text = c.getString(c.getColumnIndex("text"));
                    d.id = c.getString(c.getColumnIndex("id"));
                    d.exec = c.getLong(c.getColumnIndex("exec"));
                    d.name = c.getString(c.getColumnIndex("name"));
                    d.remark = c.getString(c.getColumnIndex("remark"));

                    data.add(d);
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                if (c != null)
                    c.close();
            }
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
        public View getView(int position, View view, ViewGroup parent) {
            if( view == null )
                view = View.inflate(context, R.layout.org_task_row2, null);

            Data d = (Data) getItem(position);
            TextView tv = view.findViewById(R.id.tvOrg);
            tv.setText(d.name);

            tv = view.findViewById(R.id.tvTask);
            tv.setText(d.text);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String text = sdf.format(new Date(d.start)) + " - " + sdf.format(new Date(d.finish));
            tv = (TextView)view.findViewById(R.id.tvDate);
            tv.setText(text);

            CheckBox cb = view.findViewById(R.id.cbTaskDone);
            cb.setChecked(d.exec > 0);
            cb.setTag(d);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { ((TaskListView)context).checkTask((CheckBox)v); }
            });

            tv = view.findViewById(R.id.tvRemark);
            tv.setText(d.remark);

            return view;
        }
    }
}
