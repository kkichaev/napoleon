package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Bank;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.BankImpl;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BankList extends Activity {
    protected static final int DLG_FILTER_SELECT = 0;
    private static final int BEGIN_DATE_CODE = 10;
    private static final int END_DATE_CODE = 11;

    public static void open(Context context){
        Intent intent = new Intent(context, BankList.class);
        context.startActivity(intent);
    }

    ListView list;
    TextView tvDocSum;
    Adapter adapter;
    protected DatePeriod saveDatePeriod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_list_view);
        tvDocSum = findViewById(R.id.tvDocSum);
        Date now = Util.getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR_OF_DAY, 23);
        cal.add(Calendar.MINUTE, 59);
        saveDatePeriod = makeInitialDatePeriod(now, cal.getTime());
        adapter = new Adapter(this, saveDatePeriod);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        findViewById(R.id.btnNewDoc).setOnClickListener(this::newDoc);
        list.setOnItemClickListener(this::onItemClick);
        findViewById(R.id.btnFilter).setOnClickListener(this::filter);
        registerForContextMenu(list);

    }

    protected DatePeriod makeInitialDatePeriod(Date begin, Date end) {
        return new DatePeriod(begin, end);
    }

    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BankImpl bank = (BankImpl) adapterView.getItemAtPosition(i);
        bank.preview(this);
    }

    private void newDoc(View view) {
        BankImpl bank = new BankImpl();
        bank.init(this, "", GPSUtilNew.getLastKnownLocation());
        bank.open(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reload();
        adapter.notifyDataSetChanged();
    }

    static class Adapter extends BaseAdapter{
        private final Context context;
        private DocList data;
        private FirmImpl dogovor = new FirmImpl();
        private DatePeriod filter;
        private Firm firm = new Firm();

        public Adapter(Context context, DatePeriod filter){
            this.context = context;
            this.filter = filter;

            reload();
        }

        @Override
        public int getCount() {
            return data.getCount();
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
            if (view == null)
                view = View.inflate(context, R.layout.bank_list_row, null);

            BankImpl doc = (BankImpl) getItem(position);

            dogovor.read("id", ((Bank)doc.getData()).dogovor);

            TextView tv = view.findViewById(R.id.tvDogovor);
            tv.setText(dogovor.getData().name);

            tv = view.findViewById(R.id.tvDate);
            tv.setText(Util.simpleDateFormat.format(doc.getData().created));

            tv = view.findViewById(R.id.tvSum);
            tv.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE));

            tv = view.findViewById(R.id.tvDescr);
            tv.setText(Html.fromHtml(doc.getDescription(view.getContext())));
            return view;
        }

        public void reload() {
            data = BankDoc.instance().docList(null, "created DESC", filter);

            if (firm.id.trim().length() > 0){
                List<Long> rem = new ArrayList<>();

                for (Document d : data){
                    if (!((Bank)d.getData()).dogovor.equals(firm.id))
                        rem.add(d.getRowid());
                }

                data.removeDocuments(rem);
            }

            ((BankList)context).adapterReloaded(data);
        }

        public DatePeriod getFilter() {
            return filter;
        }

        public void setFilter(DatePeriod dp, Firm firm) {
            this.filter = dp;
            this.firm = firm;
            reload();
        }
    }

    public void filter(View view) {
        showDialog(DLG_FILTER_SELECT);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id){
            case DLG_FILTER_SELECT:
                return createDlgFilter();
            default:
                return super.onCreateDialog(id);
        }
    }

    View dialogView;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        Date curDate = new Date();
        if( data != null ) {
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());

            Date newDate = new Date();
            int id = R.id.tvDateBegin;
            DatePeriod dp = null;

            if(adapter != null)
                dp = adapter.getFilter();

            if( dp == null ) {
                dp = saveDatePeriod;
            }

            if( requestCode == BEGIN_DATE_CODE ) {
                dp.begin = new Date(ct);
                newDate = dp.begin;
                id = R.id.tvDateBegin;
            }
            else if( requestCode == END_DATE_CODE) {
                ct += (24 * 3600 - 1) * 1000;
                dp.end = new Date(ct);
                newDate = dp.end;
                id = R.id.tvDateEnd;
            }
            TextView dv = (TextView) dialogView.findViewById(id);
            dv.setText(Util.simpleDateFormat.format(newDate));
        }
    }

    void setDate(int dateType, Date date) {
        Intent i = new Intent(this, CalendarActivity.class);
        i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
        startActivityForResult(i, dateType);
    }

    protected Dialog createDlgFilter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogView = View.inflate(this, R.layout.filter_dlg, null);
        ((TextView)dialogView.findViewById(R.id.tvBegin)).setText(getString(R.string.begin) + ":");
        ((TextView)dialogView.findViewById(R.id.tvEnd)).setText(getString(R.string.end) + ":");
        final DatePeriod dp = (adapter != null && adapter.getFilter() != null) ? adapter.getFilter() : saveDatePeriod;
        TextView dv = (TextView) dialogView.findViewById(R.id.tvDateBegin);
        dv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setDate(BEGIN_DATE_CODE, dp.begin);
            }
        });
        dv.setText(Util.simpleDateFormat.format(dp.begin));

        dv = (TextView) dialogView.findViewById(R.id.tvDateEnd);
        dv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setDate(END_DATE_CODE, dp.end);
            }
        });
        dv.setText(Util.simpleDateFormat.format(dp.end));

        Spinner sp = (Spinner)dialogView.findViewById(R.id.spFirm);
        ArrayList<KeyValue> values = new ArrayList<KeyValue>();
        values.add(new KeyValue("", getResources().getString(R.string.all)));

        List<Firm> data = new ArrayList<>();
        Firm all = new Firm();
        all.name = "<Все>";
        data.add(all);

        for (Firm d : DbReader.fetch(Firm.class))
            data.add(d);

        Collections.sort(data, (lhs, rhs) -> lhs.name.compareToIgnoreCase(rhs.name));
        ArrayAdapter<Firm> filter = new ArrayAdapter<Firm>(this, R.layout.simple_spinner_layout, data);
        sp.setAdapter(filter);

        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, setFilter);
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    private DialogInterface.OnClickListener setFilter = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) { filterClick(dialog); }
    };

    protected void filterClick(DialogInterface dialog) {
        AlertDialog alertDialog = (AlertDialog)dialog;

        CheckBox cbCreatedFiltered = (CheckBox) alertDialog.findViewById(R.id.cbCreatedFiltered);
        DatePeriod dp = null;

        if (adapter != null)
            dp = adapter.getFilter();

        if( dp == null )
            dp = saveDatePeriod;

        dp.periodType = DatePeriod.CREATED;

        Spinner sp = (Spinner)alertDialog.findViewById(R.id.spFirm);
        Firm firm = (Firm)sp.getSelectedItem();
        adapter.setFilter(dp, firm);
        adapter.notifyDataSetChanged();
    }

    private void adapterReloaded(DocList docs) {
        long sum = 0;

        for (Document d : docs)
            sum += d.sum();

        tvDocSum.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.bank_list_opt_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        BankImpl doc = (BankImpl) list.getAdapter().getItem(menuInfo.position);

        if (item.getItemId() == R.id.itDelete){
            doc.delete();
            doc.close();
            ((Adapter)list.getAdapter()).reload();
            ((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }
}
