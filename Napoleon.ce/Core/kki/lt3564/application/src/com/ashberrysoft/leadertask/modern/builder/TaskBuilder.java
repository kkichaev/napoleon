package com.ashberrysoft.leadertask.modern.builder;

import java.util.Date;
import java.util.UUID;

import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskBuilder {

    // VALUE's
    private final Task mTask;

    public TaskBuilder() {
        mTask = new Task();
    }

    public TaskBuilder setDefaults() {
        mTask.setId(UUID.randomUUID());
        mTask.setStatus(TaskStatus.NOT_BEGIN);
        mTask.setReaded(true);

        final Date date = new Date(Utils.getCurrentTimeWithSavings());
        mTask.setCreationTime(date);
        mTask.setCompleteTime(date);
        mTask.setPerformTime(date);

        return this;
    }

    public TaskBuilder setCustomerAndPeformer(String customerAndPerformer) {
        mTask.setCustomer(customerAndPerformer);
        mTask.setPerformer(customerAndPerformer);

        return this;
    }

    public Task getTask() {
        return mTask;
    }
}