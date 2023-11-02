package com.ashberrysoft.leadertask.data_providers;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * @since 2014-06-23
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class TaskSeriesCalculator {

    public enum SeriesType {
        /** 0 Нет повторений */
        NONE(R.string.empty_string, R.string.empty_string),
        /** 1 Ежедневно */
        DAILY(R.string.lang_task_recurrence_daily, R.string.lang_task_after_day),
        /** 2 Еженедельно */
        WEEKLY(R.string.lang_task_recurrence_weekly, R.string.lang_task_after_week),
        /** 3 Ежемесячно */
        MONTHLY(R.string.lang_task_recurrence_monthly, R.string.lang_task_after_month),
        /** 4 Ежегдно */
        YEARLY(R.string.lang_task_recurrence_yearly, R.string.lang_task_after_year);

        private int mMainResId;
        private int mAfterResId;

        private SeriesType(int mainResId, int afterResId) {
            mMainResId = mainResId;
            mAfterResId = afterResId;
        }

        public int getMainResId() {
            return mMainResId;
        }

        public int getAfterResId() {
            return mAfterResId;
        }
    }

    // VALUE's
    private Context mContext;
    /** Задача над которой проводится операция */
    private Task mTask;
    /** Предыдущий статус задачи */

    private Calendar mBegin;
    private Calendar mEnd;

    private Calendar mTemporary;
    private Boolinte[] mDaysOfWeek;
    private Task mNewTaks;

    public TaskSeriesCalculator(Context context, Task task) {
        mContext = context;
        mTask = task;
    }

    /** Создается следующая задачи из серии */
    public void createNextSeriesTask() {
        /** если задача не повторяющаяся */
        if (mTask.getSeriesType() == SeriesType.NONE.ordinal()) {
            /** ничего не делать */
            return;
        }

        final long currentTimeMillis = System.currentTimeMillis();
        /** если время серии для задачи уже истекло */
        if (mTask.getSeriesEnd() != null && mTask.getSeriesEnd().getTime() <= currentTimeMillis) {
            /** сброс значений текущей задачи */
            resetCurrentTask();
            /** выход из метода */
            return;
        }

        /** инициализация вспомогательных календарей */
        {
            final TimeZone tz = TimeZone.getTimeZone(SharedStrings.GMT);
            mBegin = Calendar.getInstance(tz);
            mEnd = Calendar.getInstance(tz);
            mTemporary = Calendar.getInstance(tz);
        }

        /** если время у задачи не задано */
        if (mTask.getTermBegin() == null && mTask.getTermEnd() == null) {
            mBegin.setTimeInMillis(currentTimeMillis);
            resetCalendar(mBegin, true);

            mEnd.setTimeInMillis(currentTimeMillis);
            resetCalendar(mEnd, false);
        }
        /** если время начала задачи не задано */
        else if (mTask.getTermBegin() == null) {
            mEnd.setTime(mTask.getTermEnd());

            mBegin.setTimeInMillis(mEnd.getTimeInMillis());
            resetCalendar(mBegin, true);
        }
        /** если время окончания задачи не задано */
        else if (mTask.getTermEnd() == null) {
            mBegin.setTime(mTask.getTermBegin());

            mEnd.setTimeInMillis(mBegin.getTimeInMillis());
            resetCalendar(mEnd, false);
        }
        /** если время задачи задано */
        else {
            mBegin.setTime(mTask.getTermBegin());
            mEnd.setTime(mTask.getTermEnd());
        }

        /** до тех пор, пока начало задачи не станет больше чем текущее время */
        do {
            switch (SeriesType.values()[mTask.getSeriesType()]) {
            case DAILY:
                dailyShift();
                break;

            case WEEKLY:
                weeklyShift();
                break;

            case MONTHLY:
                monthlyShift();
                break;

            case YEARLY:
                yearlyShift();
            default:
                break;
            }

            /** если ближайшая дата начала задачи больше, чем время окончания серии задачи */
            if (mTask.getSeriesEnd() != null && mBegin.getTimeInMillis() >= mTask.getSeriesEnd().getTime()) {
                /** сброс значений текущей задачи */
                resetCurrentTask();
                /** выход из метода */
                return;
            }
        } while (mEnd.getTimeInMillis() <= currentTimeMillis);

        /** создается новая задача на базе предыдущей задачи из серии */
        final CloneTaskHelper cloneTaskHelper = new CloneTaskHelper(mContext, mTask);
        cloneTaskHelper.createCloneOfSeriesTask(mBegin.getTime(), mEnd.getTime()).create();

        mNewTaks = cloneTaskHelper.getNewTask();

        resetCurrentTask();
    }

    /** Добавляются значения сразу двум календарям задачи */
    private void addToCalendars(int field, int value) {
        if (value != 0) {
            mBegin.add(field, value);
            mEnd.add(field, value);
        }
    }

    /** Линейный сдвиг */
    private void dailyShift() {
        switch (SeriesType.values()[mTask.getSeriesAfterType()]) {
        case DAILY:
            addToCalendars(Calendar.DAY_OF_YEAR, mTask.getSeriesAfterCount());
            break;

        case WEEKLY:
            addToCalendars(Calendar.DAY_OF_YEAR, mTask.getSeriesAfterCount() * 7);
            break;

        case MONTHLY:
            addToCalendars(Calendar.MONTH, mTask.getSeriesAfterCount());
            break;

        case YEARLY:
            addToCalendars(Calendar.YEAR, mTask.getSeriesAfterCount());
            break;

        default:
            break;
        }
    }

    /** Сдвиг на неделю от даты начала задачи */
    private void weeklyShift() {
        if (mDaysOfWeek == null) {
            mDaysOfWeek = new Boolinte[7];
            mDaysOfWeek[0] = new Boolinte(Calendar.SUNDAY, mTask.isSeriesWeekSun());
            mDaysOfWeek[1] = new Boolinte(Calendar.MONDAY, mTask.isSeriesWeekMon());
            mDaysOfWeek[2] = new Boolinte(Calendar.TUESDAY, mTask.isSeriesWeekTue());
            mDaysOfWeek[3] = new Boolinte(Calendar.WEDNESDAY, mTask.isSeriesWeekWed());
            mDaysOfWeek[4] = new Boolinte(Calendar.THURSDAY, mTask.isSeriesWeekThu());
            mDaysOfWeek[5] = new Boolinte(Calendar.FRIDAY, mTask.isSeriesWeekFri());
            mDaysOfWeek[6] = new Boolinte(Calendar.SATURDAY, mTask.isSeriesWeekSat());
        }

        boolean moreThanOne;
        {
            byte days = 0;
            for (Boolinte b : mDaysOfWeek) {
                if (b.bool) {
                    days++;
                }
            }
            moreThanOne = days > 1;
        }

        boolean onMoreThanOne = false;
        boolean added = false;
        // mTemporary.setTimeInMillis(mBegin.getTimeInMillis());
        final int beginDayOfWeek = mBegin.get(Calendar.DAY_OF_WEEK);

        /** сначала ищутся совпадения по текущему дню недели, если их нет, то по будущим */
        for (Boolinte b : mDaysOfWeek) {
            if (b.bool && b.inte >= beginDayOfWeek) {
                if (moreThanOne) {
                    moreThanOne = false;
                    onMoreThanOne = true;
                    continue;
                } else {
                    final int differenceInDays = b.inte - beginDayOfWeek;
                    addToCalendars(Calendar.DAY_OF_WEEK, differenceInDays);
                    if (!onMoreThanOne) {
                        addToCalendars(Calendar.DAY_OF_WEEK, mTask.getSeriesWeekCount() * 7);
                    }

                    added = true;
                    break;
                }
            }
        }

        if (added) {
            return;
        }

        /** если еще не обработано, то ищется предыдущий день недели */
        for (Boolinte b : mDaysOfWeek) {
            if (b.bool && b.inte < beginDayOfWeek) {
                final int differenceInDays = b.inte - beginDayOfWeek;
                addToCalendars(Calendar.DAY_OF_WEEK, differenceInDays);
                addToCalendars(Calendar.DAY_OF_WEEK, mTask.getSeriesWeekCount() * 7);
                break;
            }
        }
    }

    /** Сдвиг на месяц от даты начала задачи */
    private void monthlyShift() {
        mTemporary.setTimeInMillis(mBegin.getTimeInMillis());

        /** через каждые N месяцев в конкретную M неделю месяца в I день недели */
        if (mTask.getSeriesMonthType() == SeriesType.WEEKLY.ordinal()) {
            monthlyWeeklyShift(mTemporary, mTask.getSeriesMonthWeekType(),
                    fromMonSunToSunSat(mTask.getSeriesMonthDayOfWeek()));
        }
        /** через каждые N месяцев в конкретное M число (M > N.size ? N.size : M) */
        else {
            mTemporary.add(Calendar.MONTH, mTask.getSeriesMonthCount());
            mTemporary.set(Calendar.DAY_OF_MONTH, mTask.getSeriesMonthDay());
            final int dayOfMonth = mTemporary.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth != mTask.getSeriesMonthDay()) {
                mTemporary.add(Calendar.DAY_OF_MONTH, -dayOfMonth);
            }
        }

        getDifferenceAndAddToCalendars();
    }

    /** Разница просчитаной даты с началом задачи добавляется календарям задачи */
    private void getDifferenceAndAddToCalendars() {
        final long differenceBeginEnd = mEnd.getTimeInMillis() - mBegin.getTimeInMillis();

        mBegin.set(Calendar.YEAR, mTemporary.get(Calendar.YEAR));
        mBegin.set(Calendar.DAY_OF_YEAR, mTemporary.get(Calendar.DAY_OF_YEAR));

        mEnd.setTimeInMillis(mBegin.getTimeInMillis() + differenceBeginEnd);
    }

    /** Приведение дня недели с формата ПН-ВС к с формату ВС-СБ */
    private int fromMonSunToSunSat(int dayOfWeek) {
        int newDayOfWeek = dayOfWeek + 1;
        if (newDayOfWeek > 7) {
            newDayOfWeek = 1;
        }
        return newDayOfWeek;
    }

    /** Метод для поиска ближайшего дня недели по условию */
    private void monthlyWeeklyShift(Calendar begin, int weekInMonth, int dayOfWeek) {
        /** начальное время календаря */
        final long time = begin.getTimeInMillis();

        boolean condition;
        do {
            /** начальное условие для поиска указанного дня недели */
            begin.set(Calendar.DAY_OF_MONTH, 1);

            /** считается по порядку какой по счету это день недели */
            for (int dayOfWeekInMonth = 0; dayOfWeekInMonth < weekInMonth;) {
                /** если текущий день недели совпадает с заданным */
                if (begin.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                    /** увеличивается номер по порядку дня недели */
                    dayOfWeekInMonth++;

                    final int maximalDayOfWeek = begin.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
                    /** проверяется максимальное ко-во заданных дней недели в текущем месяце */
                    if (weekInMonth > maximalDayOfWeek) {
                        /** если больше чем возможно */
                        weekInMonth = maximalDayOfWeek;
                    }
                }

                /** если номер дня недели не равен необходимому */
                if (dayOfWeekInMonth != weekInMonth) {
                    /** начинается проверка нового дня */
                    begin.add(Calendar.DAY_OF_MONTH, 1);
                }
            }

            /** если получившаяся дата меньше начальной */
            if (condition = begin.getTimeInMillis() <= time) {
                /** дата сбрасывается */
                begin.setTimeInMillis(time);
                /** увеличивается на заданное ко-во месяцев */
                begin.add(Calendar.MONTH, mTask.getSeriesMonthCount());
            }
        } while (condition);
    }

    /** Годовой сдвиг */
    private void yearlyShift() {
        mTemporary.setTimeInMillis(mBegin.getTimeInMillis());

        /** через каждый год в конкретный N месяц года в конкретную M неделю месяца в I день недели */
        if (mTask.getSeriesYearType() == SeriesType.WEEKLY.ordinal()) {
            yearlyWeeklyShift(mTemporary, mTask.getSeriesYearMonth() - 1,//
                    mTask.getSeriesYearWeekType(), fromMonSunToSunSat(mTask.getSeriesYearDayOfWeek()));
        }

        /** через каждый год в конкретный N месяц года в конкретное M число (M > N.size ? N.size : M) */
        else {
            mTemporary.add(Calendar.YEAR, 1);
            mTemporary.set(Calendar.MONTH, mTask.getSeriesYearMonth() - 1);
            mTemporary.set(Calendar.DAY_OF_MONTH, mTask.getSeriesYearMonthDay());
            final int dayOfMonth = mTemporary.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth != mTask.getSeriesYearMonthDay()) {
                mTemporary.add(Calendar.DAY_OF_MONTH, -dayOfMonth);
            }
        }

        getDifferenceAndAddToCalendars();
    }

    /** Метод для поиска ближайшего дня недели по условию */
    private void yearlyWeeklyShift(Calendar begin, int monthOfYear, int weekInMonth, int dayOfWeek) {
        final long time = begin.getTimeInMillis();
        begin.set(Calendar.MONTH, monthOfYear);

        boolean condition;
        do {
            begin.set(Calendar.DAY_OF_MONTH, 1);

            for (int dayOfWeekInMonth = 0; dayOfWeekInMonth < weekInMonth;) {
                if (begin.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                    dayOfWeekInMonth++;

                    final int maximalDayOfWeek = begin.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
                    if (weekInMonth > maximalDayOfWeek) {
                        weekInMonth = maximalDayOfWeek;
                    }
                }

                if (dayOfWeekInMonth != weekInMonth) {
                    begin.add(Calendar.DAY_OF_WEEK, 1);
                }
            }

            if (condition = begin.getTimeInMillis() <= time) {
                begin.setTimeInMillis(time);
                begin.set(Calendar.MONTH, monthOfYear);
                begin.add(Calendar.YEAR, 1);
            }
        } while (condition);
    }

    /** Обнуляет значения серии текущей задачи */
    private void resetCurrentTask() {
        resetTaskSeries(mTask, true);

        mTask.setCompleteTime(new Date(Utils.getCurrentTimeWithSavings()));
        mTask.setUsnFieldCompleteTime(mTask.getUsnFieldCompleteTime() + 1);

        try {
            DbHelper.getInstance(mContext).getTaskDao().update(mTask);
        } catch (SQLException e) {
            Utils.toLog(e);
        }
    }

    /** Значения по умолчанию для задачи без повторений */
    public static void resetTaskSeries(Task task, boolean resetUsnEntity) {
        task.setSeriesType(0);
        task.setSeriesAfterType(1);
        task.setSeriesAfterCount(1);
        task.setSeriesWeekCount(1);
        task.setSeriesWeekMon(true);
        task.setSeriesWeekTue(false);
        task.setSeriesWeekWed(false);
        task.setSeriesWeekThu(false);
        task.setSeriesWeekFri(false);
        task.setSeriesWeekSat(false);
        task.setSeriesWeekSun(false);
        task.setSeriesMonthType(1);
        task.setSeriesMonthCount(1);
        task.setSeriesMonthDay(1);
        task.setSeriesMonthWeekType(1);
        task.setSeriesMonthDayOfWeek(1);
        task.setSeriesYearType(1);
        task.setSeriesYearMonth(1);
        task.setSeriesYearMonthDay(1);
        task.setSeriesYearDayOfWeek(1);
        task.setSeriesEnd(null);

        if (resetUsnEntity) {
            task.setUsn(0);
            task.setUsnFieldSeries(task.getUsnFieldSeries() + 1);
        } else {
            task.setUsnFieldSeries(0);
        }
    }

    /** Сброс времени календаря на начало или конец дня */
    public static void resetCalendar(Calendar c, boolean onBeginning) {
        if (onBeginning) {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 1);
        } else {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);
        }
    }

    /** Вспомогательный класс для работы со сдвигом по дням недели */
    private class Boolinte {

        public boolean bool;
        public int inte;

        public Boolinte(int i, boolean b) {
            this.bool = b;
            this.inte = i;
        }
    }

    public Task getNewTask() {
        return mNewTaks;
    }
}