package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.CheckInvoice;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Income;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.IncomeImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.IncomeDoc;
import com.grsoft.util.Util;
import com.grsoft.view.Refreshable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeList extends Activity implements Refreshable {
    Adapter adapter;

    public static void open(Context context) {
        Intent i = new Intent(context, IncomeList.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.income_list);

        ListView lv = findViewById(R.id.lvDocs);
        adapter = new Adapter(this);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item doc = (Item) adapter.getItem(i);
                IncomeImpl ii = new IncomeImpl();
                if(doc.dst != null) {
                    ii.getData().created = doc.dst.created;
                    ii.read();
                } else {
                    ii.initFrom(IncomeList.this, doc.src);
                }
                ii.close();
                ii.open(IncomeList.this);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.income_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Item doc = (Item) adapter.getItem(menuInfo.position);
        if(item.getItemId() == R.id.itDel && doc.dst != null) {
            AlertDialog delConfirm = new AlertDialog.Builder(IncomeList.this).create();
            delConfirm.setTitle(getString(R.string.confirm));
            delConfirm.setMessage((doc.isExported()) ? getString(R.string.ask_to_delete_doc) : getString(R.string.doc_not_sent));
            delConfirm.setButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IncomeImpl ii = new IncomeImpl();
                    ii.getData().created = doc.dst.created;
                    ii.read();
                    if( ii.delete()){
                        adapter.refresh();
                    }
                    ii.close();
                }
            });
            delConfirm.show();

            return true;
        }
        return super.onContextItemSelected(item);
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
        List<Item> data = new ArrayList<>();

        public Adapter(Context context) {
        }

        public void refresh() {

            final Map<String, Item> tmap = new HashMap<>();
            data.clear();

            DataTraveler.travel(CheckInvoice.class, new DataTraveler.Travel<CheckInvoice>(true) {
                @Override
                public boolean travel(DataTraveler<CheckInvoice> item) {
                    Item i = new Item();
                    i.src = item.data;
                    data.add(i);
                    tmap.put(item.data.number, i);
                    return true;
                }
            }, "", "date desc");

            DataTraveler.travel(Income.class, new DataTraveler.Travel<Income>(true) {
                @Override
                public boolean travel(DataTraveler<Income> item) {
                    Item i = tmap.get(item.data.number);
                    if(i != null)
                        i.dst = item.data;
                    return true;
                }
            }, "");

            notifyDataSetChanged();
        }

        @Override public int getCount() { return data.size(); }
        @Override public Object getItem(int i) { return data.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null)
                view = View.inflate(IncomeList.this, R.layout.income_list_row, null);

            Item item = (Item) getItem(i);
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(Html.fromHtml(item.getText()));

            tv = view.findViewById(R.id.tvStatus);
            tv.setText(Html.fromHtml(item.getStatus()));
            return view;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View v = super.getView(position, convertView, parent);
//            v.findViewById(R.id.tvSum).setVisibility(View.GONE);
//            return v;
//        }
    }

    static class Item {
        public CheckInvoice src = null;
        public Income dst = null;

        public String getText() {
            return src.number + " от " + Util.simpleDateFormat.format(src.date) + " <i>" + src.name + "</i>";
        }
        public String getStatus() {
            if(dst == null)
                return "";
            return ((dst.params & ParamState.ofExported) != 0) ? "отправлен" : "в работе";
        }

        public boolean isExported() {
            return (dst != null && ((dst.params & ParamState.ofExported) != 0));
        }
    }
}
