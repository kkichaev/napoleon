package com.ksoft.dms.database;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ksoft.dms.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarIconHelper {
    public static void init(MaterialToolbar mtb){
        View view = mtb.getMenu().findItem(R.id.calendar).getActionView();
        TextView tv = view.findViewById(R.id.tvCurDate);
        tv.setText(new SimpleDateFormat("dd").format(new Date()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtb.getMenu().performIdentifierAction(R.id.calendar, 0);
            }
        });
    }
}
