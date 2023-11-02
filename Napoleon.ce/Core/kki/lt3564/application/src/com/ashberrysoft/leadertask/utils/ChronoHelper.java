package com.ashberrysoft.leadertask.utils;

import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

public class ChronoHelper {
    public static ChronoHelper instance = new ChronoHelper();

    public long getFactTiming(long factOld, long dtInWork){
        long res = factOld;
        long now = TimeHelper.getInstance().currentTimeMillisWithoutTimeZone();
        long sec = (now - dtInWork) / 1000;

        final int WORK_DAY_HOUR = 8;
        final int WORK_DAY_SEC = WORK_DAY_HOUR * 60 * 60;
        final int SEN_IN_HOUR = 60 * 60;

        if (sec > 0){
            if (sec > WORK_DAY_SEC){
                long d = daysDiff(dtInWork, now);

                if (d <= 0)
                    d = 1;

                long h  = factOld / SEN_IN_HOUR;

                d += h / WORK_DAY_HOUR;
                res = d * WORK_DAY_SEC;
            }else
                res += sec;
        }

        return res;
    }

    private static long daysDiff(long from, long to) {
        return Math.round((to - from) / 86400000D); // 1000 * 60 * 60 * 24
    }
}
