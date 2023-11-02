package com.grsoft.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.RouteDeviation;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentAlerts extends DrawerActivity {

    Map<Object, ManagerAgent> agents = new HashMap<>();
    Adapter adapter;

    public static void open(Context context){
        Intent i = new Intent(context, AgentAlerts.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
        context.startActivity(i);
    }

    @Override protected int getLayoutID() {return R.layout.agent_alert;}
    @Override protected int getOptionsMenuID() {return R.menu.alert_option_menu;}
    @Override protected String getActionBarTitle() {return getString(R.string.alert_menu);}

    @Override
    protected void postSyncUpdate() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        agents = DbReader.fetchDic(ManagerAgent.class, "id");
        ListView lv = findViewById(R.id.list);
        adapter = new Adapter(getDate());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            RouteDeviation rd = (RouteDeviation) adapter.getItem(i);
            Date date = getDate();
            SyncDetail.sync(this, createUpdateCtrl(this, date, rd.userid), rd.userid, date, true);
        });
    }

    private UpdateCtrl createUpdateCtrl(final Activity activity, final Date date, final String userid) {
        return new UpdateCtrl() {
            @Override public void onFinish(boolean result) {
                if( result )
                    AgentRouteNew.open(activity, userid, date, AgentRouteNew.MAP_VIEW_TYPE);
            }
            @Override public void updateCtrl(boolean enabled) {} };
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        super.onDateSet(view, year, monthOfYear, dayOfMonth);
        adapter.refresh(getDate());
    }

    class Adapter extends BaseAdapter {
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        List<RouteDeviation> data = new ArrayList<>();
        public Adapter(Date date) {
            refresh(date);
        }

        public void refresh(Date date) {
            long tm = Util.resetTime(date).getTime();
            String where = String.format("date >= %d and date < %d", tm, tm+24*3600*1000);
            data = DbReader.fetch(RouteDeviation.class, where, "date desc");
            notifyDataSetChanged();
        }

        @Override public int getCount() {return data.size();}
        @Override public Object getItem(int i) {return data.get(i);}
        @Override public long getItemId(int i) {return i;}

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(AgentAlerts.this, R.layout.alert_row, null);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                view.findViewById(R.id.alert).setMinimumWidth(size.x / 2);
            }

            RouteDeviation rd = (RouteDeviation) getItem(i);
            ManagerAgent ma = agents.get(rd.userid);
            TextView tv;

            tv = view.findViewById(R.id.time);
            tv.setText(sdf.format(rd.date));

            tv = view.findViewById(R.id.agent);
            tv.setText(ma != null ? ma.name : rd.userid);

            tv = view.findViewById(R.id.alert);
            tv.setText(rd.getNotifyText(true));

            view.setBackgroundResource(i % 2 != 0 ? com.grsoft.napoleon.R.drawable.even_row_selector
                    : com.grsoft.napoleon.R.drawable.list_selector);

            return view;
        }
    }
}
