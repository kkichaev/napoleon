package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Класс, для связи фильтров с кол-вом задач.
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
@DatabaseTable(tableName = "filter_number_task")
public class FilterNumberTask implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String RECORD_TODAY = "a84e8c08-c144-498e-8632-b6578e053b45";
    public static final String RECORD_INCOME = "366f7075-3d70-4bd1-b7e4-16f878faff31";
    public static final UUID sTodayRecordUUID = UUID.fromString(RECORD_TODAY);
    public static final UUID sIncomeRecordUUID = UUID.fromString(RECORD_INCOME);

    public static final String FILTER_TODAY = "today";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TASK_MODE = "TaskMode";
    public static final String FIELD_TASK_ALL = "TaskALL";
    public static final String FIELD_TASK_NOT_DONE = "TaskNotDone";
    public static final String FIELD_TASK_NOT_READ_FOR_ALL = "TaskNotReadForAll";
    public static final String FIELD_TASK_NOT_READ_FOR_NOT_DONE = "TaskNotReadForNotDone";

    @DatabaseField(generatedId = true)
    private int mId;

    /**
     * режим отображения задач: 0 - сегодня; 1 - входящие; 2 - поручено мне; 3 - проекты и доступные мне; 4 - категории;
     * 
     */
    @DatabaseField(columnName = FIELD_TASK_MODE, index = true, uniqueCombo = true)
    private int mTaskMode;

    /**
     * имя фильтра
     */
    @DatabaseField(columnName = FIELD_NAME, index = true, uniqueCombo = true)
    private String mName;

    /**
     * общее количество задач
     */
    @DatabaseField(columnName = FIELD_TASK_ALL, index = true)
    private int mTaskAll;

    /**
     * количество незавершенных задач
     */
    @DatabaseField(columnName = FIELD_TASK_NOT_DONE, index = true)
    private int mTaskNotDone;

    /**
     * количество непрочитанных задач при включенной опции "показать сделанные задачи"
     */
    @DatabaseField(columnName = FIELD_TASK_NOT_READ_FOR_ALL, index = true)
    private int mTaskNotReadForAll;

    /**
     * количество непрочитанных задач при включенной опции "скрыть сделанные задачи"
     */
    @DatabaseField(columnName = FIELD_TASK_NOT_READ_FOR_NOT_DONE, index = true)
    private int mTaskNotReadForNotDone;

    public FilterNumberTask() {
    }

    // parameterized constructor
    public FilterNumberTask(int mode, String name, int taskAll, int taskNotDone, int taskNotReadForAll,
            int taskNotReadForNotDone) {
        setName(name);
        setTaskMode(mode);
        setTaskAll(taskAll);
        setTaskNotDone(taskNotDone);
        setTaskNotReadForAll(taskNotReadForAll);
        setTaskNotReadForNotDone(taskNotReadForNotDone);
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getTaskMode() {
        return mTaskMode;
    }

    public void setTaskMode(int mTaskMode) {
        this.mTaskMode = mTaskMode;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getTaskAll() {
        return mTaskAll;
    }

    public void setTaskAll(int mTaskAll) {
        this.mTaskAll = mTaskAll;
    }

    public int getTaskNotDone() {
        return mTaskNotDone;
    }

    public void setTaskNotDone(int mTaskNotDone) {
        this.mTaskNotDone = mTaskNotDone;
    }

    public int getTaskNotReadForAll() {
        return mTaskNotReadForAll;
    }

    public void setTaskNotReadForAll(int mTaskNotReadForAll) {
        this.mTaskNotReadForAll = mTaskNotReadForAll;
    }

    public int getTaskNotReadForNotDone() {
        return mTaskNotReadForNotDone;
    }

    public void setTaskNotReadForNotDone(int mTaskNotReadForNotDone) {
        this.mTaskNotReadForNotDone = mTaskNotReadForNotDone;
    }
}