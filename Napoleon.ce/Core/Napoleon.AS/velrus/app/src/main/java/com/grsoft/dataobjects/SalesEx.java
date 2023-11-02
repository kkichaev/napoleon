package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;

public class SalesEx extends Sales {
    public int black = 0;

    public boolean canEdit() {
        return number.length() == 0;
    }

    public static SalesEx getNoNumber() {
        SalesEx sales = new SalesEx();
        DbReader r = new DbReader();
        boolean have = r.select(sales, sales.getTableName(), "number='' or number is null");
        r.close();

        return have ? sales : null;
    }
}
