package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.PlanRichard;
import com.grsoft.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class RichardPlanView extends BaseActivity {
    public static void open(Context context) {
        Intent i = new Intent(context, RichardPlanView.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_view);

        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(new Adapter());
    }

    class Adapter extends BaseAdapter {
        List<PlanRichard> plans = new ArrayList<>();
        public Adapter() {
            DbReader r = new DbReader();
            plans = (List<PlanRichard>) r.fetch(PlanRichard.class, "", "title");
        }

        @Override
        public int getCount() {
            return plans.size();
        }

        @Override
        public Object getItem(int position) {
            return plans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(RichardPlanView.this, R.layout.plan_row, null);
            }
            PlanRichard plan = (PlanRichard) getItem(position);
            TextView tv;
            tv = view.findViewById(R.id.tvTitle);
            tv.setText(plan.title);
            tv = view.findViewById(R.id.tvText);
            tv.setText(plan.text);
            return view;
        }
    }
}
