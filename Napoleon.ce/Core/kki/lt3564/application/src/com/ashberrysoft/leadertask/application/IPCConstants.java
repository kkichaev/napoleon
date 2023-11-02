package com.ashberrysoft.leadertask.application;

public class IPCConstants {

    public static final boolean DEBUG = false;
    public static final boolean BOX = false;
    
	private static final String ACTION = "com.ashberrysoft.leadertask.intent.action";

    public static final String EXTRA_TASK = "task";
    public static final String EXTRA_TASK_POSITION = "task_position";
    public static final String ACTION_SAVE_TASK_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_SAVE_TASK_FINISHED";
    public static final String ACTION_DELETE_TASK_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_DELETE_TASK_FINISHED";
    public static final String ACTION_GET_SUBTASKS_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_SUBTASKS_FINISHED";
    public static final String ACTION_GET_TASKS_IN_CATEGORY_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_TASKS_IN_CATEGORY_FINISHED";
    public static final String ACTION_SAVE_TASK_CATEGORIES_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_SAVE_TASK_CATEGORIES_FINISHED";
    public static final String ACTION_GET_TASKS_BY_DATE_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_TASKS_BY_DATE_FINISHED";
    public static final String ACTION_GET_TASKS_BY_EMAIL_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_TASKS_BY_EMAIL_FINISHED";
    public static final String ACTION_GET_TASKS_NUMBER_IN_PROJECT_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_TASKS_NUMBER_IN_PROJECT_FINISHED";
    public static final String ACTION_GET_CATEGORY_TASKS_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_CATEGORY_TASKS_FINISHED";
    public static final String ACTION_GET_PROJECT_TASKS_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_PROJECT_TASKS_FINISHED";
    public static final String ACTION_GET_NOTIFICATION_TASKS_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_GET_NOTIFICATION_TASKS_FINISHED";
    public static final String ACTION_TASK_DEPTH_CALCULATION_FINISHED = "com.ashberrysoft.leadertask.intent.action.ACTION_TASK_DEPTH_CALCULATION_FINISHED";
	public static final String ACTION_SYNCHRONIZATION_STATE_CHANGED = ACTION+".ACTION_SYNCHRONIZATION_STATE_CHANGED";

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_TASK_RAW_TEXT = "raw_text";
}
