package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.dataobjects.impl.ConfigImpl;

import java.util.Date;

public class PriceChecker {

    static final String P_NAME = "PriceChecker";
    public enum ActualState {OK, Alert, NotActual};

    static void markPriceRecieved(Context context) {
        SharedPreferences p = context.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        p.edit().putLong(P_NAME, new Date().getTime()).commit();
    }


    static ActualState getActual(Context context) {
        SharedPreferences p = context.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        long val = p.getLong(P_NAME, 0);
        if(val == 0) {
            return ActualState.NotActual;
        }

        long block = 3, alert = 1;
        ConfigImpl ci = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        if(ci.getValue(sb, "ПрайсПредупреждение")) {
            try {
                alert = Integer.parseInt(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb = new StringBuilder();
        if(ci.getValue(sb, "ПрайсБлокировка")) {
            try {
                block = Integer.parseInt(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long diff = (long)(new Date().getTime() - val) / (1000 * 24 * 3600);
        return diff > block ? ActualState.NotActual : diff > alert ? ActualState.Alert : ActualState.OK;
    }
}
