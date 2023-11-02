package com.ashberrysoft.leadertask.modern.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.cache.CompletedCache;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskSeriesHelper extends Thread {

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

    // BASE
    private final Context mContext;
    /** Задача над которой проводится операция */
    private final LTask mTask;
    private final LTask mTaskOld;

    // VALUE's
    private Calendar mBegin;
    private Calendar mEnd;

    private Calendar mTemporary;
    private Boolint[] mDaysOfWeek;

    public TaskSeriesHelper(Context context, LTask task) {
        super(TaskSeriesHelper.class.getSimpleName());

        mContext = context;
        mTask = task;
        mTaskOld = task.clone();
    }

    @Override
    public void run() {
        super.run();

        try {
            if (LTSettings.getInstance().getUserName().equals(mTask.getEmailCustomer()) && //
                    (mTask.getStatus() == TaskStatus.COMPLETED.getCode() || //
                    mTask.getStatus() == TaskStatus.CANCELLED.getCode())) {
                process();
            }

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    /** Создается следующая задачи из серии */
    private void process() throws Exception {
        /** если задача не повторяющаяся */
        if (mTask.getSeriesType() == SeriesType.NONE.ordinal()) {
            /** ничего не делать */
            return;
        }

        final long currentTimeMillis = System.currentTimeMillis();
        /** если время серии для задачи уже истекло */
        if (mTask.getSeriesEnd() != 0 && mTask.getSeriesEnd() <= currentTimeMillis) {
            /** сброс значений текущей задачи */
            resetCurrentTaskNew(mTask);
            /** выход из метода */
            return;
        }

        /** инициализация вспомогательных календарей */
        {
            TimeZone timeZone = new TimeZone() {
                @Override
                public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
                    return 0;
                }

                @Override
                public void setRawOffset(int offsetMillis) {

                }

                @Override
                public int getRawOffset() {
                    return 0;
                }

                @Override
                public boolean useDaylightTime() {
                    return false;
                }

                @Override
                public boolean inDaylightTime(Date date) {
                    return false;
                }
            };

            mBegin = Calendar.getInstance();
            mBegin.setTimeZone(timeZone);
            mEnd = Calendar.getInstance();
            mEnd.setTimeZone(timeZone);
            mTemporary = Calendar.getInstance();
            mTemporary.setTimeZone(timeZone);
        }

        /** если время у задачи не задано */
        if (mTask.getTermBegin() == 0 && mTask.getTermEnd() == 0) {
            mBegin.setTimeInMillis(currentTimeMillis);
            TimeHelper.roundCalendar(mBegin, true);

            mEnd.setTimeInMillis(currentTimeMillis);
            TimeHelper.roundCalendar(mEnd, false);
        }
        /** если время начала задачи не задано */
        else if (mTask.getTermBegin() == 0) {
            mEnd.setTimeInMillis(mTask.getTermEnd());

            mBegin.setTimeInMillis(mEnd.getTimeInMillis());
            TimeHelper.roundCalendar(mBegin, true);
        }
        /** если время окончания задачи не задано */
        else if (mTask.getTermEnd() == 0) {
            mBegin.setTimeInMillis(mTask.getTermBegin());

            mEnd.setTimeInMillis(mBegin.getTimeInMillis());
            TimeHelper.roundCalendar(mEnd, false);
        }
        /** если время задачи задано */
        else {
            mBegin.setTimeInMillis(mTask.getTermBegin());
            mEnd.setTimeInMillis(mTask.getTermEnd());
        }

        /** до тех пор, пока начало задачи не станет больше чем текущее время */
        int moreOne = 0;
        do {
            switch (SeriesType.values()[mTask.getSeriesType()]) {
            case DAILY:
                moreOne++;
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
            if (mTask.getSeriesEnd() != 0 && mBegin.getTimeInMillis() >= mTask.getSeriesEnd()) {
                /** сброс значений текущей задачи */
                resetCurrentTaskNew(mTask);
                /** выход из метода */
                return;
            }
        } while (mEnd.getTimeInMillis()<= currentTimeMillis);

        createNextTask();
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
            mDaysOfWeek = new Boolint[7];
            mDaysOfWeek[0] = new Boolint(Calendar.SUNDAY, mTask.getSeriesWeekSun());
            mDaysOfWeek[1] = new Boolint(Calendar.MONDAY, mTask.getSeriesWeekMon());
            mDaysOfWeek[2] = new Boolint(Calendar.TUESDAY, mTask.getSeriesWeekTue());
            mDaysOfWeek[3] = new Boolint(Calendar.WEDNESDAY, mTask.getSeriesWeekWed());
            mDaysOfWeek[4] = new Boolint(Calendar.THURSDAY, mTask.getSeriesWeekThu());
            mDaysOfWeek[5] = new Boolint(Calendar.FRIDAY, mTask.getSeriesWeekFri());
            mDaysOfWeek[6] = new Boolint(Calendar.SATURDAY, mTask.getSeriesWeekSat());
        }

        boolean moreThanOne;
        {
            byte days = 0;
            for (Boolint b : mDaysOfWeek) {
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
        for (Boolint b : mDaysOfWeek) {
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
        for (Boolint b : mDaysOfWeek) {
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
            monthlyWeeklyShift(mTemporary, mTask.getSeriesMonthWeekType(), fromMonSunToSunSat(mTask.getSeriesMonthDayOfWeek()));
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
    private void resetCurrentTaskNew(LTask task) {
        resetTaskSeries(task, true);

        task.setCompleteTime(TimeHelper.currentTimeMillisWithoutTimeZone());
        task.setUsnFieldCompletetime(task.getUsnFieldCompletetime() + 1);

        mContext.getContentResolver().update(LTaskContract.CONTENT_URI, task.getContentValues(null),
                SelectionKeeper.equals(null, LTaskContract._ID, task.getIdTask()), null);
    }

    /** Значения по умолчанию для задачи без повторений */
    public static void resetTaskSeries(LTask task, boolean resetUsnEntity) {
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
        task.setSeriesEnd(0);

        if (resetUsnEntity) {
            task.setUsnEntity(0);
            task.setUsnFieldSeries(task.getUsnFieldSeries() + 1);

        } else {
            task.setUsnFieldSeries(0);
        }
    }

    private void createNextTask() {
        //создать новую завершенную с другим юид
        final LTask taskNew = TaskHelper.createNewTaskWithParams(LTSettings.getInstance().getUserName(), mTask.getEmailPerformer(), mTask.getTermBegin(), mTask.getUIDParent(), mTask.getUidProject(), mTask.getCategories(), mTask.getUidMarker());

        taskNew.setStatus(mTask.getStatus());
        taskNew.setName(mTask.getName());
        taskNew.setComment(mTask.getComment());
        taskNew.setTermBegin(mTask.getTermBegin());
        taskNew.setTermEnd(mTask.getTermEnd());
        taskNew.setUidMarker(null);

        new TaskSaveHelper(false, mContext, taskNew, true, null, null, 0, copyTaskFiles(UUID.fromString(mTask.getUid())), new ArrayList<TaskFile>(0), true).run();
        //сохранить текущую и прибавить срок
        {
            mTask.setUsnEntity(mTask.getUsnEntity() + 1);
            mTask.setUsnFieldTerm(mTask.getUsnFieldTerm() + 1);
            mTask.setUsnFieldCustomerTerm(mTask.getUsnFieldCustomerTerm() + 1);
            mTask.setStatus(TaskStatus.NOT_BEGIN.getCode());
            mTask.setUsnFieldStatus(mTask.getUsnFieldStatus() +1);
            //обновление хронометража
            mTask.setTime(0);
            mTask.setInWorkTime(0);
            mTask.setUsnTime(mTask.getUsnTime() + 1);
            mTask.setUsnInWorkTime(mTask.getUsnInWorkTime() + 1);
            //
        }
        mTask.setTermBegin(mBegin.getTimeInMillis());
        mTask.setTermEnd(mEnd.getTimeInMillis());
        mTask.setTermBeginCustomer(mBegin.getTimeInMillis());
        mTask.setTermEndCustomer(mEnd.getTimeInMillis());
        new TaskSaveHelper(false, mContext, mTask, false, null, mTaskOld, 0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), true).run();

        CompletedCache.getInstance(mContext).refreshCache();

    }

    private List<TaskFile> copyTaskFiles(UUID taskUid) {
        final List<TaskFile> taskFiles;
        {
            final List<TaskFile> files = TaskFileCache.getInstance(mContext).find(taskUid.toString().toLowerCase().hashCode());
            taskFiles = files == null ? new ArrayList<TaskFile>(0) : new ArrayList<>(files);
        }

        if (taskFiles.size() == 0) {
            return taskFiles;
        }

        final File appFolder = ((LTApplication) mContext).getAppFolder();
        int count = 1;

        for (TaskFile file : taskFiles) {
            // TODO: may be need to clone

            if (file.isFileExist()) {
                try {
                    Utils.FileWorker.copyFile(file.getFileName(), appFolder);

                } catch (IOException e) {
                    Utils.toLog(e);
                }
            }
            file.setDeleteObject(false);
            file.setFileExist(true);
            file.setId(UUID.randomUUID());
            file.setFileId(UUID.randomUUID());
            file.setTaskId(taskUid);
            file.setOrder(count++);
            file.resetUsnFields();
        }

        return taskFiles;
    }

    /** Вспомогательный класс для работы со сдвигом по дням недели */
    private static final class Boolint {

        public boolean bool;
        public int inte;

        public Boolint(int i, boolean b) {
            this.bool = b;
            this.inte = i;
        }
    }
}