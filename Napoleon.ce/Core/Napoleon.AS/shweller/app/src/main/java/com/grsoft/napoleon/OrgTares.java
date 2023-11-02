package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgTare;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.TareImpl;
import com.grsoft.util.ExtrasConst;

import java.util.List;

public class OrgTares extends Activity {
    TareImpl doc = new TareImpl();

    public static void open(Context context, TareImpl doc) {
        Intent i = new Intent(context, OrgTares.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.org_tares);

        Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
        long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        doc.read(rid);

        OrgImpl oi = new OrgImpl();
        OrgEx oe = (OrgEx) oi.getData();
        oe.id = doc.getId();
        oi.read();
        oi.close();

        ((TextView)findViewById(R.id.tvOrg)).setText(oe.name);

        ListView lv = findViewById(R.id.lvItems);;
        Adapter a = new Adapter(oe.tare);
        lv.setAdapter(a);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            OrgTare item = (OrgTare) parent.getAdapter().getItem(position);
            ((TareImpl)doc).update(item);
            a.notifyDataSetChanged();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
    }

    class Adapter extends BaseAdapter {
        List<OrgTare> tares;
        public Adapter(List<OrgTare> tares) {
            this.tares = tares;
        }


        @Override public int getCount() { return tares.size(); }

        @Override public Object getItem(int position) {return tares.get(position);}

        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(OrgTares.this, R.layout.org_tare_row, null);
            }

            OrgTare item = (OrgTare) getItem(position);
            int color = doc.findItem(item.id) != null ? getResources().getColor(doc.getItemColor()) : Color.BLACK;

            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(item.name);
            tv.setTextColor(color);

            tv = view.findViewById(R.id.tvNumber);
            tv.setText(item.number);
            tv.setTextColor(color);

            return view;
        }
    }
}
