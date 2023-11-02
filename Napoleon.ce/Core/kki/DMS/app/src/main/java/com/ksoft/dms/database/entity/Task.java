package com.ksoft.dms.database.entity;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {
    public static final int NOT_SET = 0;
    public static final int OK = 1;
    public String id;
    public long created;
    public long date;
    public String schedule;
    public String text;
    public int status;
    public List<TaskItem> items;
    public long finish;
    public int alarmid;
}
