package com.grsoft.napoleon.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.ScheduleAlarm;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSchedule;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgScheduleImpl;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Schedule extends BaseFragment implements SelectTime.TimeSelectedListener {
    static final String TAG = Schedule.class.toString();
    private static final String TASK_ALARM_CNT = "task_alarm_cnt";
    View v;
    CalendarView calendarView;
    OrgScheduleAdapter adapter;
    Date currentDate;

    @Override
    protected int getLayoutID() {
        return R.layout.schedule_view;
    }

    @Override
    public String TAG() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.btnAdd).setOnClickListener(v-> showSelectTime(null));
        v.findViewById(R.id.btnOK).setOnClickListener(v->getParentFragmentManager().popBackStack());

        calendarView = v.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((w,y,m,d)->{
            currentDate = new Date(y-1900,m,d);
            adapter.reload(currentDate);
            adapter.notifyDataSetChanged();
        });

        currentDate = Util.resetTime(new Date());
        adapter = new OrgScheduleAdapter(this);
        adapter.reload(currentDate);

        RecyclerView items = v.findViewById(R.id.items);
        items.setAdapter(adapter);
        items.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        return v;
    }

    public void showSelectTime(String key) {
        SelectTime dlg = new SelectTime();

        Bundle args = new Bundle();
        args.putString(SelectTime.KEY, key);
        dlg.setArguments(args);
        dlg.setTimeSelectedListener(this);
        dlg.show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public String getTitle() {
        return getString(R.string.visit_to, model.currentOrg.getValue().name);
    }

    public void editSchedule(String key){
        showSelectTime(key);
    }

    public void deleteSchedule(String key){
        OrgScheduleImpl impl = new OrgScheduleImpl();
        impl.read("key", key);
        impl.delete();
        impl.close();

        adapter.reload(currentDate);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTimeSelected(String k, int h, int m, String c, boolean a, int t) {
        Calendar cr = Calendar.getInstance();
        cr.setTimeInMillis(currentDate.getTime());
        cr.setTime(Util.resetTime(cr.getTime()));
        cr.set(Calendar.HOUR_OF_DAY, h);
        cr.set(Calendar.MINUTE, m);

        OrgScheduleImpl impl = new OrgScheduleImpl();
        OrgSchedule doc = impl.getData();

        if (k == null) {
            doc.key = UUID.randomUUID().toString().replace("-", "");
            doc.id = model.getCurrentOrg().getValue().id;
        }else {
            doc.key = k;
            impl.read();
        }

        doc.date = cr.getTime();
        doc.remark = c;
        doc.params = 0;

        impl.write();
        impl.close();

        if (a)
            setAlarm(doc.date, t, model.getCurrentOrg().getValue());

        adapter.reload(currentDate);
        adapter.notifyDataSetChanged();
    }

    private void setAlarm(Date date, int min, OrgEx org) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, -min);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int id = pref.getInt(TASK_ALARM_CNT, 0);

        AlarmManager amr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(getContext(), ScheduleAlarm.class);
        i.putExtra(ScheduleAlarm.TEXT, buildText(date, org));

        PendingIntent pi = PendingIntent.getBroadcast(getContext(), id, i, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        amr.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        SharedPreferences.Editor ed = pref.edit();
        ed.putInt(TASK_ALARM_CNT, ++id);
        ed.commit();
    }

    private String buildText(Date date, OrgEx org) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy hh:mm", Locale.getDefault());
        return getString(R.string.visit_alarm_text, org.name, sdf.format(date));
    }

    private static class OrgScheduleHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView tvTime;
        TextView tvOrg;
        OrgScheduleAdapter adapter;

        public OrgScheduleHolder(@NonNull View itemView, OrgScheduleAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            tvOrg = itemView.findViewById(R.id.tvOrg);
            tvTime = itemView.findViewById(R.id.tvTime);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater mi = new MenuInflater(v.getContext());
            mi.inflate(R.menu.schedule_context_menu, menu);

            for(int i = 0; i < menu.size(); i++)
                menu.getItem(i).setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.miDel){
                adapter.schedule.deleteSchedule(adapter.data.get(getAdapterPosition()).key);
                return true;
            } else if (item.getItemId() == R.id.miEdit){
                adapter.schedule.editSchedule(adapter.data.get(getAdapterPosition()).key);
                return true;
            }

            return false;
        }
    }

    private static class OrgScheduleAdapter extends RecyclerView.Adapter<OrgScheduleHolder>{
        List<OrgSchedule> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Schedule schedule;

        public OrgScheduleAdapter(Schedule schedule){
            this.schedule = schedule;
        }

        @NonNull
        @Override
        public OrgScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
            return new OrgScheduleHolder(v,this);
        }

        @Override
        public void onBindViewHolder(@NonNull OrgScheduleHolder holder, int position) {
            OrgSchedule s = data.get(position);
            holder.tvTime.setText(timeToString(s.date));
            holder.tvOrg.setText(getOrgName(s.id));
        }

        private String getOrgName(String id) {
            OrgImpl org = new OrgImpl();
            org.read("id",id);
            return org.getData().name;
        }

        private String timeToString(Date date) {
            return sdf.format(date);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void reload(Date date){
            data.clear();

            Calendar c = Calendar.getInstance();
            c.setTime(Util.resetTime(date));
            Date s = c.getTime();
            c.add(Calendar.DATE, 1);
            Date e = c.getTime();

            String where = String.format("date>=%d and date<%d", s.getTime(), e.getTime());

            DataTraveler.travel(OrgSchedule.class, new DataTraveler.Travel<OrgSchedule>(true){
                @Override
                public boolean travel(DataTraveler<OrgSchedule> item) {
                    data.add(item.data);
                    return true;
                }
            }, where);

            data.sort(Comparator.comparing(x -> x.date));
        }
    }
}
