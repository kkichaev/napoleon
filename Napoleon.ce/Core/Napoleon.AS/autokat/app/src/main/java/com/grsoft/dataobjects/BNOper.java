package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Calendar;

@TableInfo(name="bnoper", keyFields = "id")
@ServerInfo(name="BNOper")
public class BNOper extends DataObject{
    public String id = "";

    @DayOrder(order = Calendar.MONDAY)
    public String mo = "";
    @DayOrder(order = Calendar.TUESDAY)
    public String tu = "";
    @DayOrder(order = Calendar.WEDNESDAY)
    public String we = "";
    @DayOrder(order = Calendar.THURSDAY)
    public String th = "";
    @DayOrder(order = Calendar.FRIDAY)
    public String fr = "";
    @DayOrder(order = Calendar.SATURDAY)
    public String sa = "";
    @DayOrder(order = Calendar.SUNDAY)
    public String su = "";
}
