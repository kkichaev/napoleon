package com.ashberrysoft.leadertask.modern.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class TomorrowTaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LTask oldTask = (LTask) intent.getSerializableExtra(SlidingActivity.EXTRA_TASK);
        LTask newTask = oldTask.clone();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            manager.cancel(oldTask.getIdTask());
        }catch(Exception e){
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(newTask.getTermBegin());
        c.add(Calendar.DATE, 1);

        newTask.setTermBegin(c.getTimeInMillis());

        c.setTimeInMillis(newTask.getTermEnd());
        c.add(Calendar.DATE, 1);

        newTask.setTermEnd(c.getTimeInMillis());
        newTask.setUsnFieldTerm(newTask.getUsnFieldTerm() + 1);

        new TaskSaveHelper(false, context, newTask, false, null, oldTask, 0,//
                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
    }
}
