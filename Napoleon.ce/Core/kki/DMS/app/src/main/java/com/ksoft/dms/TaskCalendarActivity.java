package com.ksoft.dms;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ksoft.dms.database.controller.TaskController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskCalendarActivity extends AppCompatActivity {
    TextView tvMonth;
    Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskcalendar);
        RecyclerView list = findViewById(R.id.list);
        tvMonth = findViewById(R.id.tvMonth);

        adapter = new Adapter(this, tvMonth, new Date());
        list.setLayoutManager(new GridLayoutManager(this, 7));
        list.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.cal_clmn_spacing)));
        list.setAdapter(adapter);

        findViewById(R.id.prev).setOnClickListener((v)->{
            adapter.prevMonth();
        });
        findViewById(R.id.next).setOnClickListener((v)->{
            adapter.nextMonth();
        });

        adapter.action = new Adapter.AdapterAction() {
            @Override
            public void click(Adapter.ViewHolder v) {
                Intent i = new Intent(TaskCalendarActivity.this, Tasks.class);
                i.putExtra(Tasks.DATA, adapter.data.get(v.getAdapterPosition()));
                startActivity(i);
                finish();
            }
        };

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMMM yyyy");
        TextView tv = findViewById(R.id.tvToday);
        tv.setText(getString(R.string.today, sfd.format(Calendar.getInstance().getTime())));
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Date from;
        public List<Date> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("d");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM yyyy");
        TextView tvMonth;
        TaskController controller;
        private Context context;

        public AdapterAction action;

        interface  AdapterAction{
            void click(ViewHolder v);
        }

        public void prevMonth() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            cal.add(Calendar.MONTH, -1);
            from = cal.getTime();

            tvMonth.setText(sdfMonth.format(from));
            fillData();
            notifyDataSetChanged();
        }

        public void nextMonth() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            cal.add(Calendar.MONTH, 1);
            from = cal.getTime();

            tvMonth.setText(sdfMonth.format(from));
            fillData();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvDay;
            TextView tvTaskCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDay = itemView.findViewById(R.id.tvDay);
                tvTaskCount = itemView.findViewById(R.id.tvTask);

                if (action != null)
                    itemView.setOnClickListener((v)->action.click(this));
            }
        }

        public Adapter(Context context, TextView tvMonth, Date from){
            this.tvMonth = tvMonth;
            this.from = from;
            this.context = context;

            controller = new TaskController(context);
            tvMonth.setText(sdfMonth.format(from));
            fillData();
        }

        private void fillData() {
            data.clear();
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_WEEK) - 2));

            Date start = cal.getTime();
            cal = Calendar.getInstance();
            cal.setTime(from);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH,1);
            cal.add(Calendar.DAY_OF_MONTH,-1);
            Date end = cal.getTime();
            cal.setTime(start);

            while (cal.getTimeInMillis() < end.getTime()){
                data.add(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.taskcalendaritem, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder)holder).tvDay.setText(sdf.format(data.get(position)));
            ((ViewHolder) holder).tvDay.setTypeface(Typeface.DEFAULT);

            int tc = controller.getTaskCount(data.get(position));

            if (tc > 0){
                ((ViewHolder)holder).tvTaskCount.setVisibility(View.VISIBLE);
                ((ViewHolder)holder).tvTaskCount.setText(Integer.toString(tc));
            }else
                ((ViewHolder)holder).tvTaskCount.setVisibility(View.INVISIBLE);

            Date d = data.get(position);
            Calendar c = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance();
            c.setTime(d);

            int wd = c.get(Calendar.DAY_OF_WEEK);

            ((ViewHolder)holder).tvDay.setTextColor(context.getColor(R.color.black));

            if (wd == Calendar.SUNDAY || wd == Calendar.SATURDAY)
                ((ViewHolder)holder).tvDay.setTextColor(context.getColor(R.color.red));

            if (c.get(Calendar.DATE) == c1.get(Calendar.DATE)
                    && c.get(Calendar.YEAR) == c1.get(Calendar.YEAR)
                    && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH)) {
                ((ViewHolder) holder).tvDay.setTextColor(context.getColor(R.color.green));
                ((ViewHolder) holder).tvDay.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}
