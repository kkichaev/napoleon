package com.grsoft.napoleon;

import android.view.View;

public class GpsSettingEx extends GpsSetting {
    @Override
    protected void init() {
        super.init();

        int[] ids = new int[] {
                R.id.edFrec, R.id.edDist, R.id.cbSendDataInBackground, R.id.spDataSendInterval, R.id.spWaitGPSOnRecieve, R.id.spGPSValidInOrg,
        };
        for(int id : ids) {
            View v = findViewById(id);
            if(v != null)
                v.setEnabled(false);
        }
    }
}
