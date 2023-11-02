package com.ashberrysoft.leadertask;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
    static final String GMT = "GMT";
    static final char COMMA_C = ',';
    static final char SPACE_C = ' ';

    // BASE
    private final Context mContext;


    public static TimeHelper getInstance(Context context) {
        return new TimeHelper(context);
    }

    private TimeHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public String getCuteDateTitle(Date date) {
        StringBuilder mStringBuilder = new StringBuilder();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            String dateW = null;
            switch (dayOfWeek) //в тайм хелпер в отдельную функцию
            {
                case 1:
                    dateW = mContext.getResources().getString(R.string.Su);
                    break;
                case 2:
                    dateW = mContext.getResources().getString(R.string.Mo);
                    break;
                case 3:
                    dateW = mContext.getResources().getString(R.string.Tu);
                    break;
                case 4:
                    dateW = mContext.getResources().getString(R.string.We);
                    break;
                case 5:
                    dateW = mContext.getResources().getString(R.string.Th);
                    break;
                case 6:
                    dateW = mContext.getResources().getString(R.string.Fr);
                    break;
                case 7:
                    dateW = mContext.getResources().getString(R.string.Sa);
                    break;
            }
            dateW = dateW.toLowerCase();
            mStringBuilder.append(getCuteDayMonth(date));
            mStringBuilder.append(COMMA_C);
            mStringBuilder.append(SPACE_C);
            mStringBuilder.append(dateW);
        }catch(Exception e){
            e.printStackTrace();
        }

        return mStringBuilder.toString();
    }

    public String getCuteDayMonth(Date date) {
        String res = "";

        try {
            String[] Monthes = mContext.getResources().getStringArray(R.array.months_full);
            String Month = Monthes[date.getMonth()].substring(0, 3);
            Month = Month.toLowerCase();
            res = date.getDate() + " " + Month;
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

}