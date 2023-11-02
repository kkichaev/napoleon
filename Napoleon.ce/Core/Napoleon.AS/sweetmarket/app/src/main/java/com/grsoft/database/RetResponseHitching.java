package com.grsoft.database;

import com.grsoft.dataobjects.ReturnResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RetResponseHitching extends HitchOnSelect {
    public static int RESTORE_PERIOD = 1;
    public RetResponseHitching() {
        super(ReturnResponse.class, "ReturnResponse", "");
    }

    @Override
    protected String getCondition() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -RESTORE_PERIOD);
        String where = String.format("\"userid\"='$CURRENT_USERID' and \"created\" >= ToDate('%s')"
            ,sdf.format(c.getTime()));

        RESTORE_PERIOD = 1;
        return super.getCondition();
    }
}
