package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Monitoring;
import com.grsoft.manager.MonitoringDetail;

public class MMonitoringImpl extends MOrderImplBase<Monitoring> {
    @Override
    public void open(Context context) {
        MonitoringDetail.open(context, this);
    }
}
