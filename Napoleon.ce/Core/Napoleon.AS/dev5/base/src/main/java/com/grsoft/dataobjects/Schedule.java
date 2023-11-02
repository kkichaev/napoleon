package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name = "Schedule", keyFields = "date")
public class Schedule extends DataObject {
    public Date date;
    public List<ScheduleItem> items = new ArrayList<>();
}
