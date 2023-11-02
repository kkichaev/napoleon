package com.grsoft.napoleon;
import com.grsoft.database.DbReader;
import com.grsoft.aceteam.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.GPSPos;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Coordutils;
import com.grsoft.util.Util;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DistanceActivity extends Activity {
    public static Class<? extends DistanceActivity> activity = DistanceActivity.class;

    public static void open(Context context) {
        Intent i = new Intent(context, activity);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance);

        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                finish();
            }
        });

        updateDistanceLabel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.distance_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itSetting) {
            openSetting();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSetting() {
        DialogFragment dlg = new DistanceSettings();
        dlg.show(getFragmentManager(), dlg.getClass().toString());
    }

    public double calcDistance() {
        double distance = 0.0;

        CfgNpl config = (CfgNpl) ConfigManager.getConfig();
        Date date = Util.resetTime(Calendar.getInstance().getTime());

        Calendar c = Calendar.getInstance();

        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, config.distance_start);
        long ds = c.getTime().getTime();
        long de = ds + (long) config.distance_end * 24 * 3600 * 1000;

        @SuppressLint("DefaultLocale") String where = String.format("date >= %d and date <= %d", ds, de);

        List<GPSPos> loc = DbReader.fetch(GPSPos.class, where, "date");
        for(int i=1; i< loc.size(); i++) {
            GPSPos p1 = loc.get(i-1);
            GPSPos p2 = loc.get(i);
            double cd = Coordutils.distance(
                    (double)p1.latitude / Consts.GPS_SCALE, (double)p1.longitude / Consts.GPS_SCALE,
                    (double)p2.latitude / Consts.GPS_SCALE, (double)p2.longitude / Consts.GPS_SCALE
            );

            if(cd < 100 * 1000)
                distance += cd;
        }

        return distance;
    }

    private void updateDistanceLabel() {
        double distance = calcDistance();

        ((TextView) findViewById(R.id.tvDistance)).setText(getString(R.string.distance, Math.round(distance)));
    }
}
