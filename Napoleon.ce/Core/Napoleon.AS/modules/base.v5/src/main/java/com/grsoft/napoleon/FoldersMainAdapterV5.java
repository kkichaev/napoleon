package com.grsoft.napoleon;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Schedule;
import com.grsoft.dataobjects.ScheduleItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoldersMainAdapterV5 extends BaseMainAdapter
implements Main.MainAdapter, FilterAdapter {
    private Main main;
    public List<Schedule> data = new ArrayList<>();
    public Schedule current = null;
    public List<String> filtered = null;
    public OrgImpl org = new OrgImpl();
    public SimpleDateFormat weekDayName = new SimpleDateFormat("EEEE");

    public static final int DAY_ITEM_ID = 0;
    public static final int ROUTE_ITEM_ID = 1;
    public static final int FILTER_ITEM_ID = 2;

    public FoldersMainAdapterV5(Main main){
        this.main = main;
    }

    @Override
    void reload() {
        current = null;
        data.clear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        Date start = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, 7);

        String where = String.format("date>=%d and date<=%d",start.getTime(), cal.getTimeInMillis());
        DbReader.fetch(Schedule.class, where, "date").forEach((s)->{data.add(s);});
    }

    @Override
    public Org getOrg(int pos) {
        return null;
    }

    @Override
    public int getCount() {
        if (filtered != null)
            return filtered.size();
        else if (current == null)
            return data.size();
        else
            return data.get(data.indexOf(current)).items.size();
    }

    @Override
    public Object getItem(int position) {
        if (filtered != null)
            return filtered.get(position);
        else if (current == null)
            return data.get(position);
        else
            return data.get(data.indexOf(current)).items.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (filtered != null)
            return  FILTER_ITEM_ID;
        else if (current == null)
            return DAY_ITEM_ID;
        else
            return ROUTE_ITEM_ID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        long id = getItemId(position);

        if (id == FILTER_ITEM_ID)
            return getFilterView(position, convertView);
        else if (id == DAY_ITEM_ID)
            return getDayView(position, convertView);
        else
            return getRouteView(position, convertView);
    }

    private View getFilterView(int position, View convertView) {
        org.read("id", ((String)getItem(position)));
        convertView =  main.getSolidMainView(org.getData(), position, convertView);
        return convertView;
    }

    private View getRouteView(int position, View convertView) {
        org.read("id", ((ScheduleItem)getItem(position)).id);
        convertView =  main.getSolidMainView(org.getData(), position, convertView);
        return convertView;
    }


    private FoldersMainAdapter.ViewData createViewData(int position) {
        Schedule sc = (Schedule) getItem(position);
        FoldersMainAdapter.ViewData res = new FoldersMainAdapter.ViewData();
        res.name = weekDayName.format(sc.date);
        return res;
    }

    private View getDayView(int position, View convertView) {
        convertView = main.getFolderMainView(convertView, position, createViewData(position));
        ImageView ivFolder = (ImageView) convertView.findViewById(R.id.ivFolder);
        ivFolder.setImageResource(isToday(position) ? R.drawable.folder_open : R.drawable.folder);
        ivFolder.setVisibility(View.VISIBLE);
        return convertView;
    }

    private boolean isToday(int position) {
        return Util.getDate().getTime() == ((Schedule)getItem(position)).date.getTime();
    }

    @Override
    public void adjustView() {
        View v = main.findViewById(R.id.ivGoUp);
        if(v != null) {
            v.setVisibility(current == null ? View.GONE : View.VISIBLE);
            v.setOnClickListener((w)->{
                current = null;
                notifyDataSetChanged();
                adjustView();
                main.resetFind();
            });
        }

        v = main.findViewById(R.id.btnMode);

        if(v != null)
            ((ImageView)v).setImageResource(R.drawable.route);

        if(current == null){
            v = main.findViewById(R.id.tvFirstColumnCaption);

            if(v != null){
                ((TextView)v).setText(main.getString(R.string.Day_of_week));
            }

            v = main.findViewById(R.id.tvMainDocValColTitle);

            if(v != null)
                ((TextView)v).setText(main.getString(R.string.Clients_of));
        }else {
            DocType.getCurDoc().viewOpened(main);
            TextView tv = (TextView)main.findViewById(R.id.tvFirstColumnCaption);
            if( tv != null )
                tv.setText(weekDayName.format(current.date));
        }

        main.onAdapterViewAdjusted();
    }

    @Override
    public void click(int position) {
        switch ((int) getItemId(position)){
            case DAY_ITEM_ID: dayClick(position); break;
            case FILTER_ITEM_ID:
            case ROUTE_ITEM_ID: routeClick(position); break;
        }
    }

    private void routeClick(int position) {
        ScheduleItem i = (ScheduleItem) getItem(position);
        org.read("id", i.id);
        main.openOrg((Org) org.getData(), position);
    }

    private void dayClick(int position) {
        current = (Schedule) getItem(position);
        notifyDataSetChanged();
        adjustView();
    }

    public void applyFilterLow(String value) {
        if (value.length() == 0) {
            if (filtered != null)
                filtered = null;
            return;
        }

        if (filtered == null)
            filtered = new ArrayList<>();
        else
            filtered.clear();

        List<String> ids = new ArrayList<>();

        if (current == null)
            for (int i = 0; i < data.size(); i++) {
                data.get(i).items.forEach((x)->ids.add(x.id));
                processItems(ids, value);
            }
        else
            current.items.forEach((x)->ids.add(x.id));
            processItems(ids, value);
    }

    protected void processItems(List<String> ids, String value) {
        for (int y = 0; y < ids.size(); y++) {
            String id = ids.get(y);

            if (filtered.contains(id))
                continue;

            org.getData().id = id;

            if (!org.read())
                continue;

            if (org.getData().srchName.contains(value.toUpperCase()))
               filtered.add(id);
        }
    }

    @Override
    public void applyFilter(String value) {
        applyFilterLow(value);
        super.notifyDataSetChanged();
    }

    @Override
    public void resetFilter() {
        filtered = null;
        notifyDataSetChanged();
    }
}
