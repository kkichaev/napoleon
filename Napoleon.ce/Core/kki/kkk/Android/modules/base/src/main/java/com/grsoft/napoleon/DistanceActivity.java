package com.grsoft.napoleon;

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

import java.util.Calendar;
import java.util.Date;

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

    GPSPos pos = null;
    double distance = 0.0;

    public void calcDistance() {
        CfgNpl config = (CfgNpl) ConfigManager.getConfig();
        Date date = Util.resetTime(Calendar.getInstance().getTime());

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, config.distance_start);
        Date d1 = c.getTime();

        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, config.distance_end);
        Date d2 = c.getTime();

        String where = "";
        where = "date >= " + d1.getTime() + " and date < " + d2.getTime();

        DataTraveler.travel(GPSPos.class, new DataTraveler.Travel<GPSPos>(true) {
            @Override
            public boolean travel(DataTraveler<GPSPos> item) {
                if (pos != null)
                    distance += Coordutils.distance(
                            pos.latitude / Consts.GPS_SCALE, pos.longitude / Consts.GPS_SCALE,
                            item.data.latitude / Consts.GPS_SCALE, item.data.longitude / Consts.GPS_SCALE
                    );

                pos = item.data;
                return true;
            }
        }, where);
    }

    private void updateDistanceLabel() {
        distance = 0.0;
        calcDistance();

        ((TextView) findViewById(R.id.tvDistance)).setText(getString(R.string.distance, Math.round(distance)));
    }
}
