package com.ashberrysoft.leadertask.fragments;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Email.OrderInstruct;
import com.ashberrysoft.leadertask.providers.SyncProvider;
import com.ashberrysoft.leadertask.service.AuthService;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.LTPowerManager;

/**
 * Базовый фрагмент для всех фрагментов кроме фрагмента сообщений, просмотра задач, редактора задач.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public abstract class LTVisibleBaseFragment extends LTBaseFragment {

    // private MenuItem mRefreshItem;

    protected enum TaskAction {
        ADD, REMOVE, CHANGE, NONE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceConstants.ACTION_TASKS_TODAY);
        filter.addAction(ServiceConstants.ACTION_TASKS_INPUT);
        filter.addAction(ServiceConstants.ACTION_TASK_INSTRUCT);
        filter.addAction(ServiceConstants.ACTION_TASK_PROJECT);
        filter.addAction(ServiceConstants.ACTION_TASK_CATEGORY);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
        // update synchronization icon state
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {}

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ServiceConstants.ACTION_TASKS_TODAY)) {
                // clear tasks list
                TasksListFragment.clearData();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mSettings.setTaskMode(0);
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASKS_INPUT)) {
                // clear tasks list
                TasksListFragment.clearData();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mSettings.setTaskMode(1);
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASK_INSTRUCT)) {
                // clear tasks list
                TasksListFragment.clearData();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                final Email email = (Email) intent.getExtras().getSerializable(ServiceConstants.VALUE_EMAIL);
                mSettings.setTaskMode(email.getOrderInstruct() == OrderInstruct.INSTRUCTI ? TaskMode.ASSIGNED_BY_ME
                        : TaskMode.ASSIGNED_TO_ME);
                mSettings.setChooseEmail(email);
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASK_PROJECT)) {
                // clear tasks list
                TasksListFragment.clearData();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mSettings.setTaskMode(3);

                final Project pr = (Project) intent.getExtras().getSerializable(ServiceConstants.VALUE_PROJECT);
                mSettings.setChooseProject(pr);
            }

            else if (intent.getAction().equals(ServiceConstants.ACTION_TASK_CATEGORY)) {
                // clear tasks list
                TasksListFragment.clearData();
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mSettings.setTaskMode(4);

                final Category category = (Category) intent.getExtras()
                        .getSerializable(ServiceConstants.VALUE_CATEGORY);
                mSettings.setChooseCategory(category);
            }

            else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() == null) {
                    // finishSynchronize();
                    // showMessage(R.string.error_wrong_serv);
                }
            }
        }
    };

    public void synchronize() {
        mApp.setSyncingOngoingNow(true);
        LTPowerManager.getInstance(mApp).sleepLock();

        Account account = mSettings.getAccountHelper().getPrimaryAccount();
        boolean accountNotExist = false;

        if (account == null || (accountNotExist = mSettings.getAccountHelper().getAccountByType(0) == null)) {
            if (accountNotExist) {
                mSettings.setAccountHelper();
            }

            account = new Account(mSettings.getUserName(), AuthService.ACCOUNT_TYPE);
            mSettings.getAccountHelper().addPrimaryAccount(account, true);
            mSettings.setSyncPeriod(mSettings.getAutosyncModeInt());
        }

        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and manual sync settings
         */
        ContentResolver//
                .requestSync(mSettings.getAccountHelper().getPrimaryAccount(),//
                        SyncProvider.PROVIDER_NAME, settingsBundle);
    }

    /**
     * 
     * @return true - visible bootom panel
     */
    public abstract boolean showTitleBar();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.show_hide_make_task:
            if (mApp.getSettings().isMakeTaskHide()) {
                Toast.makeText(getActivity(), R.string.menu_show_make_task, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.menu_hide_make_task, Toast.LENGTH_LONG).show();
            }
            // ((HomeActivity) getActivity()).setBlockingProcess(true, null);
            mApp.getSettings().setMakeTaskHide(!mApp.getSettings().isMakeTaskHide());

            Intent intentTask = new Intent();
            // set action for updating tasks/subtasks list
            intentTask.setAction(ServiceConstants.ACTION_NOTIFYDATASETCHANGED);
            // send intent
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentTask);
            intentTask = new Intent();
            // set action for updating sliding menu
            intentTask.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
            // send intent
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentTask);
            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }
}