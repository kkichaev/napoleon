package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PlanogramDoc;
import com.grsoft.dataobjects.Planograms;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PlanogramDocImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlanogramEdit extends Activity implements SendResultListener {
    PlanogramDocImpl doc = new PlanogramDocImpl();


    public static void open(Context context, PlanogramDocImpl doc) {
        Intent i = new Intent(context, PlanogramEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planogram_edit);

        Bundle b = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));

        OrgImpl oi = new OrgImpl();
        Org o = oi.getData();
        o.id = doc.getId();
        TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
        tvOrg.setText(o.name);

        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(new Adapter());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Planograms item = (Planograms) parent.getAdapter().getItem(position);
                preview(new String(item.photo));
                if(doc.isEditable()) {
                    PlanogramDoc pd = doc.getData();
                    pd.planogram = item.id;
                    pd.planogramTitle = item.name;
                    doc.write();
                    com.grsoft.napoleon.documents.PlanogramDoc.instance().refreshDocSum(doc.getId());
                    ((Adapter)parent.getAdapter()).notifyDataSetChanged();
                }
            }
        });

        View v = findViewById(R.id.btnSend);
        if( Features.CANT_SEND_SCRIPT_PART ) {
            if(ScriptImpl.containsDocument(com.grsoft.napoleon.documents.PlanogramDoc.instance().getObjectName(), doc.getData().created, doc.getId()) != null)
                v.setVisibility(View.GONE);
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { send(); }
        });
    }

    private void preview(String path) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);

        Uri uri = null;

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this,getString(R.string.fileprovider_authorities), new File(path));
        }else
            uri = Uri.parse("file://" + path);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setDataAndType(uri, "image/*");

        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        if(doc.isEmpty()) {
            doc.delete();
        }
        super.onBackPressed();
    }

    void send() {
        if(doc.isEmpty()) {
            return;
        }

        new DocumentSender(this, findViewById(R.id.btnSend),
                com.grsoft.napoleon.documents.PlanogramDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());;
    }

    @Override
    public void postSendExecute(boolean result) {
        if(result) {
            doc.read(doc.getRowid(), false);
        }
    }

    class Adapter extends BaseAdapter {

        public List<Planograms> data = new ArrayList<>();

        public Adapter() {
            DataTraveler.travel(Planograms.class, new DataTraveler.Travel<Planograms>(true) {
                @Override
                public boolean travel(DataTraveler<Planograms> item) {
                    data.add(item.data);
                    return true;
                }
            }, "", "name");
        }

        @Override public int getCount() { return data.size(); }
        @Override public Object getItem(int position) { return data.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(PlanogramEdit.this, R.layout.planogram_edit_row, null);
            }
            Planograms item = (Planograms) getItem(position);
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(item.name);
            tv.setTypeface(item.name.equals(doc.getData().planogramTitle) ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

            view.setBackgroundResource((position % 2) == 0 ? R.drawable.list_selector : R.drawable.even_row_selector);
            return view;
        }
    }
}
