package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.CalendarView;

import java.util.Date;

public class CalendarActivityEx extends CalendarActivity {
    static CalendarView.CalendarHandler handler;

    public static Intent open(Context context, Date date, CalendarView.CalendarHandler handler) {
        CalendarActivityEx.handler = handler;
        Intent i = new Intent(context, CalendarActivityEx.class);
        i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDateHandler(handler);
    }
}
