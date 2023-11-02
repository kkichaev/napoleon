package com.grsoft.ads.database;

import android.content.Context;
import android.content.Intent;

import com.grsoft.ads.AdsService;
import com.grsoft.ads.dataobjects.TaskResponce;
import com.grsoft.ads.dataobjects.impl.TaskResponceImpl;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskHitchingEx extends TaskHitching {
    List<TaskQuery> applayTask = new ArrayList<TaskQuery>();

    public TaskHitchingEx(Context context){
        super(context);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        super.onRead(rawObject);
    }

    @Override
    public void onEnd() {
        super.onEnd();

        long time = Calendar.getInstance().getTimeInMillis();

        for(TaskQuery t : applayTask){
            TaskResponceImpl impl = new TaskResponceImpl();
            impl.init(context, t);
            TaskResponce tr = impl.getData();
            tr.solution = TaskQuery.APPLY;
            tr.created = new Date(time);

            time += 1000;

            impl.write();
            impl.close();
        }

        if (applayTask.size() > 0)
            context.sendBroadcast(new Intent(AdsService.SYNC_ACTION));
    }

    @Override
    public void postRead(TaskQuery task) {
        super.postRead(task);

        if (task.solution == TaskQuery.NEW){
            task.solution = TaskQuery.APPLY;
            applayTask.add(task);
        }
    }
}
