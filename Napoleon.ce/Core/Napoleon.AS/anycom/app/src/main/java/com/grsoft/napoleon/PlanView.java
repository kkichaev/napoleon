package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.AgentPlanItem;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanView extends Activity {
    Adapter adapter;

    public static void open(Context context) {
        Intent i = new Intent(context, PlanView.class);
        context.startActivity(i);
    }

    public interface CountHandler {
        void onFinish(List<DataItem> data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_view);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

//        if(BuildConfig.DEBUG) {
//            c.set(Calendar.MONTH, Calendar.JUNE);
//        }

        ListView lv = findViewById(R.id.lvItems);
        adapter = new Adapter();
        lv.setAdapter(adapter);

        long begin = c.getTime().getTime();
        c.add(Calendar.MONTH, 1);
        long finish = c.getTime().getTime();
        String filter = String.format("[begin] >= %d and [begin] < %d", begin, finish);
        for(AgentPlan ap : DbReader.fetch(AgentPlan.class, filter)) {
            countPlan(ap, begin, finish, (data) -> refreshPlan(data));
            break;
        }

    }

    void refreshPlan(List<DataItem> data) {
        DataItem total = new DataItem();
        total.name = "хрнцн";
        for(DataItem si : data) {
            total.add(si);
        }

        runOnUiThread(() -> {
            adapter.refresh(data);

            TextView tv[] = new TextView[] {
                    findViewById(R.id.tvTotalName),
                    findViewById(R.id.tvTotalOrder),
                    findViewById(R.id.tvTotalFOrder),
                    findViewById(R.id.tvTotalAKB),
                    findViewById(R.id.tvTotalFAKB),
            };
            total.setText(tv);
        });
    }

    void countPlan(AgentPlan plan, long begin, long finish, CountHandler handler) {
        List<DataItem> data = new ArrayList<>();

        Thread th = new Thread(() -> {
            FolderCache fc = new FolderCache();
            FolderTree ft = CostStrategy.getFolders();
            for(AgentPlanItem api : plan.plans) {
                data.add(new DataItem(api, ft));
            }

            String filter = String.format("date >= %d and date < %d", begin, finish);
            for (Document<?> doc : DeliveryDoc.instance().docList(null, null, filter)) {
                Delivery d = (Delivery) doc.getData();
                for(DeliveryItem di : d.items) {
                    for(DataItem item : data) {
                        item.add(d, di, fc);
                    }
                }
            }
            fc.close();
            handler.onFinish(data);
        });
        th.start();
    }

    class Adapter extends BaseAdapter {
        List<DataItem> data;
        public Adapter() { data = new ArrayList<>(); }

        public void refresh(List<DataItem> src) {
            data = src;
            notifyDataSetChanged();
        }

        @Override public int getCount() {return data.size();}
        @Override public Object getItem(int position) {return data.get(position);}

        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(PlanView.this, R.layout.planv_view_row, null);
            }
            DataItem item = (DataItem) getItem(position);

            TextView tv[] = new TextView[] {
                    view.findViewById(R.id.tvName),
                    view.findViewById(R.id.tvOrder),
                    view.findViewById(R.id.tvFOrder),
                    view.findViewById(R.id.tvAKB),
                    view.findViewById(R.id.tvFAKB),
            };
            item.setText(tv);
            return view;
        }
    }

    static class FolderCache {
        Map<String, String> data = new HashMap<>();
        PriceImpl pi = new PriceImpl();

        public String get(String priceId) {
            String fid = data.get(priceId);
            if(fid == null) {
                Price p = pi.getData();
                p.id = priceId;
                if(pi.read()) {
                    fid = ((PriceEx)p).fid;
                } else {
                    fid = "";
                }
                data.put(priceId, fid);
            }
            return fid;
        }

        public void close() {
            pi.close();
            data.clear();
        }
    }

    static class DataItem {
        public String name = "";
        public long orders = 0;
        public int akb = 0;

        public long fact_orders = 0;

        Set<String> folders = new HashSet<>();
        List<String> orgs = new ArrayList<>();

        public  DataItem() {}

        public DataItem(AgentPlanItem src, FolderTree ft) {
            for(Folder f : ft.getWithDescendats(src.id)) {
                if(folders.size() == 0) {
                    name = f.name;
                }
                folders.add(f.fid);
            }

            orders = src.order;
            akb = src.akb;
        }

        public void add(Delivery doc, DeliveryItem item, FolderCache fc) {
            String fid = fc.get(item.id);
            if(folders.contains(fid)) {
                fact_orders += ((DeliveryItemEx)item).sumWOTax;
                if(!orgs.contains(doc.id))
                    orgs.add(doc.id);
            }
        }

        public void add(DataItem src) {
            orders += src.orders;
            akb += src.akb;

            orgs.addAll(src.orgs);
            fact_orders += src.fact_orders;
        }

        String pcText(long pc) {
            return String.format("<font color='red'>%d%%</font>", pc);
        }

        public void setText(TextView[] tv) {
            tv[0].setText(name);
            String text;
            if(orders > 0) {
                text = Util.IntToScaleStr(orders, Consts.SUM_SCALE, Util.DEC_DELIM, false);
                text += "<br/>";
                text += Util.IntToScaleStr(fact_orders, Consts.SUM_SCALE, Util.DEC_DELIM, false);
                long pc = ((long)fact_orders * 100) / orders;
                text += " / " + pcText(pc);

                tv[1].setText(Html.fromHtml(text));

//                text = Util.IntToScaleStr(fact_orders, Consts.SUM_SCALE, Util.DEC_DELIM, false);
//                long pc = ((long)fact_orders * 100) / orders;
//                text += " / " + pcText(pc);
//
//                tv[2].setText(Html.fromHtml(text));
            } else {
                text = "";
                tv[1].setText(text);
                tv[2].setText(text);
            }

            if(akb > 0) {
                text = Integer.toString(akb);
                text += "<br/>";
                int fact_akb = orgs.size();
                text += Integer.toString(fact_akb);
                long pc = ((long)fact_akb * 100) / akb;
                text += " / " + pcText(pc);
                tv[3].setText(Html.fromHtml(text));

//                int fact_akb = orgs.size();
//                text = Integer.toString(fact_akb);
//                long pc = ((long)fact_akb * 100) / akb;
//                text += " / " + pcText(pc);
//                tv[4].setText(Html.fromHtml(text));
            } else {
                text = "";
                tv[3].setText(text);
                tv[4].setText(text);
            }
        }
    }
}
