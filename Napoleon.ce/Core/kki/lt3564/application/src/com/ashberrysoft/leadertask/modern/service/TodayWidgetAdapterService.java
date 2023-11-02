package com.ashberrysoft.leadertask.modern.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Антон on 25.06.2018.
 */

public class TodayWidgetAdapterService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyFactory(getApplicationContext(), intent);
    }

}