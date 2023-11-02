package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Schedule;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleHitching extends ReportHitching{
    public ScheduleHitching() {
        super("make_schedule", new Param(), new Hitching(Schedule.class));
    }

//    @Override
//    protected String getCondition() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.clear(Calendar.MINUTE);
//        cal.clear(Calendar.SECOND);
//        cal.clear(Calendar.MILLISECOND);
//        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
//        SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
//        return String.format(" \"userid\" = '$CURRENT_USERID' and \"date\" >= ToDate('%s 00:00:00')", sdf.format(cal.getTime()));
//    }

    public static class Param extends DataObject {
        public String userid;
        public String start;
        public String finish;

        public Param() {
            Config c = ConfigManager.getConfig();
            userid = c.uuid;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -7);
            start = sdf.format(cal.getTime());

            cal.add(Calendar.DAY_OF_YEAR, 21);
            finish = sdf.format(cal.getTime());
        }
    }
}
