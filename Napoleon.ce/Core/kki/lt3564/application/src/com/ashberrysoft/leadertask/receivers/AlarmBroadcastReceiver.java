package com.ashberrysoft.leadertask.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final TaskNotifyHelper notifyHelper = TaskNotifyHelper.getInstance(context);

        switch (intent.getAction()) {
        case Intent.ACTION_BOOT_COMPLETED:
            notifyHelper.connectAllTaskNotifiesToTrigger();
            break;

        case TaskNotifyHelper.ACTION_SHOW_NOTIFICATION:
            final int taskId = intent.getIntExtra(TaskNotifyHelper.EXTRA_TASK_ID, 0);
            notifyHelper.showNotification(taskId);
            break;

        default:
            break;
        }
    }
}