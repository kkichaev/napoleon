package com.ashberrysoft.leadertask.interfaces;

import com.ashberrysoft.leadertask.domains.ordinary.Task;

public interface FragmentsCommunicationInterface {
    public void onTaskDeleted(Task task, boolean isFromDialog, boolean isChangeStatusDirectly);

    public void onTaskAdded(Task task);

    public void onTaskChanged(Task task);
}