package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesEx extends Sales{
    public static final Date MIN_DATE = new Date(5000);
    public int black = 0;

    public Date bonusDoc = MIN_DATE;
    public int bonus = 0;
    public int blackBonus = 0;

    public List<SimpleItem> actions = new ArrayList<>();

    public boolean canEdit() {
        return number.length() == 0 && bonus == 0;
    }

    public static SalesEx getNoNumber() {
        SalesEx sales = new SalesEx();
        DbReader r = new DbReader();
        boolean have = r.select(sales, sales.getTableName(), "(number='' or number is null) and bonus = 0");
        r.close();

        return have ? sales : null;
    }
}
