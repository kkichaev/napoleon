package com.ksoft.dms;

public class SetTaskDateDlg extends  AlarmDlg{
    @Override
    protected int getLayout() {
        return R.layout.set_task_time_dlg;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.set_date_time);
    }
}
