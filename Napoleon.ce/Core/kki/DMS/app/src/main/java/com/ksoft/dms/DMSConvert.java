package com.ksoft.dms;

import android.content.Context;

public class DMSConvert {
    public static class DMS{
        public double d = 0.0;
        public double m = 0.0;
        public double s = 0.0;
    }

    public static DMS toDMS(double val){
        DMS result = new DMS();

        result.d = (int)val;
        double min = (val - result.d) * 60;
        result.m = (int) min;
        result.s = (min - (int)min) * 60;

        return result;
    }

    public static String toDMSString(Context context, double val){
        DMS dms = toDMS(val);

        StringBuilder sb = new StringBuilder();

        if (dms.d < 10)
            sb.append("0");

        sb.append((int)dms.d).append(context.getString(R.string.angle));

        if (dms.m < 10)
            sb.append("0");

        sb.append((int)dms.m).append(context.getString(R.string.min));

        if (dms.s < 10)
            sb.append("0");

        sb.append(Math.round(dms.s * 10) / 10.0).append(context.getString(R.string.sec));

        return sb.toString();
    }
}
