package com.ashberrysoft.leadertask.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.adapters.CommentsAdapter;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.exceptions.InputFormException;

public class TaskCommentsFragment extends LTBaseFragment {

    // VIEW's
    private EditText mEditText;
    private Task mTask;

    // ADAPTER
    private CommentsAdapter mAdapter;

    public static TaskCommentsFragment newInstance(Task task) {
        final Bundle b = new Bundle(1);
        b.putSerializable(IPCConstants.EXTRA_TASK, task);

        final TaskCommentsFragment f = new TaskCommentsFragment();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        }
        mTask = (Task) bundle.getSerializable(IPCConstants.EXTRA_TASK);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(IPCConstants.EXTRA_TASK, mTask);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_comments_task, container, false);
        v.setBackgroundColor(mSettings.isThemeDark() ? Color.BLACK : Color.WHITE);

        //final ListView listView = (ListView) v.findViewById(R.id.listComments);
        setListViewParams(v.findViewById(R.id.linear_layout));

        mEditText = (EditText) v.findViewById(R.id.comments_new);
        mEditText.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Utils.showInput(mApp, v);
                    if (getActivity() != null) {
                        ((HomeActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                }
                return false;
            }
        });

        List<TaskMessage> messagesList = null;
        try {
            messagesList = DbHelper.getInstance(getActivity()).getTaskMessageDao().queryBuilder().where().eq(TaskMessage.FIELD_TASK_UID, mTask.getId()).query();

        } catch (SQLException e) {
            messagesList = new ArrayList<TaskMessage>(0);
        }

        mAdapter = new CommentsAdapter(getActivity(), messagesList);
        //listView.setAdapter(mAdapter);

        mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                // && event.getAction() == KeyEvent.ACTION_DOWN
                ) {
                    addMessage();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        Utils.hideInput(mApp, mEditText);
        super.onPause();
    }

    private void addMessage() {
        try {
            InputFormException.assertTrue(mEditText.getText().length() >= 1, R.string.error_comment);
            final Date date = new Date();

            final TaskMessage taskMessage = new TaskMessage(UUID.randomUUID(), mApp.getSettings().getUserName(), mEditText.getText().toString(), mTask.getId(),
                    date, date, false, 0, 0, 0);

            //mAdapter.addItem(taskMessage);
            try {
                DbHelper.getInstance(getActivity()).getTaskMessageDao().createOrUpdate(taskMessage);
                mEditText.setText(null);

                final Intent intent = new Intent();
                intent.setAction(ServiceConstants.ACTION_TASK_MESSAGE);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                Utils.hideInput(mApp, mEditText);

            } catch (SQLException e) {
                showError(e.toString());
            }

        } catch (InputFormException e) {
            Utils.showToast(getActivity(), e.getMessageResource());
        }
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    private void setListViewParams(View layout) {
        layout.setBackgroundColor(mSettings.isThemeDark() ? Color.BLACK : Color.WHITE);

        if (mApp.isTablet()) {
            return;
        }

        final int width = getResources().getDimensionPixelSize(R.dimen.fetn_width);
        final Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final int displayWidth = display.getWidth();

        if (width > displayWidth) {
            return;
        }

        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
        layout.setLayoutParams(lp);
    }
}