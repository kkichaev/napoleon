package com.ashberrysoft.leadertask.enums;

import com.ashberrysoft.leadertask.R;

public enum TaskStatusBehavior {

    NONE(R.string.on_status_none), FINISH(R.string.on_status_finish), SELECT(R.string.on_status_select);

    int mStringId;

    TaskStatusBehavior(int stringId) {
        mStringId = stringId;
    }

    public int getStringId() {
        return mStringId;
    }
}