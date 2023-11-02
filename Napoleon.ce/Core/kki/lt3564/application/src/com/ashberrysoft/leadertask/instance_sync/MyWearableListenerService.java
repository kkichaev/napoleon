package com.ashberrysoft.leadertask.instance_sync;

import android.database.Cursor;
import android.widget.Toast;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.mApp;
import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.syncWearLogIn;


public class MyWearableListenerService extends WearableListenerService {
    String nodeId;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        nodeId = messageEvent.getSourceNodeId();
        String message = messageEvent.getPath();
        String username = LTSettings.getInstance().getUserName();
        if (message != null && message.contains("check_login")) {
            syncWearLogIn();
        }

        if (message != null && message.contains("addNewAssign")) {
            //  taskName+"addNewAssign"+toUser
            String taskName = message.substring(0, message.indexOf("addNewAssign"));
            String toUser = message.substring(message.indexOf("addNewAssign")+"addNewAssign".length(), message.length());
            // toUser
            List<Employee> mEmployees = DbHelper.getListEmployees(mApp);
            String userToEmail = "";

            for (Employee emp : mEmployees) {
                if (emp.getName().trim().toLowerCase().equals(toUser.trim().toLowerCase())) {
                    userToEmail = emp.getEmail();
                    break;
                }
            }
            if (!userToEmail.isEmpty()) {
                final LTask task = TaskHelper.createNewTaskWithParams(username, userToEmail, 0, null, null, null, null);
                task.setName(taskName);

                new TaskSaveHelper(false, getApplicationContext(), task, true, null, null,//
                        0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).run();
            }
        }
        if (message != null && message.contains("addTask")) {
            String taskName = message.substring("addTask".length(), messageEvent.getPath().length());
            final LTask task = TaskHelper.createNewTaskWithParams(username, username, 0, null, null, null, null);
            task.setName(taskName);
            long term = TimeHelper.currentTimeMillisWithoutTimeZone();
            //
            final Calendar calendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
            calendar.setTimeInMillis(term);

            TimeHelper.roundCalendar(calendar, false);
            task.setTermEnd(calendar.getTimeInMillis());

            TimeHelper.roundCalendar(calendar, true);
            task.setTermBegin(calendar.getTimeInMillis());
            //
            new TaskSaveHelper(false, getApplicationContext(), task, true, null, null,//
                    0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).run();
        }
        if (message != null && message.contains("leadSync")) {
            LeaderTaskSyncService.syncWearFull();
        }
        if (message != null && message.contains("cancelTask")) {
            String uid = message.substring("cancelTask".length(), messageEvent.getPath().length());
            Cursor c = null;
            try {
                c = mApp.getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LionMetaData.LTaskContract.Uid + "='"+uid+"'", null, null);
                if (c.getCount() > 0) {
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        LTask taskOld = new LTask(c);
                        LTask taskNew = new LTask(c);
                        if (taskNew.getEmailPerformer().equals(username) && !taskNew.getEmailCustomer().equals(username)) {
                            // если поручена мне задача
                            taskNew.setStatus(Status.TASK_READY.getStatusCode());
                        } else {
                            // иначе завершить
                            taskNew.setStatus(Status.TASK_COMPLETED.getStatusCode());
                        }
                        taskNew.setUsnEntity(0);
                        taskNew.setUsnFieldStatus(taskNew.getUsnFieldStatus()+1);
                        new TaskSaveHelper(false, getApplicationContext(), taskNew, false, new ArrayList<TaskMessage>(0), taskOld, 0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
                    }
                }
            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }
}
