package com.ashberrysoft.leadertask.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.domains.ordinary.Task;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class UniqueLogger {

    public static final String FILE_NAME = "PutTasks.log";

    // SINGLETON
    private static UniqueLogger sInstance;

    // VALUE's
    private Context mContext;
    private File mFile;
    private StringBuilder mStringBuilder;
    private FileOutputStream mOutputStream;

    public static UniqueLogger getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UniqueLogger.class) {
                if (sInstance == null) {
                    sInstance = new UniqueLogger(context);
                }
            }
        }
        return sInstance;
    }

    private UniqueLogger(Context context) {
        mContext = context;
        mFile = new File(((LTApplication) mContext.getApplicationContext()).getAppFolderLogs(), FILE_NAME);
        mStringBuilder = new StringBuilder();
    }

    private void writeInFile() {
        if (openFile()) {
            try {
                mOutputStream.write(mStringBuilder.toString().getBytes());
            } catch (Exception e) {
                Utils.toLog(e);
            } finally {
                closeFile();
            }
        }
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

    private void closeFile() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
                mOutputStream = null;
            } catch (IOException e) {}
        }
        mStringBuilder.delete(0, mStringBuilder.length());
    }

    public void toLog(String s) {
        // mStringBuilder.append(Task.SDF.format(new Date()));
        // mStringBuilder.append(CursorySyncLogger.COLON);
        // mStringBuilder.append(s);
        //
        // writeInFile();
    }

    public void toLog(Throwable e) {
        if (e == null) {
            return;
        }

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

    public boolean deleteLog() {
        return mFile != null ? mFile.delete() : false;
    }
}