package com.grsoft.database;

import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.napoleon.BuildConfig;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AgentPlanRcv extends HitchOnSelect{
    public AgentPlanRcv() {
        super(AgentPlan.class, "AgentPlan");

        Calendar c = Calendar.getInstance();
//        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        if(BuildConfig.DEBUG) {
            c.set(Calendar.MONTH, Calendar.JUNE);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String filter = String.format("userid='$CURRENT_USERID' and [begin] >= ToDate('%s')"
                , sdf.format(c.getTime()));
        setCondition(filter);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        super.onRead(rawObject);
    }
}
