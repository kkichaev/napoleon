package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SalesReport extends FragmentActivity {

    static int DATE_START = 0;
    static int DATE_END = 1;
    static int WH_FILTER = 2;

    Params params = new Params();
    FilterDialog paramDialog;

    static void open(Context context) {
        Intent i = new Intent(context, SalesReport.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salesreport);

        paramDialog = new FilterDialog(params, this);
        paramDialog.show(getSupportFragmentManager(), "");

        findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramDialog = new FilterDialog(params, SalesReport.this);
                paramDialog.show(getSupportFragmentManager(), "");
            }
        });
    }

    void setDate(int dateType) {
        Intent i = new Intent(this, CalendarActivity.class);
        i.putExtra(ExtrasConst.DATE_TAG, dateType == DATE_START ? params.start.getTime() : params.end.getTime());
        startActivityForResult(i, dateType);
    }

    void reporting() {
        ((TextView)findViewById(R.id.tvFilterInfo)).setText(Html.fromHtml(params.text()));

        findViewById(R.id.llProgress).setVisibility(View.VISIBLE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                makeReport();
            }
        });

        t.start();
    }

    void makeReport() {
        final ReportAccum ra = new ReportAccum(params);

        String where = "created >= " + Long.toString(Util.getDayStart(params.start).getTime()) +
                " and created < " + Long.toString(Util.getDayEnd(params.start).getTime());

        if(params.id.length() > 0)
            where += " and id='" + params.id + "'";

        DocList dl = OrderDoc.instance().docList(null, null, where);
        for(Document d : dl) {
            ra.add((Order) d.getData());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.llProgress).setVisibility(View.GONE);

                ListView lv = findViewById(R.id.lvItems);
                List<ReportData> src = ra.result();
                lv.setAdapter(new Adapter(src));
                long sum = 0;
                for(ReportData rd : src) sum += rd.sum;
                ((TextView)findViewById(R.id.tvTotalSum)).setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK ) {
            if(requestCode == WH_FILTER) {
                Bundle b = data.getExtras();
                String[] items = b.getStringArray(WarehouseFilter.ITEM_TAG);
                int[] folders = b.getIntArray(WarehouseFilter.FOLDER_TAG);

                params.items = Arrays.asList(items);
                params.folders.clear();
                for(int f : folders) params.folders.add(f);

                paramDialog.setPrice();
            } else  if (requestCode == DATE_END | requestCode == DATE_START) {
                if (data != null) {
                    long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, new Date().getTime());
                    Date newDate = new Date(ct);
                    if (requestCode == DATE_END) params.end = newDate;
                    else params.start = newDate;
                    paramDialog.refrehsParams();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class FilterDialog extends DialogFragment {
        Params params;
        SalesReport owner;
        View view;
        CheckBox cbPrice;

        public FilterDialog(Params params, SalesReport owner) {
            this.params = params;
            this.owner = owner;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder b = new AlertDialog.Builder(owner);
            view = View.inflate(owner, R.layout.sales_report_params, null);
            b.setTitle("Параметры отчета");
            b.setView(view);
            view.findViewById(R.id.tvEnd).setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) { owner.setDate(SalesReport.DATE_END); }
            });

            Spinner sp;
            sp = view.findViewById(R.id.spOrg);
            DialogHelper.loadSpinnerFromDataObject(sp, OrgView.class, new DialogHelper.Selected<OrgView>() {
                @Override public boolean isSelected(OrgView object) { return object.id.equals(params.id); }
            }, true, "name");

            sp = view.findViewById(R.id.spMatrix);
            DialogHelper.loadSpinnerFromDataObject(sp, MatrixView.class, new DialogHelper.Selected<MatrixView>() {
                @Override public boolean isSelected(MatrixView object) { return object.name.equals(params.matrix); }
            }, true, "name");

            final CheckBox cbMatrix = view.findViewById(R.id.cbMatrix);
            cbPrice = view.findViewById(R.id.cbPrice);

            cbMatrix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cbPrice.setChecked(!isChecked);
                }
            });
            cbPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cbMatrix.setChecked(!isChecked);
                }
            });

            if(params.useMatrix) {
                cbMatrix.setChecked(true);
            } else {
                cbPrice.setChecked(true);
            }

            view.findViewById(R.id.btnItems).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WarehouseFilter.open(owner, params.items, params.folders, WH_FILTER);
                }
            });

            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Spinner sp = view.findViewById(R.id.spOrg);
                    Org sel = (Org) sp.getSelectedItem();
                    params.id = sel.id;
                    params.name = sel.name;

                    params.useMatrix = cbMatrix.isChecked();
                    if(params.useMatrix) {
                        MatrixView msel = (MatrixView) ((Spinner)view.findViewById(R.id.spMatrix)).getSelectedItem();
                        params.matrix = msel;
                    }

                    owner.reporting();
                    dismiss();
                }
            });


            refrehsParams();
            return b.create();
        }

        public void setPrice() {
            cbPrice.setChecked(true);
            params.useMatrix = false;
        }

        public void refrehsParams() {
            TextView tv;
            tv = view.findViewById(R.id.tvStart);
            tv.setText(Util.simpleDateFormat.format(params.start));

            tv = view.findViewById(R.id.tvEnd);
            tv.setText(Util.simpleDateFormat.format(params.end));
        }
    }

    static class ReportAccum {

        Map<String, ReportData> items = new HashMap<>();
        List<Pair<Set<String>, ReportData>> folders = new ArrayList<>();
        boolean allItems = false;
        PriceImpl pi = new PriceImpl();

        public ReportAccum(Params params) {
            Price p = pi.getData();

            if(params.useMatrix) {
                if(params.matrix.name.length() == 0) {
                    allItems = true;
                } else {
                    for(MatrixItem mi: params.matrix.items) {
                        p.id = mi.id;
                        if(pi.read()) {
                            ReportData rd = new ReportData();
                            rd.name = p.name;
                            rd.isFolder = false;
                            items.put(mi.id, rd);
                        }
                    }
                }
            } else {
                for(String id : params.items) {
                    p.id = id;
                    if(pi.read()) {
                        ReportData rd = new ReportData();
                        rd.name = p.name;
                        rd.isFolder = false;
                        items.put(id, rd);
                    }
                }


                FolderTree ft = new FolderTree();
                ft.load();
                for(Integer fi : params.folders) {
                    String ids = "";
                    String name = "";
                    for(Folder fs : ft.getWithDescendats(fi)) {
                        if(ids.length() > 0) {
                            ids += ",";
                        } else {
                            name = fs.name;
                        }
                        ids += Integer.toString(fs.id);
                    }

                    if(ids.length() > 0) {
                        Set<String> idSet = new HashSet<>();
                        try {
                            Cursor c = DataBaseManager.getDataBase().rawQuery("select id from " + new Price().getTableName() + " where folderid in (" + ids + ")", null);
                            while(c.moveToNext()) {
                                idSet.add((c.getString(0)));
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        if(idSet.size() > 0) {
                            ReportData rd = new ReportData();
                            rd.isFolder = true;
                            rd.name = name;
                            folders.add(new Pair<Set<String>, ReportData>(idSet, rd));
                        }
                    }
                }
            }
        }

        public void add(Order doc) {
            for(OrderItem oi : doc.items) {
                ReportData rd = items.get(oi.id);
                if(rd != null)
                    rd.add(oi);
                else  if(allItems) {
                    Price p = pi.getData();
                    p.id = oi.id;
                    if(pi.read()) {
                        rd = new ReportData();
                        rd.name = p.name;
                        rd.isFolder = false;
                        rd.add(oi);
                        items.put(oi.id, rd);
                    }
                }
                for(Pair<Set<String>, ReportData> kv : folders) {
                    if(kv.first.contains(oi.id))
                        kv.second.add(oi);
                }
            }
        }

        public List<ReportData> result() {
            pi.close();
            List<ReportData> ret = new ArrayList<>();

            for(ReportData rd : items.values()) {
                if(rd.qty > 0) {
                    ret.add(rd);
                }
            }

            for(Pair<Set<String>, ReportData> rd : folders) {
                if(rd.second.qty > 0) {
                    ret.add(rd.second);
                }
            }

            Collections.sort(ret);
            return ret;
        }
    }

    public static class OrgView extends Org {
        @Override
        public String toString() { return name.length() == 0 ? "<все>" : name; }
    }

    public static class MatrixView extends Matrix {
        @NonNull
        @Override
        public String toString() { return name.length() == 0 ? "<весь товар>" : name; }
    }

    static class Params {
        public Date start = Util.getDate();
        public Date end = Util.getDate();
        public String id = "";
        public String name = "";
        public MatrixView matrix = new MatrixView();
        public boolean useMatrix = true;

        public List<Integer> folders = new ArrayList<>();
        public List<String> items = new ArrayList<>();

        public String text() {
            String ret = "";
            ret += Util.simpleDateFormat.format(start) + " - " + Util.simpleDateFormat.format(end);
            if(name.length() > 0) {
                ret += "<br/>" + name;
            }
            if(useMatrix)
                ret += "<br/>Матрица:" + matrix.toString();
            return ret;
        }
    }

    static class ReportData implements Comparable<ReportData> {
        boolean isFolder = false;
        String name = "";
        int qty = 0;
        long sum = 0;

        public void add(OrderItem i) {
            qty += i.qty;
            sum += (long)i.qty * i.cost / Consts.QTY_SCALE;
        }

        @Override
        public int compareTo(ReportData o) {
            if(isFolder) {
                return o.isFolder ? name.compareTo(o.name) : -1;
            }

            return o.isFolder ? 1 : name.compareTo(o.name);
        }
    }

    class Adapter extends BaseAdapter {
        List<ReportData> data;
        public Adapter(List<ReportData> data) {
            this.data = data;
        }

        @Override public int getCount() { return data.size(); }
        @Override public Object getItem(int position) { return data.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(SalesReport.this, R.layout.sales_report_row, null);
            }
            ReportData data = (ReportData) getItem(position);

            ImageView iv = view.findViewById(R.id.ivImage);
            if(data.isFolder) {
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }

            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(data.name);

            tv = view.findViewById(R.id.tvQty);
            tv.setText(Util.IntToScaleStr(data.qty, Consts.QTY_SCALE));

            tv = view.findViewById(R.id.tvSum);
            tv.setText(Util.IntToScaleStr(data.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));

            view.setBackgroundResource((position % 2) == 0 ? R.drawable.list_selector : R.drawable.even_row_selector);
            return view;
        }
    }
}
