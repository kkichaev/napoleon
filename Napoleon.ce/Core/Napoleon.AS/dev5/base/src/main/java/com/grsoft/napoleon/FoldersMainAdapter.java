package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

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
import java.util.List;

public class FoldersMainAdapter extends BaseMainAdapter
implements Main.MainAdapter, FilterAdapter {
    private Main main;
    public List<Schedule> data = new ArrayList<>();
    public Schedule current = null;
    public List<ScheduleItem> filtered = null;
    public OrgImpl org = new OrgImpl();
    String filter = "";
    public SimpleDateFormat weekDayName = new SimpleDateFormat("EEEE");

    public static final int DAY_ITEM_ID = 0;
    public static final int ROUTE_ITEM_ID = 1;
    public static final int FILTER_ITEM_ID = 2;

    public boolean isTopLevel() { return current == null; }

    public Schedule getCurrent() { return current; }

    public FoldersMainAdapter(Main main){
        this.main = main;
        reload();
    }

    static class ViewData{
        String name;
        List<String> ids = new ArrayList<String>();
    }

    @Override
    void reload() {
//        current = null;
        data.clear();

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        Date start = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, 6);

        String where = String.format("date>=%d and date<=%d",start.getTime(), cal.getTimeInMillis());
        DbReader.fetch(Schedule.class, where, "date").forEach((s)->{data.add(s);});

        if(current != null) {
            Schedule sel = null;
            for(Schedule si : data) {
                if(si.date.equals(current.date)) {
                    sel = si;
                    break;
                }
            }
            current = sel;
        }
        applyFilter(filter);
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
        org.read("id", ((ScheduleItem)getItem(position)).id);
        convertView =  main.getSolidMainView(org.getData(), position, convertView);
        return convertView;
    }

    private View getRouteView(int position, View convertView) {
        org.read("id", ((ScheduleItem)getItem(position)).id);
        convertView =  main.getSolidMainView(org.getData(), position, convertView);
        return convertView;
    }


    private ViewData createViewData(int position) {
        Schedule sc = (Schedule) getItem(position);
        FoldersMainAdapter.ViewData res = new FoldersMainAdapter.ViewData();
        res.name = weekDayName.format(sc.date);
        sc.items.forEach(si -> res.ids.add(si.id));
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
        if (value == null || value.length() == 0) {
            if (filtered != null)
                filtered = null;
            return;
        }

        if (filtered == null)
            filtered = new ArrayList<>();
        else
            filtered.clear();

        if (current == null)
            for (int i = 0; i < data.size(); i++) {
//                data.get(i).items.forEach((x)->ids.add(x.id));
                processItems(data.get(i).items, value);
            }
        else {
//            current.items.forEach((x) -> ids.add(x.id));
            processItems(current.items, value);
        }
    }

    protected void processItems(List<ScheduleItem> ids, String value) {
        for(ScheduleItem si : ids) {
            String id = si.id;

            if (filtered.contains(si))
                continue;

            org.getData().id = id;

            if (!org.read())
                continue;

            if (org.getData().srchName.contains(value.toUpperCase()))
                filtered.add(si);
        }
    }

    @Override
    public void applyFilter(String value) {
        filter = value;
        applyFilterLow(value);
        super.notifyDataSetChanged();
    }

    @Override
    public void resetFilter() {
        filtered = null;
        notifyDataSetChanged();
    }
}
