package com.grsoft.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.LastScript;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.VisitPreview;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.dataobjects.impl.MScriptImpl;
import com.grsoft.dataobjects.impl.NotVisitedImpl;
import com.grsoft.manager.documents.MDocType;
import com.grsoft.manager.documents.MScriptDoc;
import com.grsoft.manager.documents.MVisitDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LastScriptView extends Activity implements AdapterView.OnItemClickListener {
    private final static String USERID = "userid";

    public static void open(Context context, String userid){
        Intent i = new Intent(context, LastScriptView.class);
        i.putExtra(USERID, userid);

        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lastscript);

        ListView list = findViewById(R.id.list);
        list.setAdapter(new Adapter(this,getIntent().getStringExtra(USERID)));
        list.setOnItemClickListener(this);
    }

    public static class Param extends DataObject{
        public Date created;
        public String userid;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final LastScript ls = (LastScript) parent.getItemAtPosition(position);

        if (ls != null){
            Param param = new Param();
            param.created = ls.created;
            param.userid = ls.userid;

            List<Hitching> ret = new ArrayList<Hitching>();
            List<Hitching> res = new ArrayList<>();

            for (DocTypeBase dt : DocTypeBase.docTypes)
               res.add(((MDocType)dt).getRcvHitch());

            res.add(new Hitching(Price.class));
            res.add(new Hitching(Price.class, "ManagerPrice"));

            res.add(new Hitching(ScriptDef.class, "ScriptDef"));

            ret.add(new ReportHitching("getscriptdocs", param, res));

            UpdateProcess upp = new UpdateProcess( this, new UpdateCtrl() {

                @Override
                public void onFinish(boolean result) {
                   ScriptView.open(LastScriptView.this, ls.created.getTime(), ls.userid);
                }

                @Override
                public void updateCtrl(boolean enabled) {
                }
            }, ret);
            upp.execute((Void[]) null);
        }
    }

    static class Adapter extends BaseAdapter {
        String userid;
        List<LastScript> data = new ArrayList<>();
        Context context;
        @SuppressLint("SimpleDateFormat")
        protected static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");

        public Adapter(Context context, String userid) {
            this.context = context;
            this.userid = userid;
            load();
        }

        @SuppressWarnings("unchecked")
        public void load() {
            data.clear();

            DataTraveler.travel(LastScript.class, new DataTraveler.Travel<LastScript>(true) {
                @Override
                public boolean travel(DataTraveler<LastScript> item) {
                    data.add(item.data);
                    return true;
                }
            }, String.format("userid=\"%s\"", userid));

            Collections.sort(data, (x,y)->y.created.compareTo(x.created));
        }

        @Override
        public int getCount() { return data.size(); }

        @Override
        public Object getItem(int position) { return data.get(position); }

        @Override
        public long getItemId(int position) { return 0;	}

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            if (view == null)
                view = View.inflate(context, R.layout.lastscriptrow, null);

            LastScript dr = (LastScript) getItem(pos);
            if( dr != null )
                setView(pos, view, dr);

            int backId = pos % 2 != 0 ? R.drawable.list_selector : R.drawable.even_row_selector;

            view.setBackgroundResource(backId);

            return view;
        }

        private void setView(int pos, View view, LastScript row) {
            TextView tv = view.findViewById(R.id.tvDate);
            tv.setText(sdf.format(row.created));

            tv = view.findViewById(R.id.tvScriptName);
            tv.setText(row.scriptname);

            tv = view.findViewById(R.id.tvOrgName);
            tv.setText(row.orgname);

        }
    }
}
