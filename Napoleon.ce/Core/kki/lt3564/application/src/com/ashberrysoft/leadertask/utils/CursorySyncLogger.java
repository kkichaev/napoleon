package com.ashberrysoft.leadertask.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.domains.ordinary.Task;

import static com.ashberrysoft.leadertask.R.id.date;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CursorySyncLogger {

    public static final String FILE_NAME = "sync.log";
    public static final String COLON = " : ";
    public static final String EQUALS = " = ";
    public static final String ERROR = "\n-------------\n";
    public static final SimpleDateFormat SDF_DURATION = new SimpleDateFormat("mm:ss.SSS", Locale.getDefault());

    // SINGLETON
    private static CursorySyncLogger sInstance;

    // VALUE's
    private final LTApplication mApp;
    private final File mFile;
    private final StringBuilder mStringBuilder;
    private final Date mDate;

    private FileOutputStream mOutputStream;

    public static CursorySyncLogger getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CursorySyncLogger.class) {
                if (sInstance == null) {
                    sInstance = new CursorySyncLogger(context);
                }
            }
        }
        return sInstance;
    }

    private CursorySyncLogger(Context context) {
        mApp = (LTApplication) context.getApplicationContext();

        mFile = new File(mApp.getAppFolderLogs(), FILE_NAME);
        mStringBuilder = new StringBuilder();
        mDate = new Date();
    }

    public void openFile() {
        mApp.getAppFolderLogs();
        mFile.delete();

        try {
            mOutputStream = new FileOutputStream(mFile, true);

        } catch (Exception e) {
            Utils.toLog(e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void closeFile() {
        try {
            mOutputStream.close();
            mOutputStream = null;

        } catch (Exception e) {}
    }

    public boolean isStreamOpen() {
        return mOutputStream != null;
    }

    private void writeToFile(byte[] bytes) {
        try {
            mOutputStream.write(bytes);

        } catch (Exception e) {}
    }

    public void toLog(Throwable e) {
        clearStringBuilder();

        mStringBuilder.append(ERROR);
        mStringBuilder.append(e.getMessage());
        mStringBuilder.append(SharedStrings.NEW_LINE_C);
        mStringBuilder.append(e.getClass().getName());
        for (StackTraceElement s : e.getStackTrace()) {
            mStringBuilder.append(SharedStrings.NEW_LINE_C);
            mStringBuilder.append(s.toString());
        }
        mStringBuilder.append(ERROR);

        writeToFile(getStringBuilderBytes());
    }

    public void toLog(String s) {
        writeToFile(appendTimeStamp(s));
    }

    public void toLog(String s, Date date) {
        //writeToFile(appendDuration(s, date));
    }

    public void toLogSimple(String s) {
        writeToFile(getBytes(s));
    }

    private byte[] getBytes(String s) {
        clearStringBuilder();
        mStringBuilder.append(s);
        return getStringBuilderBytes();
    }

    private byte[] appendTimeStamp(String s) {
        try {
            clearStringBuilder();
            mDate.setTime(System.currentTimeMillis());

            mStringBuilder.append(Task.SDF.format(mDate));
            mStringBuilder.append(COLON);
            mStringBuilder.append(s);
        } catch (Exception e) {
            return getStringBuilderBytes();
        } finally {
            return getStringBuilderBytes();
        }
    }

    private byte[] appendDuration(String s, Date date) {
        byte[] full = null;
        try {
            clearStringBuilder();

            mStringBuilder.append(SDF_DURATION.format(date));
            mStringBuilder.append(EQUALS);
            mStringBuilder.append(s);
                String s1 = mStringBuilder.toString();
                full = s1.getBytes();
                full = mStringBuilder.toString().getBytes();
            } catch (Exception e) {

            }
        return /*appendTimeStamp(full)*/ full;
    }

    private void clearStringBuilder() {
        Utils.clearStringBuilder(mStringBuilder);
    }

    private byte[] getStringBuilderBytes() {
        byte[] bytes = null;
        try {

            mStringBuilder.append(SharedStrings.NEW_LINE_C);
            bytes = mStringBuilder.toString().getBytes();
        } catch (Exception e) {

        } finally {
            if (bytes != null) {
                return bytes;
            } else {
                return new byte[0];
            }
        }
    }
}