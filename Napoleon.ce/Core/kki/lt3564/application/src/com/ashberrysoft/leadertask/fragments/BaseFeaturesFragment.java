package com.ashberrysoft.leadertask.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public abstract class BaseFeaturesFragment extends BaseFragment<LTApplication, LTSettings>//
        implements DialogInterface.OnClickListener {

    protected enum Operation {
        NONE, SET_DATA, NOTIFY_ADAPTER, UP, DOWN, RIGHT, LEFT, DELETE, START_BLOCK, STOP_BLOCK, SHOW_KEYBOARD
    }

    // VIEW
    protected ListView mListView;

    // VALUE's
    protected LTApplication mApp;
    protected LTSettings mSettings;
    protected DbHelper mDbHelper;
    protected Handler mHandler;
    private OperationHolder mOperationHolder;
    private boolean needToSave;
    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mSettings = LTSettings.getInstance(getActivity());

        setHasOptionsMenu(true);
        mApp = (LTApplication) getActivity().getApplicationContext();
        mDbHelper = DbHelper.getInstance(getActivity());
        mHandler = getHandler();
        mOperationHolder = new OperationHolder();
        needToSave = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle b) {
        mListView = (ListView) inflater.inflate(R.layout.edit_features_fragment, container, false);
        if (getListViewHeader() != null) {
            mListView.addHeaderView(getListViewHeader());
        }
        FrameLayout footerLayout = (FrameLayout) getLayoutInflater(b).inflate(R.layout.footer_view, null);
        FrameLayout footerDivider = (FrameLayout) getLayoutInflater(b).inflate(R.layout.footer_divider, null);
        mListView.addFooterView(footerDivider, null, true);
        mListView.addFooterView(footerLayout, null, false);

        mListView.setAdapter(getAdapter());
        registerForContextMenu(mListView);
        return mListView;
    }

    protected abstract View getListViewHeader();

    protected abstract ListAdapter getAdapter();

    @Override
    public void onResume() {
        super.onResume();
        getView().setBackgroundColor(mSettings.isThemeDark() ? Color.BLACK : Color.WHITE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mApp = (LTApplication) activity.getApplicationContext();
        mSettings = LTSettings.getInstance(mApp);
        mDbHelper = DbHelper.getInstance(mApp);
    }

    protected IntentFilter getIntentFilter() {
        return null;
    }

    protected abstract int getActionBarTitle();

    protected abstract int getActionBarIcon();

    /**
     * if <b>true</b> then "R.id.add_feature"=visible, "R.id.save_feature"=invisible<br/>
     * if <b>false</b> then inverse
     */
    protected abstract boolean getVisibilitySwitchMode();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                inputHide(null);
                SaveOrNot();
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    getActivity().onBackPressed();
                }
                return true;

            case R.id.dont_save:
                inputHide(null);
                needToSave = false;
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    getActivity().onBackPressed();
                }
                return true;

            case R.id.save_feature:
                inputHide(null);
                Save();
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    getActivity().onBackPressed();
                }
                return true;


            default:
                return onOtherFeatureClick(item);
        }
    }

    protected ActionBar getActionBar() {
        return mActionBar;
    }

    protected void setActionBarTitle(String title) {
        mActionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
    }

    protected void onBroadcastReceive(Context context, Intent intent) {}

    protected abstract boolean onAddFeatureClick();

    protected abstract boolean onSaveFeatureClick();

    protected abstract boolean onOtherFeatureClick(MenuItem item);

    protected void startFragment(Fragment f) {
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(FeaturesActivity.FRAGMENT_CONTAINER, f);
        ft.addToBackStack(f.getClass().getName());
        ft.commit();
    }

    protected void setBlockAtUI(boolean setBlock) {
        mHandler.sendEmptyMessage(setBlock ? Operation.START_BLOCK.ordinal() : Operation.STOP_BLOCK.ordinal());
    }

    protected void setBlock(boolean setBlock) {
        if (getActivity() == null) {
            return;
        }

        try {
            ((FeaturesActivity) getActivity()).setBlockingProcess(setBlock, null);

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    protected void adapterNotifyDataSetChanged() {
        mHandler.sendEmptyMessage(Operation.NOTIFY_ADAPTER.ordinal());
    }

    protected void showKeyboard(EditText et) {
        final Message message = new Message();
        message.what = Operation.SHOW_KEYBOARD.ordinal();
        message.obj = et;

        mHandler.sendMessageDelayed(message, 300);
    }

    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (Operation.values()[msg.what]) {
                    case NOTIFY_ADAPTER:
                        if (getAdapter() == null) {
                            return;
                        }

                        try {
                            ((BaseAdapter) getAdapter()).notifyDataSetChanged();

                        } catch (Exception e) {
                            Utils.toLog(e);
                        }
                        break;

                    case START_BLOCK:
                        setBlock(true);
                        break;

                    case STOP_BLOCK:
                        setBlock(false);
                        break;

                    case SHOW_KEYBOARD:
                        final EditText editText = (EditText) msg.obj;
                        editText.requestFocus();
                        editText.setSelection(editText.length());

                        final InputMethodManager imm = (InputMethodManager) mApp
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    protected void inputHide(View v) {
        if (v == null) {
            v = mListView;
        }

        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                v.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v){ }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActionBarTitle());
        }
        catch (Exception e) {

        }
        menu.clear();

        if(mSettings.ismOnBackpressedSave()){ // сохраняем по кнопке назад
            inflater.inflate(R.menu.edit_task_menu, menu);
            inflater.inflate(R.menu.edit_features_menu, menu);

            if (getVisibilitySwitchMode()) {
                menu.removeItem(R.id.dont_save);
                menu.removeItem(R.id.save_feature);
            } else {
                menu.removeItem(R.id.save_feature);
                //menu.removeItem(R.id.dont_save); // убрать эту строку чтобы вернуть как кнопку "не сохранять"
            }
        }
        else
        {
            inflater.inflate(R.menu.edit_features_menu, menu);

            if (getVisibilitySwitchMode()) { // НЕ сохраняем по кнопке назад
                menu.removeItem(R.id.save_feature);
                menu.removeItem(R.id.dont_save);
            }
            else {
                //menu.removeItem(R.id.save_feature);
                menu.removeItem(R.id.dont_save);
            }
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            onDialogPositiveButton();
        }
    }

    protected abstract void onDialogPositiveButton();

    protected void showSimpleDialog(int title, int message) {
        Utils.getSimpleDialog(getActivity(), this, title, message);
    }

    protected void setMenuItemEnabled(android.view.MenuItem item, boolean enabled) {
        item.setVisible(enabled);
    }

    protected abstract boolean runOperationInBackground(Operation operation);

    protected final class OperationHolder {

        private final Runnable mRunnable;
        private Operation mOperation;

        public OperationHolder() {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (runOperationInBackground(mOperation)) {
                        adapterNotifyDataSetChanged();
                    }

                    setBlockAtUI(false);
                }
            };
        }

        public void runThread(Operation operation) {
            new Thread(getRunnable(operation)).start();
        }

        public Runnable getRunnable(Operation operation) {
            mOperation = operation;
            return mRunnable;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    protected OperationHolder getOperationHolder() {
        return mOperationHolder;
    }

    public void  Save()
    {
        onSaveFeatureClick();
        Utils.startSync( (LTApplication) getActivity().getApplicationContext());
        needToSave = false;
    }

    public void  SaveOrNot()
    {
        if(mSettings.ismOnBackpressedSave() && needToSave && !getVisibilitySwitchMode()) {
            Save();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SaveOrNot();
    }
}