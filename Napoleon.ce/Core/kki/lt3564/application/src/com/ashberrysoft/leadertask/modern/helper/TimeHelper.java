package com.ashberrysoft.leadertask.modern.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.BroadcastAction;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.R.id.date;

public class TimeHelper {

    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone(SharedStrings.GMT);

    public static final SimpleDateFormat SDF_DAY_MONTH = getSimpleDateFormat("dd MMM");
    public static final SimpleDateFormat SDF_12H = getSimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat SDF_24H = getSimpleDateFormat("HH:mm");

    private static SimpleDateFormat getSimpleDateFormat(String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeHelper.DEFAULT_TIME_ZONE);

        return sdf;
    }

    public static final int DEFAULT_BEGIN_HOUR_OF_DAY = 0;
    public static final int DEFAULT_BEGIN_MINUTE = 0;
    public static final int DEFAULT_BEGIN_SECOND = 0;
    public static final int DEFAULT_BEGIN_MILLISECOND = 0;

    public static final int DEFAULT_END_HOUR_OF_DAY = 23;
    public static final int DEFAULT_END_MINUTE = 59;
    public static final int DEFAULT_END_SECOND = 59;
    public static final int DEFAULT_END_MILLISECOND = 999;

    // SINGLETON
    private static TimeHelper sTimeHelper;

    // BASE
    private final Context mContext;

    // VALUE's
    private final Calendar mCalendar;
    private final Date mDate;
    private final Timer mTimer;
    private final StringBuilder mStringBuilder;
    public long lastCheckedTime;

    public static void init(Context context) {
        if (sTimeHelper == null) {
            sTimeHelper = new TimeHelper(context);
        }
    }

    public static TimeHelper getInstance() {
        return sTimeHelper;
    }

    private TimeHelper(Context context) {
        mContext = context.getApplicationContext();

        mCalendar = Calendar.getInstance(DEFAULT_TIME_ZONE);
        mDate = new Date();
        mTimer = new Timer(TimeHelper.class.getSimpleName());
        mStringBuilder = new StringBuilder();
        lastCheckedTime = currentTimeMillisWithoutTimeZone();
        sheduleMidnightBroadcast();
    }

    public void sheduleMidnightBroadcast() {
        mTimer.schedule(new MidnightTask(mContext), getMidnightDate());
    }

    public Date getMidnightDate() {
        synchronized (mCalendar) {
            try {
                mCalendar.setTimeZone(TimeZone.getDefault());
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                roundCalendar(mCalendar, false);

                final Date date = mCalendar.getTime();
                Utils.toLog("getMidnightDate = " + date);

                return date;

            } finally {
                mCalendar.setTimeZone(DEFAULT_TIME_ZONE);
            }
        }
    }

    private static final class MidnightTask extends TimerTask {

        static final Intent INTENT = new Intent(BroadcastAction.MIDNIGHT_NOTIFY);
        final LocalBroadcastManager mBroadcastManager;

        public MidnightTask(Context context) {
            mBroadcastManager = LocalBroadcastManager.getInstance(context);
        }

        @Override
        public void run() {
            mBroadcastManager.sendBroadcast(INTENT);
            TimeHelper.getInstance().sheduleMidnightBroadcast();
        }
    }

    public static Calendar roundCalendar(Calendar calendar, boolean toBegin) {
        if (toBegin) {
            calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_BEGIN_HOUR_OF_DAY);
            calendar.set(Calendar.MINUTE, DEFAULT_BEGIN_MINUTE);
            calendar.set(Calendar.SECOND, DEFAULT_BEGIN_SECOND);
            calendar.set(Calendar.MILLISECOND, DEFAULT_BEGIN_MILLISECOND);

        } else {
            calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_END_HOUR_OF_DAY);
            calendar.set(Calendar.MINUTE, DEFAULT_END_MINUTE);
            calendar.set(Calendar.SECOND, DEFAULT_END_SECOND);
            calendar.set(Calendar.MILLISECOND, DEFAULT_END_MILLISECOND);
        }
        return calendar;
    }

    public String getCuteDateTitleS(Date date) {
        Calendar c = Calendar.getInstance();
        //c.setTimeZone(TimeZone.getDefault());
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
        Utils.clearStringBuilder(mStringBuilder);
        final String nearestDay = getNearestDay(date.getTime(), true);
        if (nearestDay != null) {
            mStringBuilder.append(nearestDay);
            mStringBuilder.append(SharedStrings.COLON_C);
            mStringBuilder.append(SharedStrings.SPACE_C);
            mStringBuilder.append(getCuteDayMonth(date));
            mStringBuilder.append(SharedStrings.COMMA_C);
            mStringBuilder.append(SharedStrings.SPACE_C);
            mStringBuilder.append(dateW);
        } else {
            mStringBuilder.append(getCuteDayMonth(date));
            mStringBuilder.append(SharedStrings.COMMA_C);
            mStringBuilder.append(SharedStrings.SPACE_C);
            mStringBuilder.append(dateW);
        }
        return mStringBuilder.toString();
    }

    public String getCuteDateTitleForCalendar(Date date, boolean oneDayMode) {
        Calendar c = Calendar.getInstance();
        //c.setTimeZone(TimeZone.getDefault());
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
        Utils.clearStringBuilder(mStringBuilder);
        final String nearestDay = getNearestDay(date.getTime(), true);
        //
        if (nearestDay != null) {
            if (oneDayMode) {
                mStringBuilder.append(nearestDay);
                mStringBuilder.append(SharedStrings.COLON_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(getCuteDayMonth(date));
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(dateW);
            } else {
                if (nearestDay.equals(mContext.getString(R.string.task_today))) {
                    mStringBuilder.append(nearestDay);
                } else {
                    mStringBuilder.append(getCuteDayMonth(date));
                    mStringBuilder.append(SharedStrings.COMMA_C);
                    mStringBuilder.append(SharedStrings.SPACE_C);
                    mStringBuilder.append(dateW);
                }
            }
        } else {
            mStringBuilder.append(getCuteDayMonth(date));
            mStringBuilder.append(SharedStrings.COMMA_C);
            mStringBuilder.append(SharedStrings.SPACE_C);
            mStringBuilder.append(dateW);
        }
        //
        return mStringBuilder.toString();
    }

    public String getCuteDateTitle(Date date) {
        try {
            Calendar c = Calendar.getInstance();
            //c.setTimeZone(TimeZone.getDefault());
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
            Utils.clearStringBuilder(mStringBuilder);
            final String nearestDay = getNearestDayS(date.getTime(), false);
            if (nearestDay != null) {
                mStringBuilder.append(nearestDay);
                mStringBuilder.append(SharedStrings.COLON_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(getCuteDayMonth(date));
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(dateW);
            } else {
                mStringBuilder.append(getCuteDayMonth(date));
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(dateW);
            }
            return mStringBuilder.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String getNearestDay(long date, boolean currentTimeZone) {
        synchronized (mCalendar) {
            try {
                if (currentTimeZone) {
                    mCalendar.setTimeZone(TimeZone.getDefault());
                }

                mCalendar.setTimeInMillis(date);

                final int dateYear = mCalendar.get(Calendar.YEAR);
                final int dateMonth = mCalendar.get(Calendar.MONTH);
                final int dateMonthDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                Date tmpDate = new Date();
                tmpDate.setHours(tmpDate.getHours());
                if (dateYear == tmpDate.getYear() + 1900 && dateMonth == tmpDate.getMonth()) {
                    final int monthDay = tmpDate.getDate();
                    if (dateMonthDay == monthDay) {
                        return mContext.getString(R.string.task_today);

                    } else if (dateMonthDay == monthDay + 1) {
                        return mContext.getString(R.string.task_tomorrow);

                    } else if (dateMonthDay == monthDay - 1) {
                        return mContext.getString(R.string.task_yesterday);
                    }
                }
                return null;

            } finally {
                if (currentTimeZone) {
                    mCalendar.setTimeZone(DEFAULT_TIME_ZONE);
                }
            }
        }
    }

    public String getNearestDayS(long date, boolean currentTimeZone) {
        synchronized (mCalendar) {
            try {
                if (currentTimeZone) {
                    mCalendar.setTimeZone(TimeZone.getDefault());
                }

                mCalendar.setTimeInMillis(date);

                final int dateYear = mCalendar.get(Calendar.YEAR);
                final int dateMonth = mCalendar.get(Calendar.MONTH);
                final int dateMonthDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                Date tmpDate = new Date();
                tmpDate.setHours(tmpDate.getHours()+tmpDate.getTimezoneOffset()/60);
                if (dateYear == tmpDate.getYear() + 1900 && dateMonth == tmpDate.getMonth()) {
                    final int monthDay = tmpDate.getDate();
                    if (dateMonthDay == monthDay) {
                        return mContext.getString(R.string.task_today);

                    } else if (dateMonthDay == monthDay + 1) {
                        return mContext.getString(R.string.task_tomorrow);

                    } else if (dateMonthDay == monthDay - 1) {
                        return mContext.getString(R.string.task_yesterday);
                    }
                }
                return null;

            } finally {
                if (currentTimeZone) {
                    mCalendar.setTimeZone(DEFAULT_TIME_ZONE);
                }
            }
        }
    }

    public String getCuteDayMonth(Date date) {
        String[] Monthes = mContext.getResources().getStringArray(R.array.months_full);
        String Month = Monthes[date.getMonth()].substring(0, 3);
        Month = Month.toLowerCase();
        return date.getDate() + " " + Month;
    }


    public String getDifferencesDateInDays(long dateBig, long date) {
        return " ("+((int)((dateBig/(24*60*60*1000)) -(int)(date/(24*60*60*1000))))+" "+mContext.getString(R.string.days)+")";
    }

    public int getIntDifferencesDateInDays(long dateBig, long date) {
        return ((int)((dateBig/(24*60*60*1000)) -(int)(date/(24*60*60*1000))));
    }


    public boolean isWholeDayTask(LTask task, boolean isPerformer) {
        synchronized (mCalendar) {
            final long termBegin;
            final long termEnd;

            if (isPerformer) {
                termBegin = task.getTermBegin();
                termEnd = task.getTermEnd();

            } else {
                termBegin = task.getTermBeginCustomer();
                termEnd = task.getTermEndCustomer();
            }

            mCalendar.setTimeInMillis(termBegin);
            final int beginYear = mCalendar.get(Calendar.YEAR);
            final int beginDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

            mCalendar.setTimeInMillis(termEnd);
            final int endYear = mCalendar.get(Calendar.YEAR);
            final int endDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

            if (beginYear == endYear && beginDayOfYear == endDayOfYear) {
                mCalendar.setTimeInMillis(termBegin);
                if (equalsStandart(mCalendar, true)) {
                    mCalendar.setTimeInMillis(termEnd);
                    return equalsStandart(mCalendar, false);
                }
            }
            return false;
        }
    }

    public boolean isWholeDayTask(long termBegin, long termEnd) {
        synchronized (mCalendar) {

            mCalendar.setTimeInMillis(termBegin);
            final int beginYear = mCalendar.get(Calendar.YEAR);
            final int beginDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

            mCalendar.setTimeInMillis(termEnd);
            final int endYear = mCalendar.get(Calendar.YEAR);
            final int endDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

            if (beginYear == endYear && beginDayOfYear == endDayOfYear) {
                mCalendar.setTimeInMillis(termBegin);
                if (equalsStandart(mCalendar, true)) {
                    mCalendar.setTimeInMillis(termEnd);
                    return equalsStandart(mCalendar, false);
                }
            }
            return false;
        }
    }

    public boolean isSomeDaysTask(LTask task, boolean isPerformer) {
        synchronized (mCalendar) {
            final long termBegin;
            final long termEnd;

            if (isPerformer) {
                termBegin = task.getTermBegin();
                termEnd = task.getTermEnd();

            } else {
                termBegin = task.getTermBeginCustomer();
                termEnd = task.getTermEndCustomer();
            }

            mCalendar.setTimeInMillis(termBegin);
            final int beginYear = mCalendar.get(Calendar.YEAR);
            final int beginDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

            mCalendar.setTimeInMillis(termEnd);
            final int endYear = mCalendar.get(Calendar.YEAR);
            final int endDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

            if (beginYear == endYear && endDayOfYear - beginDayOfYear > 1 ) {
                mCalendar.setTimeInMillis(termBegin);
                if (equalsStandart(mCalendar, true)) {
                    mCalendar.setTimeInMillis(termEnd);
                    return equalsStandart(mCalendar, false);
                }
            }
            return false;
        }
    }

    public static boolean equalsStandart(Calendar calendar, boolean startOfDay) {
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);

        if (startOfDay) {
            return hourOfDay == DEFAULT_BEGIN_HOUR_OF_DAY && //
                    minute == DEFAULT_BEGIN_MINUTE && second == DEFAULT_BEGIN_SECOND;

        } else {
            return hourOfDay == DEFAULT_END_HOUR_OF_DAY && //
                    minute == DEFAULT_END_MINUTE && second == DEFAULT_END_SECOND;
        }
    }

    public static boolean termsEquals(LTask one, LTask second) {
        return one.getTermBegin() == second.getTermBegin() && //
                one.getTermEnd() == second.getTermEnd() && //
                one.getTermBeginCustomer() == second.getTermBeginCustomer() && //
                one.getTermEndCustomer() == second.getTermEndCustomer();
    }

    public String taskTermFormatter(LTask task, boolean isPerformer, boolean needToShowBeginAndEnd) {
        synchronized (mCalendar) {
            Utils.clearStringBuilder(mStringBuilder);

            final long termBegin;
            final long termEnd;

            if (isPerformer) {
                termBegin = task.getTermBegin();
                termEnd = task.getTermEnd();

            } else {
                termBegin = task.getTermBeginCustomer();
                termEnd = task.getTermEndCustomer();
            }

            if (termBegin != 0 && termEnd != 0) {
                final boolean equalsDates;
                {
                    mCalendar.setTimeInMillis(termBegin);
                    final int beginYear = mCalendar.get(Calendar.YEAR);
                    final int beginDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

                    mCalendar.setTimeInMillis(termEnd);
                    final int endYear = mCalendar.get(Calendar.YEAR);
                    final int endDayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);

                    equalsDates = beginYear == endYear && beginDayOfYear == endDayOfYear;
                }
                {
                    //
                    /*StringBuilder mStringBuilder = new StringBuilder();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(termBegin));
                    final String day = getNearestDayNew(termBegin, true, calendar, mContext);*/
                    //
                    final String day = getNearestDay(termBegin, false);
                    if (day == null || !equalsDates) {
                        mDate.setTime(termBegin);
                        mDate.setHours(mDate.getHours()+mDate.getTimezoneOffset()/60);
                        mStringBuilder.append(getCuteDayMonth(mDate));

                    } else {
                        mStringBuilder.append(day);
                    }
                }

                if (equalsDates) {
                    if (!isWholeDayTask(task, isPerformer)) {
                        mStringBuilder.append(SharedStrings.COMMA_C);
                        mStringBuilder.append(SharedStrings.SPACE_C);

                        SimpleDateFormat sdf = DateFormat.is24HourFormat(mContext) ? SDF_24H : SDF_12H;
                        mDate.setTime(termBegin);
                        mStringBuilder.append(sdf.format(mDate));
                        if (needToShowBeginAndEnd) {
                            if (termEnd != termBegin) {
                                mStringBuilder.append("-");
                                sdf = DateFormat.is24HourFormat(mContext) ? SDF_24H : SDF_12H;
                                mDate.setTime(termEnd);
                                mStringBuilder.append(sdf.format(mDate));
                            }
                        }
                    }

                } else {
                    mStringBuilder.append(SharedStrings.SPACE_C);
                    mStringBuilder.append(SharedStrings.MINUS_C);
                    mStringBuilder.append(SharedStrings.SPACE_C);
                    mDate.setTime(termEnd);
                    mStringBuilder.append(SDF_DAY_MONTH.format(mDate));

                }
            }

            return mStringBuilder.toString();
        }
    }

    public static long currentTimeMillisWithoutTimeZone() {
        return dellTimeZone(System.currentTimeMillis());
    }

    public static long dellTimeZone(long date) {
        long dailyTime = 0;
        final TimeZone timeZone = TimeZone.getDefault();
        if(TimeZone.getDefault().inDaylightTime(new Date(date))) {
            dailyTime = timeZone.getDSTSavings();
        }
        final long answer = date + timeZone.getRawOffset() + dailyTime;

        return answer;
    }

    public static long addTimeZone(long date) {
        long dailyTime = 0;
        final TimeZone timeZone = TimeZone.getDefault();
        if(TimeZone.getDefault().inDaylightTime(new Date(date))) {
            dailyTime = timeZone.getDSTSavings();
        }
        final long answer = date - timeZone.getRawOffset() - dailyTime;

        return answer;
    }

    public boolean isToday(long time) {
        synchronized (mCalendar) {
            mCalendar.setTimeInMillis(time);

            final int year = mCalendar.get(Calendar.YEAR);
            final int month = mCalendar.get(Calendar.MONTH);
            final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

            mCalendar.setTimeInMillis(currentTimeMillisWithoutTimeZone());

            return year == mCalendar.get(Calendar.YEAR) && //
                    month == mCalendar.get(Calendar.MONTH) && //
                    dayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    public void setLastTimeEveryMinute(long time) {
        lastCheckedTime = time;
    }

    public String getSimpleDate(Date date) {
        Utils.clearStringBuilder(mStringBuilder);

        mCalendar.setTimeInMillis(date.getTime());

        final int year = mCalendar.get(Calendar.YEAR);
        final int month = mCalendar.get(Calendar.MONTH)+1;
        final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        mStringBuilder.append(dayOfMonth < 10 ? "0"+dayOfMonth : dayOfMonth);
        mStringBuilder.append(SharedStrings.DOT_C);
        mStringBuilder.append(month < 10 ? "0"+month : month);
        mStringBuilder.append(SharedStrings.DOT_C);
        mStringBuilder.append(year);

        return mStringBuilder.toString();
    }

    public static String getDateString(Date date, Calendar calendar, Context context) {
        StringBuilder mStringBuilder = new StringBuilder();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        final String nearestDay = getNearestDay(date.getTime(), true, calendar, context);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (nearestDay != null) {
            mStringBuilder.append(nearestDay);
            mStringBuilder.append(SharedStrings.COMMA_C);
            mStringBuilder.append(SharedStrings.SPACE_C);
            mStringBuilder.append(sdf.format(date));
        }
        else
        {
            mStringBuilder.append(getCuteDayMonthNew(date));
            mStringBuilder.append(SharedStrings.COMMA_C);
            mStringBuilder.append(SharedStrings.SPACE_C);
            mStringBuilder.append(sdf.format(date));
        }
        return mStringBuilder.toString();
    }

    private static String getNearestDay(long date, boolean currentTimeZone, Calendar calendar, Context context) {
        synchronized (calendar) {
            try {
                if (currentTimeZone) {
                    calendar.setTimeZone(TimeZone.getDefault());
                }

                calendar.setTimeInMillis(date);

                final int dateYear = calendar.get(Calendar.YEAR);
                final int dateMonth = calendar.get(Calendar.MONTH);
                final int dateMonthDay = calendar.get(Calendar.DAY_OF_MONTH);
                int monthDay;
                calendar.setTimeInMillis(processTimeZone(System.currentTimeMillis(), true));
                Date tmpDate = new Date();
                if (dateYear == tmpDate.getYear()+1900 && dateMonth == tmpDate.getMonth()) {
                    monthDay = tmpDate.getDate();
                    if (dateMonthDay == monthDay) {
                        return context.getString(R.string.task_today_lib);
                    }
                    else if (dateMonthDay == monthDay - 1) {
                        return context.getString(R.string.task_yesterday_lib);

                    }
                }
                return null;

            } finally {
                if (currentTimeZone) {
                    calendar.setTimeZone(TimeZone.getTimeZone("GTM"));
                }
            }
        }
    }

    public static long processTimeZone(long date, boolean addTimeZone) {
        final TimeZone timeZone = TimeZone.getDefault();
        int bias = timeZone.getRawOffset();
        if(timeZone.useDaylightTime()) {
            bias +=timeZone.getDSTSavings();
        }
        final long answer = date + (addTimeZone ? 1 : -1) * bias;
        return answer;
    }

    private static String getCuteDayMonthNew(Date date) {
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(date);
        return date.getDate()+" "+month_name.toLowerCase();
    }

    public String getDateForSyncOrSimple(Date date, Calendar calendar, Context context, boolean onlyDate, boolean needTime) {
        StringBuilder mStringBuilder = new StringBuilder();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        final String nearestDay = getNearestDayNew(date.getTime(), true, calendar, context);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (nearestDay != null) {
            if(!onlyDate) {
                mStringBuilder.append(context.getString(R.string.updated));
                mStringBuilder.append(SharedStrings.COLON_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
            }
            mStringBuilder.append(nearestDay);
            if (needTime) {
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(sdf.format(date));
            }
        }
        else
        {
            if(!onlyDate) {
                mStringBuilder.append(context.getString(R.string.updated));
                mStringBuilder.append(SharedStrings.COLON_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
            }
                mStringBuilder.append(getCuteDayMonthNew(date));
            if (needTime) {
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(sdf.format(date));
            }
        }
        return mStringBuilder.toString();
    }

    public String getDateForSyncOrSimple2(Date date, Calendar calendar, Context context, boolean onlyDate, boolean needTime) {
        StringBuilder mStringBuilder = new StringBuilder();
        Calendar c = Calendar.getInstance(DEFAULT_TIME_ZONE);
        c.setTime(date);
        final String nearestDay = getNearestDay(date.getTime(), true);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (nearestDay != null) {
            if(!onlyDate) {
                mStringBuilder.append(context.getString(R.string.updated));
                mStringBuilder.append(SharedStrings.COLON_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
            }
            mStringBuilder.append(nearestDay);
            if (needTime) {
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(sdf.format(date));
            }
        }
        else
        {
            if(!onlyDate) {
                mStringBuilder.append(context.getString(R.string.updated));
                mStringBuilder.append(SharedStrings.COLON_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
            }
            mStringBuilder.append(getCuteDayMonthNew(date));
            if (needTime) {
                mStringBuilder.append(SharedStrings.COMMA_C);
                mStringBuilder.append(SharedStrings.SPACE_C);
                mStringBuilder.append(sdf.format(date));
            }
        }
        return mStringBuilder.toString();
    }

    public static String getNearestDayNew(long date, boolean currentTimeZone, Calendar calendar, Context context) {
        synchronized (calendar) {
            try {
                if (currentTimeZone) {
                    calendar.setTimeZone(TimeZone.getDefault());
                }

                Date tmpDate2 = new Date(date);
                tmpDate2.setHours(tmpDate2.getHours()+tmpDate2.getTimezoneOffset()/60);

                calendar.setTimeInMillis(tmpDate2.getTime());

                final int dateYear = calendar.get(Calendar.YEAR);
                final int dateMonth = calendar.get(Calendar.MONTH);
                final int dateMonthDay = calendar.get(Calendar.DAY_OF_MONTH);
                int monthDay;
                calendar.setTimeInMillis(processTimeZone(System.currentTimeMillis(), true));
                Date tmpDate = new Date();
                if (dateYear == tmpDate.getYear()+1900 && dateMonth == tmpDate.getMonth()) {
                    monthDay = tmpDate.getDate();
                    if (dateMonthDay == monthDay) {
                        return context.getString(R.string.task_today_lib);
                    } else if (dateMonthDay == monthDay - 1) {
                        return context.getString(R.string.task_yesterday_lib);

                    } else if (dateMonthDay == monthDay + 1) {
                        return context.getString(R.string.task_tomorrow);

                    }
                }
                return null;

            } finally {
                if (currentTimeZone) {
                    calendar.setTimeZone(TimeZone.getTimeZone("GTM"));
                }
            }
        }
    }


}