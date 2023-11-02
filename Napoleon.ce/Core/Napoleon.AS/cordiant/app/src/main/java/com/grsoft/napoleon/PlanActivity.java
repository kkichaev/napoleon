package com.grsoft.napoleon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.impl.PlanDataImpl;
import com.grsoft.dataobjects.impl.PlanDataItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PlanActivity extends Activity {
    Adapter adapter;
    ListView list;
    TextView tvLastSync;

    public static void open(Context context){
        Intent i = new Intent(context, PlanActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans);

        list = findViewById(R.id.list);
        tvLastSync = findViewById(R.id.tvLastSync);

        adapter = new Adapter(this);
        adapter.reload();

        list.setAdapter(adapter);
        list.setDividerHeight(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(syncFinished, new IntentFilter(PlanSync.FINISH_SYNC));
        updateLastSyncText();
    }

    BroadcastReceiver syncFinished = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.reload();
            adapter.notifyDataSetChanged();
            updateLastSyncText();
        }
    };

    private void updateLastSyncText() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        long time = pref.getLong(PlanSync.LAST_SYNC_TIME, -1);
        String text = "";

        if (time != -1) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            text = getString(R.string.last_sync_time,sdf.format(c.getTime()));
        }

        tvLastSync.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plans_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itSync)
            requestPlans();
        return super.onOptionsItemSelected(item);
    }

    private void requestPlans() {
        new PlanSync(this).execute((Void[])null);
    }

    public static class Adapter extends BaseAdapter{
        PlanDataImpl data = new PlanDataImpl();
        Context context;
        public Adapter(Context context){
            this.context = context;
            reload();
        }

        public void reload() {
            data.read("id", "1");
        }

        @Override
        public int getCount() {
            return data.getData().items.size();
        }

        @Override
        public Object getItem(int position) {
            return data.getData().items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(context, R.layout.plandata_row, null);

            PlanDataItem item = (PlanDataItem) getItem(position);

            int color = context.getResources().getColor(R.color.plan_red);

            if (item.fact >= item.plan)
                color = context.getResources().getColor(R.color.plan_green);
            else if (item.fact >= item.plan * 0.5)
                color = context.getResources().getColor(R.color.plan_yelow);

            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(item.name);
            tv.setTextColor(color);

            tv = convertView.findViewById(R.id.tvPlan);
            tv.setText(Util.IntToScaleStr(item.plan, Consts.SUM_SCALE));
            tv.setTextColor(color);

            tv = convertView.findViewById(R.id.tvFact);
            tv.setText(Util.IntToScaleStr(item.fact, Consts.SUM_SCALE));
            tv.setTextColor(color);

            return convertView;
        }
    }
}
