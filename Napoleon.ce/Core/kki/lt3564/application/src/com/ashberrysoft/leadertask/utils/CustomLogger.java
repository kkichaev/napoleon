package com.ashberrysoft.leadertask.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.domains.ordinary.Task;

public class CustomLogger {

    // VALUE's
    private File mFile;
    private StringBuilder mStringBuilder;
    private FileOutputStream mOutputStream;

    public static CustomLogger newInstance(Context context, String name) {
        return new CustomLogger(context, name);
    }

    private CustomLogger(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            name = Task.SDF.format(new Date());
        }

        mFile = new File(((LTApplication) context.getApplicationContext()).getAppFolderLogs(), name);
        mStringBuilder = new StringBuilder();
        openFile();
    }

    private boolean openFile() {
        try {
            mOutputStream = new FileOutputStream(mFile, true);
            return true;
        } catch (Exception e) {
            Utils.toLog(e);
            return false;
        }
    }

    public void closeFile() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
                mOutputStream = null;
            } catch (IOException e) {}
        }
    }

    private void writeInFile() {
        try {
            mOutputStream.write(mStringBuilder.toString().getBytes());
        } catch (Exception e) {
            Utils.toLog(e);
        } finally {
            mStringBuilder.delete(0, mStringBuilder.length());
        }
    }

    public void toLog(String s) {
        mStringBuilder.append(s);
        mStringBuilder.append(SharedStrings.NEW_LINE_C);

        writeInFile();
    }

    public void toLog(Throwable e) {
        mStringBuilder.append(Task.SDF.format(new Date()));
        mStringBuilder.append(CursorySyncLogger.ERROR);
        mStringBuilder.append(e.getClass().getName());

        for (StackTraceElement s : e.getStackTrace()) {
            mStringBuilder.append(SharedStrings.NEW_LINE_C);
            mStringBuilder.append(s.toString());
        }

        mStringBuilder.append(CursorySyncLogger.ERROR);

        writeInFile();
    }
}