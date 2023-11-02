package com.ashberrysoft.leadertask.application;

import java.io.File;
import java.util.HashMap;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.BaseSOAP;
import com.ashberrysoft.leadertask.domains.ordinary.SyncInfo;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.SetBlocking;
import com.ashberrysoft.leadertask.modern.exception.LeaderExceptionHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.LTPowerManager;
import com.ashberrysoft.leadertask.utils.Utils;
import com.facebook.FacebookSdk;
import com.v2soft.AndLib.application.BaseApplication;

@ReportsCrashes(formKey = "4802b638-2b25-4af0-8086-aaf74e148ac0", mailTo = "911@leadertask.com")
public class LTApplication extends BaseApplication<LTSettings> {



    // VALUE's
    private boolean mIsSync;
    private String mTextPlainSend;
    private boolean mIsTablet;
    private File mAppFolder;
    private File mAppLogsFolder;
    private File mAppZipFolder;

    @Override
    public void onCreate() {
        super.onCreate();

        // new Thread(new LionEntityGenerator(this)).start();
        // new Thread(getTesting()).start();
        initialization();
    }

    public static HashMap<String , BaseActivity> mBackStackActivities
            = new HashMap<String, BaseActivity>();

    private void initialization() {
        DbHelper.getInstance(this);

        if (Config.DEBUG) {
            initDevMode();
            ACRA.init(this);
        }

        mIsTablet = isTabletDevice();

        SyncInfo.initialization(this);
        LTPowerManager.getInstance(this);
        TimeHelper.init(this);

        Utils.changeLocale(getResources(), getSettings().getLanguageLocale());

        try {
        if (getSettings().isCreateSetBlocking()) {
            getSettings().setCreateSetBlocking(false);
            SetBlocking.create(getApplicationContext());
        } else {
            SetBlocking.update(getApplicationContext(), false);
        }
        } catch(Exception e) {
            e.printStackTrace();
        }

        LeaderExceptionHelper.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @SuppressWarnings("unused")
    private Runnable getTesting() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    test();

                } catch (Exception e) {
                    Utils.toLog(e);
                }
            }

            private void test() throws Exception {}
        };
    }

    private boolean isTabletDevice() {
        final int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected LTSettings createApplicationSettings() {
        return LTSettings.getInstance(this);
    }

    public void setTheme(Activity activity) {
        activity.setTheme(R.style.leadertask_AppTheme_Light);
    }

    public void setSyncingOngoingNow(boolean isSync) {
        if (isSync != mIsSync) {
            mIsSync = isSync;
            LocalBroadcastManager.getInstance(this).sendBroadcast(
                    new Intent(IPCConstants.ACTION_SYNCHRONIZATION_STATE_CHANGED));
        }
    }

    public boolean isSync() {
        return mIsSync;
    }

    public void cancelSynchronize() {
        setSyncingOngoingNow(false);
        BaseSOAP.setIsSynchronize(false);
    }

    public String getTextPlainSend() {
        return mTextPlainSend;
    }

    public void setTextPlainSend(String textPlainSend) {
        mTextPlainSend = textPlainSend;
    }

    public boolean isTablet() {
        return mIsTablet;
    }

    public File getAppFolder() {
        if (mAppFolder == null) {
            synchronized (LTApplication.class) {
                if (mAppFolder == null) {
                    mAppFolder = Utils.FileWorker.initializateAppFolder(this);
                }
            }
        }
        return mAppFolder;
    }

    public File getAppFolderLogs() {
        if (mAppLogsFolder == null) {
            synchronized (LTApplication.class) {
                if (mAppLogsFolder == null) {
                    mAppLogsFolder = Utils.FileWorker.createAppLogsFolder(getAppFolder());
                }
            }
        }
        return mAppLogsFolder;
    }
    
    public File getAppFolderZips() {
        if (mAppZipFolder == null) {
            synchronized (LTApplication.class) {
                if (mAppZipFolder == null) {
                	mAppZipFolder = Utils.FileWorker.createAppZipsFolder(getAppFolder());
                }
            }
        }
        return mAppZipFolder;
    }

    public void clearAppFolder() {
        if (mAppFolder == null) {
            return;
        }

        final File[] appFolder = mAppFolder.listFiles();

        mAppFolder = null;
        if (appFolder == null || appFolder.length == 0) {
            return;
        }

        for (File file : appFolder) {
            file.delete();
        }
    }

    public void clearAppFolderLogs() {
        if (mAppLogsFolder == null) {
            return;
        }

        final File[] appFolderLogs = mAppLogsFolder.listFiles();

        mAppLogsFolder = null;
        if (appFolderLogs == null || appFolderLogs.length == 0) {
            return;
        }

        for (File file : appFolderLogs) {
            if (!file.getName().equals("syncFull.log")) {
                file.delete();
            }
        }
    }

    @Override
    protected void initializeInjector() {}

    @Override
    protected Object onCreateApplicationModule() {
        return null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}