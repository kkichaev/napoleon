package com.grsoft.napoleon.org_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSchedule;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.main.Schedule;
import com.grsoft.util.Util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter  extends BaseOrgAdapter<OrgHolder> {

    class OrgData extends OrgEx{
        public OrgSchedule schedule;

        public OrgData(OrgEx src){
            for (Field f : src.getFields()){
                Field dst = getField(f.getName());

                try {
                    dst.set(this, f.get(src));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    class ScheduleHolder extends OrgHolder{
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        public ScheduleHolder(BaseOrgAdapter<?> owner, @NonNull View itemView) {
            super(owner, itemView);
        }

        @Override
        public void update(OrgEx o) {
            super.update(o);
            OrgData d = (OrgData)o;

            TextView tv = itemView.findViewById(R.id.time);
            tv.setText(sdf.format(d.schedule.date));

            tv = itemView.findViewById(R.id.comment);

            if (d.schedule.remark.length() > 0){
                tv.setText(d.schedule.remark);
                tv.setVisibility(View.VISIBLE);
            }else
                tv.setVisibility(View.GONE);
        }
    }
    public ScheduleAdapter(MainActivity context) {
        super(context);
    }

    @NonNull
    @Override
    public OrgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_schedule_row, parent, false);
        return new ScheduleHolder(this, v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgHolder holder, int position) {
        holder.update(orgs.get(position));
    }

    @Override
    protected List<? extends OrgEx> getOrgs() {
        List<OrgEx> res = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(Util.resetTime(model.getWorkingDate().getValue()));
        Date t = c.getTime();
        c.add(Calendar.DATE, 1);
        DbWriter.checkDBTable(OrgSchedule.class);
        String where = String.format("date >= %d and date < %d", t.getTime(), c.getTimeInMillis());
        DbReader.fetch(OrgSchedule.class, where).forEach(s->{
            OrgImpl org = new OrgImpl();
            org.read("id", s.id);
            OrgData o = new OrgData((OrgEx) org.getData());
            o.schedule = s;
            res.add(o);
        });

        res.sort(Comparator.comparing(o->((OrgData)o).schedule.date));

        return res;
    }
}
