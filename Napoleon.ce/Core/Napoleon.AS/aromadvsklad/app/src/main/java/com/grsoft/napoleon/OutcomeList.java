package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Outcome;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OutcomeImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.util.Util;
import com.grsoft.view.Refreshable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutcomeList extends Activity implements Refreshable {
    Adapter adapter;

    public static void open(Context context) {
        Intent i = new Intent(context, OutcomeList.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outcome_list);
        ListView lv = findViewById(R.id.lvDocs);

        adapter = new Adapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RowData rd = (RowData) adapter.getItem(i);
                OutcomeEdit.open(OutcomeList.this, rd.dlv.id, rd.dlv.number);
            }
        });
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.income_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        RowData row = (RowData) adapter.getItem(menuInfo.position);
        if(item.getItemId() == R.id.itDel) {
            if(row.outDoc != null) {
                OutcomeImpl doc = new OutcomeImpl();
                doc.getData().created = row.outDoc.created;
                doc.read();
                doc.close();
                DocDeleteHelper.delete(doc, this);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh();
    }

    @Override
    public void refreshContent() {
        adapter.refresh();
    }

    class Adapter extends BaseAdapter {
        List<RowData> data = new ArrayList<>();

        public void refresh() {
            data.clear();

            final OrgImpl org = new OrgImpl();
            final DbReader r = new DbReader();
            DataTraveler.travel(Delivery.class, new DataTraveler.Travel<Delivery>(true) {
                @Override
                public boolean travel(DataTraveler<Delivery> item) {
                    RowData rd = new RowData();
                    rd.dlv = item.data;

                    Org o = org.getData();
                    o.id = rd.dlv.id;
                    if(org.read()) {
                        rd.org = o.name;
                        rd.address = o.address;
                    } else {
                        rd.org = o.id;
                    }

                    Outcome od = new Outcome();
                    if(r.select(od, od.getTableName(), "number='" + rd.dlv.number + "' and id='" + rd.dlv.id + "'"))
                        rd.outDoc = od;

                    data.add(rd);
                    return true;
                }
            }, "", "date desc, number");
            r.close();
            org.close();
            notifyDataSetChanged();
        }

        @Override public int getCount() { return data.size(); }
        @Override public Object getItem(int i) { return data.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null)
                view = View.inflate(OutcomeList.this, R.layout.outcome_list_row, null);
            RowData item = (RowData) getItem(i);
            TextView tv = view.findViewById(R.id.tvInfo);
            tv.setText(Html.fromHtml(item.toString()));
            return view;
        }
    }
    static class RowData {
        public String org = "";
        public String address = "";
        public Delivery dlv;
        public Outcome outDoc;

        public String toString() {
            String text = "";
            text += "<b>" + org  + "</b>" + " " + Util.simpleDateFormat.format(dlv.date) + " " + dlv.number;
            if(outDoc != null) {
                text += "<br/>" + outDoc.getText();
            }
            return text;
        }
    }
}