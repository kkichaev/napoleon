package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassDebDistrItem;
import com.grsoft.dataobjects.IncassDebDistrItemEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bank extends Activity {

    static final int FILTER_DLG = 1;
    static final int BEGIN_DATE_CODE = 10;
    static final int END_DATE_CODE = 11;

    Date start = Util.getDate();
    Date end = new Date(Util.getDate().getTime() + 24 * 3600 * 1000);
    View dialogView;

    Date startTmp, endTmp;

    List<DeliveryEx> docs = new ArrayList<>();
    Adapter adapter;

    public static void open(Context context) {
        Intent i = new Intent(context, Bank.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        ListView lv = findViewById(R.id.lvDocs);
        adapter = new Adapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeliveryEx doc = (DeliveryEx) adapter.getItem(position);
                QRView.open(Bank.this, doc.qrCode);
            }
        });

        findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(FILTER_DLG);
            }
        });

        updateDateText();
    }

    private void updateDateText() {
        TextView tv = findViewById(R.id.tvFilter);
        String text = String.format("Документы за период %s - %s", Util.simpleDateFormat.format(start), Util.simpleDateFormat.format(end));
        tv.setText(text);
    }

    void setDate(int dateType, Date date) {
        Intent i = new Intent(this, CalendarActivity.class);
        i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
        startActivityForResult(i, dateType);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == FILTER_DLG) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialogView = View.inflate(this, R.layout.bank_filter, null);
            ((TextView)dialogView.findViewById(R.id.tvBegin)).setText(getString(R.string.begin) + ":");
            ((TextView)dialogView.findViewById(R.id.tvEnd)).setText(getString(R.string.end) + ":");

            TextView dv = (TextView) dialogView.findViewById(R.id.tvDateBegin);
            dv.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    setDate(BEGIN_DATE_CODE, start);
                }
            });
            dv.setText(Util.simpleDateFormat.format(start));

            dv = (TextView) dialogView.findViewById(R.id.tvDateEnd);
            dv.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    setDate(END_DATE_CODE, end);
                }
            });
            dv.setText(Util.simpleDateFormat.format(end));

            builder.setView(dialogView);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    start = startTmp;
                    end = endTmp;
                    updateDateText();
                    adapter.refresh();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            return builder.create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);

        if(id == FILTER_DLG) {
            TextView dv = (TextView) dialogView.findViewById(R.id.tvDateBegin);
            dv.setText(Util.simpleDateFormat.format(start));
            startTmp = start;

            dv = (TextView) dialogView.findViewById(R.id.tvDateEnd);
            dv.setText(Util.simpleDateFormat.format(end));
            endTmp = end;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            if( requestCode == BEGIN_DATE_CODE ) {
                startTmp = new Date(ct);
            } else if(requestCode == END_DATE_CODE) {
                endTmp = new Date(ct);
            }
        }
    }

    class Adapter extends BaseAdapter {
        @Override public int getCount() { return docs.size(); }
        @Override public Object getItem(int position) { return docs.get(position); }
        @Override public long getItemId(int position) { return position; }

        public Adapter() {
            refresh();
        }

        public void refresh() {
            String where = "date >= " + Long.toString(start.getTime()) + " and date <= " + Long.toString(end.getTime());
            DocList dl = IncassDoc.instance().docList(null, null, where);
            docs.clear();

            DbReader r = new DbReader();
            for(Document<?> d : dl) {
                IncassDebDistr id = (IncassDebDistr) d.getData();

                for(IncassDebDistrItem i : id.items) {
                    DeliveryEx dest = new DeliveryEx();
                    String dlvwh = "link='" + ((IncassDebDistrItemEx)i).link +  "'";
                    if(r.select(dest, dest.getTableName(), dlvwh))
                        docs.add(dest);
                }
            }
            r.close();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(Bank.this, R.layout.bank_row, null);
            }
            DeliveryEx doc = (DeliveryEx) getItem(position);
            TextView tv;
            tv = view.findViewById(R.id.tvOther);
            tv.setText(doc.number);

            tv = view.findViewById(R.id.tvDate);
            tv.setText(Util.simpleDateFormat.format(doc.date));

            tv = view.findViewById(R.id.tvOther);
            tv.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE));
            return view;
        }
    }
}
