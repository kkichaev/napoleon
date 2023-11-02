package com.ashberrysoft.leadertask.modern.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;

import java.util.ArrayList;

public class CloseTaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LTask oldTask = (LTask) intent.getSerializableExtra(SlidingActivity.EXTRA_TASK);
        LTask newTask = oldTask.clone();

        LTSettings setting = LTSettings.getInstance(context);

        int status = TaskStatus.COMPLETED.getCode();

        if(!newTask.getEmailPerformer().equals(setting.getUserName()))
            status = TaskStatus.READY.getCode();

        newTask.setStatus(status);
        newTask.setUsnFieldStatus(newTask.getUsnFieldStatus() + 1);

        new TaskSaveHelper(false, context, newTask, false, null, oldTask, 0,//
                new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
    }
}
