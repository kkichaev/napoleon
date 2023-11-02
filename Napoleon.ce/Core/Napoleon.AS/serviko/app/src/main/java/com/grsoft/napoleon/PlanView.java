package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.SppAgent;
import com.grsoft.dataobjects.SppAgentItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanView extends Activity {
    ListView list;
    TextView tvSumMonthPlan;
    TextView tvSumDayPlan;
    TextView tvSumMonthFact;
    TextView tvSumDayFact;

    public static void open(Context context){
        Intent i = new Intent(context, PlanView.class);
        context.startActivity(i);
    }

    static class Result{
        int sumMonthPlan = 0;
        int sumMonthFact = 0;
        int sumDayPlan = 0;
        int sumDayFact = 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_view);
        list = findViewById(R.id.list);

        tvSumMonthPlan = findViewById(R.id.tvSumMonthPlan);
        tvSumDayPlan = findViewById(R.id.tvSumDayPlan);
        tvSumMonthFact = findViewById(R.id.tvSumMonthFact);
        tvSumDayFact = findViewById(R.id.tvSumDayFact);

        list.setAdapter(new Adapter(this));
        list.setDividerHeight(0);

        int wd = getWorkDayCount();
        Map<String, Integer> sums = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.setTime(Util.resetTime(c.getTime()));
        Date start = c.getTime();
        c.add(Calendar.DATE, 1);
        DocList docs = OrderDoc.instance().docList(null, null, new DatePeriod(start, c.getTime()));

        for (Document<?> d : docs){
            OrderImpl order = (OrderImpl)d;

            for (OrderItem i : order.getData().items){
                if (!sums.containsKey(i.id))
                    sums.put(i.id, 0);

                sums.put(i.id, sums.get(i.id) + itemCost(i));
            }
        }

        Result r = new Result();

        DbWriter.checkDBTable(SppAgent.class);

        List<SppAgent> list = DbReader.fetch(SppAgent.class);

        for (SppAgent i : list) {
            Data d = new Data();
            d.planGroup = i.planGroup;
            d.monthPlan = i.monthPlan;
            d.monthFact = i.monthFact;
            d.dayPlan = (d.monthPlan - d.monthFact) / wd;

            for (SppAgentItem a : i.items)
                if (sums.containsKey(a.id))
                    d.dayFact += sums.get(a.id);

            data.add(d);

            r.sumMonthPlan += d.monthPlan;
            r.sumDayFact += d.dayFact;
            r.sumMonthFact += d.monthFact;
            r.sumDayPlan += d.dayPlan;
        }

        tvSumMonthPlan.setText(Util.IntToScaleStr(r.sumMonthPlan, Consts.SUM_SCALE));
        tvSumDayPlan.setText(Util.IntToScaleStr(r.sumDayPlan, Consts.SUM_SCALE));
        tvSumMonthFact.setText(Util.IntToScaleStr(r.sumMonthFact, Consts.SUM_SCALE));
        tvSumDayFact.setText(Util.IntToScaleStr(r.sumDayFact, Consts.SUM_SCALE));
    }

    public static class Data {
        public String planGroup = "";
        public int monthPlan = 0;
        public int monthFact = 0;
        public int dayPlan = 0;
        public int dayFact = 0;
    }

    public List<Data> data = new ArrayList<>();

    private int itemCost(OrderItem item) {
        return item.cost * item.qty / Consts.QTY_SCALE;
    }

    private int getWorkDayCount() {
        int wd = 0;

        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH);

        while (c.get(Calendar.MONTH) == m){
            int x = c.get(Calendar.DAY_OF_WEEK);

            if (x != Calendar.SUNDAY && x != Calendar.SATURDAY)
                wd++;

            c.add(Calendar.DATE, 1);
        }

        return wd;
    }

    public static class Adapter extends BaseAdapter{
        PlanView context;

        public Adapter(PlanView context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return context.data.size();
        }

        @Override
        public Object getItem(int position) {
            return context.data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(context, R.layout.plan_view_row, null);

            Data item = (Data) getItem(position);
            TextView tv = convertView.findViewById(R.id.tvPlanGroup);
            tv.setText(item.planGroup);

            tv = convertView.findViewById(R.id.tvMonthPlan);
            tv.setText(Util.IntToScaleStr(item.monthPlan, Consts.SUM_SCALE));

            tv = convertView.findViewById(R.id.tvDayPlan);
            tv.setText(Util.IntToScaleStr(item.dayPlan, Consts.SUM_SCALE));

            tv = convertView.findViewById(R.id.tvMonthFact);
            tv.setText(Util.IntToScaleStr(item.monthFact, Consts.SUM_SCALE));

            tv = convertView.findViewById(R.id.tvDayFact);
            tv.setText(Util.IntToScaleStr(item.dayFact, Consts.SUM_SCALE));

            int color = item.dayFact < item.dayPlan ? R.color.red : R.color.green;
            tv.setTextColor(context.getResources().getColor(color));

            return convertView;
        }
    }
}
