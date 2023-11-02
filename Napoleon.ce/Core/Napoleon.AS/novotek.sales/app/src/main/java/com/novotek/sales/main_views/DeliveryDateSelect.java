package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.sales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeliveryDateSelect extends BaseView {
    final static int MAX_DAYS = 10;
    final static int START_HOUR = 11;
    final static int FINAL_HOUR = 21;
    static final String TAG = DeliveryDateSelect.class.toString();

    Date selected;
    RecyclerView dateView;
    DateAdapter dateAdapter;
    List<Date> dates = new ArrayList<>();

    int selectedHour;
    List<Integer> hours = new ArrayList<>();
    RecyclerView timeView;
    HourAdapter hourAdapter;

    @Override
    protected int getResourceId() {
        return R.layout.delivery_date;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());

        long time = startDay(new Date()).getTime();
        for(int i = 0; i < MAX_DAYS; i++) {
            time += 24 * 3600 * 1000;
            dates.add(new Date(time));
        }

        for(int i=START_HOUR; i<FINAL_HOUR; i++) {
            hours.add(i);
        }

        selected = startDay(model.getBasket().dlvDate);
        selectedHour = getAvailHour(model.getBasket().dlvDate);

        dateView = v.findViewById(R.id.date);
        dateAdapter = new DateAdapter();
        dateView.setAdapter(dateAdapter);
        dateView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        dateView.getLayoutManager().scrollToPosition(dates.indexOf(selected));

        v.findViewById(R.id.doButton).setOnClickListener(view ->{
            Calendar c = Calendar.getInstance();
            c.setTime(selected);
            c.set(Calendar.HOUR_OF_DAY, selectedHour);

            model.setDeliveryDate(c.getTime());
            getParentFragmentManager().popBackStack();
        });

        timeView = v.findViewById(R.id.time);
        hourAdapter = new HourAdapter();
        timeView.setAdapter(hourAdapter);
        timeView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        timeView.getLayoutManager().scrollToPosition(hours.indexOf(selectedHour));
        return v;
    }

    public static int getAvailHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int selectedHour = c.get(Calendar.HOUR_OF_DAY);
        if(selectedHour < START_HOUR)
            selectedHour = START_HOUR;
        if(selectedHour >= FINAL_HOUR)
            selectedHour = FINAL_HOUR - 1;

        return selectedHour;
    }

    public static Date startDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    void selectDay(Date newDay) {
        dateAdapter.notifyItemChanged(dates.indexOf(selected), selected);
        selected = newDay;
        dateAdapter.notifyItemChanged(dates.indexOf(newDay), newDay);
    }

    private void selectHour(int hour) {
        hourAdapter.notifyItemChanged(hours.indexOf(selectedHour), selectedHour);
        selectedHour = hour;
        hourAdapter.notifyItemChanged(hours.indexOf(selectedHour), selectedHour);
    }

    class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateHolder> {

        class DateHolder extends RecyclerView.ViewHolder {

            Date now, tomorrow;
            public DateHolder(@NonNull View itemView) {
                super(itemView);

                now = startDay(new Date());
                tomorrow = new Date(now.getTime() + 24 * 3600 * 1000);
            }

            void update(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");

                boolean isToday = date.equals(now);
                boolean isTomorrow = date.equals(tomorrow);

                String text = isToday ? "Сегодня, " :
                        isTomorrow ? "Завтра, " :
                                "";

                TextView tv = ((TextView)itemView.findViewById(R.id.name));
                tv.setText(text + sdf.format(date));
                tv.setOnClickListener(view -> selectDay(date));

                boolean isSel = selected.equals(date);
                tv.setBackgroundColor(getResources().getColor(isSel ? R.color.colorPrimary : R.color.gray_bg, null));
                tv.setTextColor(getResources().getColor(isSel ? R.color.white : R.color.black, null));
            }
        }

        @NonNull
        @Override
        public DateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.date_tile, parent, false);
            return new DateHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DateHolder holder, int position) {
            holder.update(dates.get(position));
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }
    }

    class HourAdapter extends RecyclerView.Adapter<HourAdapter.Holder> {
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.delivery_date_hour_tille, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.update(hours.get(position));
        }

        @Override
        public int getItemCount() {
            return hours.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            public Holder(@NonNull View itemView) {
                super(itemView);
            }

            public void update(int hour) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hour);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:00");
                String text = sdf.format(c.getTime());
                c.set(Calendar.HOUR_OF_DAY, hour + 1);
                text += " - " + sdf.format(c.getTime());

                RadioButton rb = itemView.findViewById(R.id.name);
                rb.setText(text);

                rb.setBackgroundColor(getResources().getColor(hour == selectedHour  ? R.color.colorPrimary :
                        R.color.gray_bg, null));

                rb.setTextColor(getResources().getColor(hour == selectedHour ? R.color.white :
                        R.color.black, null));
                
                rb.setOnClickListener(view -> selectHour(hour));
                rb.setChecked(hour == selectedHour);
            }
        }
    }
}
