package com.ashberrysoft.leadertask.service;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.SynchronizationTask;
import com.ashberrysoft.leadertask.providers.SyncProvider;
import com.ashberrysoft.leadertask.utils.LTPowerManager;

/**
 * This class demonstrates work of the sync adapter.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    LTApplication mApp;

        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {

        final LTSettings settings = LTSettings.getInstance(getContext());
        mApp = (LTApplication) getContext();
        if (settings.getUserProfile().isValid()) {
            mApp.setSyncingOngoingNow(true);
            new SynchronizationTask(getContext(), settings.getUserProfile()).run();
        }
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }

}