package com.ashberrysoft.leadertask.modern.loader;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CalendarTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.domains.link.CalendarLink;
import com.ashberrysoft.leadertask.modern.helper.TaskLinkReset;
import com.ashberrysoft.leadertask.modern.helper.TaskSelectionBuilder;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.Utils;

public class CalendarDayLoader extends BaseLTaskLoader {

    private static final String DEFAULT_ORDER_CALENDAR = SelectionKeeper.//
            orderLimitOne(new StringBuilder(), CalendarLinkContract.TaskId, true).toString();

    private static final int FLAG_LOADED_TASKS = -1;
    private static final int FLAG_LOADED_FUTURE = -2;
    private static final String POSTFIX = "c";
  
    private final Long mDate;

    public CalendarDayLoader(Context context) {
        super(context, MenuItemType.CALENDAR_DAY, CalendarTotalLinkContract.CONTENT_URI, getTodayLinkSelection(), null);
        mDate = null;
    }

    public CalendarDayLoader(Context context, long day) {
        super(context, LTaskContract.CONTENT_URI, new TaskSelectionBuilder().getCalendarByDay(day).build(), new TaskSelectionBuilder().getOrderForTasks().build());
        mDate = day;
    }

    private static String getTodayLinkSelection() {
        return SelectionKeeper.equals(null, CalendarTotalLinkContract.Uid, //
                CalendarLink.getStringUidFromDate(TimeHelper.currentTimeMillisWithoutTimeZone()));
    }

    @Override
    public Cursor loadInBackground() {
        final Cursor cursor = super.loadInBackground();

        if (mDate != null) {
            new CalendarDayThread(getContext(), mDate).run();

        } else if (getMenuItem() != null && !cursor.moveToFirst()) {
            new CalendarDayThread(getContext(), TimeHelper.currentTimeMillisWithoutTimeZone()).run();
        }

        return cursor;
    }

    private final static class CalendarDayThread extends Thread {

        // BASE
        private final Context mContext;
        private final long mDate;

        // VALUE
        private final String mUid;

        public CalendarDayThread(Context context, long date) {
            super(CalendarDayThread.class.getSimpleName());

            mContext = context.getApplicationContext();
            if (date <= 0) {
                date = TimeHelper.currentTimeMillisWithoutTimeZone();
            }
            mDate = CalendarLink.getLongUidFromDate(date);

            mUid = String.valueOf(mDate) + POSTFIX;
        }

        @Override
        public void run() {
            super.run();

            boolean notifyChange = false;
            try {
                notifyChange = process();

            } catch (Exception e) {
                Utils.toLog(e);

            } finally {
                if (notifyChange) {
                    mContext.getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
                }
            }
        }

        private boolean process() throws Exception {
            final boolean beforeOrToday;
            {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());
                TimeHelper.roundCalendar(calendar, false);

                final long todayEnd = calendar.getTimeInMillis();
                beforeOrToday = mDate <= todayEnd;
            }

            CalendarLink link = null;
            Cursor c = null;

            try {
                c = mContext.getContentResolver().query(CalendarLinkContract.CONTENT_URI, null, //
                        SelectionKeeper.equals(null, CalendarLinkContract.Uid, mUid), null, DEFAULT_ORDER_CALENDAR);

                if (c.moveToFirst()) {
                    link = new CalendarLink(c);
                }

            } catch (Exception e) {
                Utils.toLog(e);

            } finally {
                if (c != null) {
                    c.close();
                }
            }

            boolean answer = false;
            if (link == null) {
                answer = recalculate(null, beforeOrToday);

            } else {
                switch (link.getTaskId()) {
                case FLAG_LOADED_TASKS:
                    /** значит что всё ок, начальная запись инициирована и это уже сегодня или прошлое */
                    break;

                case FLAG_LOADED_FUTURE:
                    if (beforeOrToday) {
                        /** значит что нужно произвести пересчет */
                        answer = recalculate(link, beforeOrToday);
                        break;

                    } else {
                        /** значит что всё ок, начальная запись инициирована и это завтра или будущее */
                        break;
                    }

                default:
                    answer = recalculate(null, beforeOrToday);
                    break;
                }
            }

            return answer;
        }

        private boolean recalculate(CalendarLink link, boolean beforeOrToday) {
            new TaskLinkReset(mContext).runCalendar(mDate, false);

            if (link == null) {
                link = new CalendarLink();
                link.setUid(mUid);
                link.setTaskId(beforeOrToday ? FLAG_LOADED_TASKS : FLAG_LOADED_FUTURE);

                mContext.getContentResolver().insert(CalendarLinkContract.CONTENT_URI, link.getContentValues(null));
                return true;

            } else if (link.getTaskId() == FLAG_LOADED_FUTURE && beforeOrToday) {
                link.setTaskId(FLAG_LOADED_TASKS);
                mContext.getContentResolver().update(CalendarLinkContract.CONTENT_URI,//
                        link.getContentValues(null), SelectionKeeper.equals(null, CalendarLinkContract.Uid, mUid), null);

                return true;
            }
            return false;
        }
    }
}