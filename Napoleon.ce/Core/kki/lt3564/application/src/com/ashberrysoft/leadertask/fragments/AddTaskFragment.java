package com.ashberrysoft.leadertask.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.BaseSlidingActivity;
import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.dialogs.SetPerformerDialog;
import com.ashberrysoft.leadertask.dialogs.SetTermDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.interfaces.FragmentsCommunicationInterface;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.LTCalendarView;
import com.ashberrysoft.leadertask.views.LinedEditText;
/**
 * Экран для добавления задачи
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 *         При добавлении задачи должны быть предустановленны параметры в зависимости от того в каком экране/при каком
 *         фильтре мы нажали добавить. Если мы жмем добавить в фильтре Сегодня, то предустановлен срок сегодня/выбранная
 *         дата (причем я могу изменить его в окне добавления, тогда задача добавится, но не попадет в текущий список),
 *         если в фильтре Я поручил: емайл – то подставляется в поле поручить емайл (опять же могу поменять), если в
 *         фильтре проект/категория – то подставляется проект/категория (это поле в текущей версии не видимо в экране
 *         добавить задачу), если в фильтре входящие – то без параметров. Если мы добавляем задачу в экране подзадач, то
 *         подставляется родительская задача и проект родительской задачи (оба этих поля в текущей версии не видны в
 *         экране добавить задачу)
 * 
 */
public class AddTaskFragment extends LTVisibleBaseFragment {

    private static final String CLASS_PATH = AddTaskFragment.class.getName();
    private static final String EXTRA_NEW_TASK_TEXT = CLASS_PATH + "EXTRA_NEW_TASK_TEXT";
    private static final String NEWLINE = "\n\n";

    // INSTANCE
    private static AddTaskFragment sInstance;

    // VIEW's
    private ImageView mImgClient, mImgTerm;
    private TextView mTextTerm, mTextClient;
    private LinedEditText mEditText;
    private View mLine;

    // VALUE's
    private Handler mHandler;
    private Category mCategory;
    private long mLastClickTime = 0;
    private Task mTask;

    private static boolean sSavingNewTask;

    // LISTENER
    private FragmentsCommunicationInterface mListener;
/*
     public static AddTaskFragment newInstance(Fragment fragment, Date date, Email mail, UUID project,
     Category category, UUID parentUUID) {
    
     final Task task = new Task();
     task.setParentId(parentUUID);
     task.setId(UUID.randomUUID());
     task.setStatus(Status.TASK_NOT_BEGIN.getStatusCode());
     task.setProjectUid(project);
    
     if (date != null) {
     final Calendar dayBegin = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     final Calendar dayEnd = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     dayBegin.setTime(date);
     dayEnd.setTime(date);
    
     dayBegin.set(Calendar.HOUR_OF_DAY, 0);
     dayBegin.set(Calendar.MINUTE, 0);
     dayBegin.set(Calendar.SECOND, 0);
    
     dayEnd.set(Calendar.HOUR_OF_DAY, 23);
     dayEnd.set(Calendar.MINUTE, 59);
     dayEnd.set(Calendar.SECOND, 59);
    
     task.setTermBegin(dayBegin.getTime());
     task.setTermEnd(dayEnd.getTime());
     } else {
     task.setTermBegin(null);
     task.setTermEnd(null);
     }
    
     if (mail != null) {
     task.setPerformer(mail.getName());
     }
    
     final Bundle b = new Bundle();
     b.putSerializable(IPCConstants.EXTRA_TASK, task);
     b.putSerializable(IPCConstants.EXTRA_CATEGORY, category);
    
     final AddTaskFragment f = new AddTaskFragment();
     f.setArguments(b);
     f.setTargetFragment(fragment, 0);
    
     return f;
     }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (FragmentsCommunicationInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onFragmentToFragmentCommunication");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mHandler = new Handler();

        final Bundle b;
        if (savedInstanceState != null) {
            b = savedInstanceState;
        } else {
            b = getArguments();
        }

        mCategory = (Category) b.getSerializable(IPCConstants.EXTRA_CATEGORY);
        mTask = (Task) b.getSerializable(IPCConstants.EXTRA_TASK);
        mTask.setCustomer(mSettings.getUserName());

        sInstance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        final View v = inflater.inflate(R.layout.fragment_review_task, container, false);
        mEditText = (LinedEditText) v.findViewById(R.id.editText);
        mImgClient = (ImageView) v.findViewById(R.id.img_client);
        mImgTerm = (ImageView) v.findViewById(R.id.img_term);
        mTextTerm = (TextView) v.findViewById(R.id.text_term);
        mTextClient = (TextView) v.findViewById(R.id.text_client);
        mLine = (View) v.findViewById(R.id.viewPanel);

        if (b != null) {
            mEditText.setText(b.getString(IPCConstants.EXTRA_TASK_RAW_TEXT));
        }

        registerOnClickListener(new int[]{R.id.layout_performer, R.id.layout_set_term}, v);
        updateViews();

        if (getArguments() != null && getArguments().getBoolean(EXTRA_NEW_TASK_TEXT, false)) {
            mEditText.setText(mApp.getTextPlainSend());
            mApp.setTextPlainSend(null);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mEditText.requestFocus();
        Utils.showInput(mApp, mEditText);

        ((BaseSlidingActivity) getActivity()).disableAndSetSlidingMenu(true, true);
    }

    @Override
    public void onPause() {
        Utils.hideInput(mApp, mEditText);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        b.putSerializable(IPCConstants.EXTRA_TASK, mTask);
        b.putSerializable(IPCConstants.EXTRA_CATEGORY, mCategory);
        if (mEditText != null) {
            b.putString(IPCConstants.EXTRA_TASK_RAW_TEXT, mEditText.getText().toString());
        }
    }

    private void updateViews() {
        mEditText.setLineColor(getActivity());

        if (mSettings.isThemeDark()) {
            mLine.setBackgroundColor(Color.WHITE);
            mTextTerm.setTextColor(Color.WHITE);
            mTextClient.setTextColor(Color.WHITE);
            mEditText.setBackgroundColor(Color.BLACK);
            mEditText.setTextColor(Color.WHITE);
        } else {
            final int gray = getResources().getColor(R.color.gray);
            mEditText.setBackgroundColor(Color.WHITE);
            mEditText.setTextColor(Color.BLACK);
            mLine.setBackgroundColor(gray);
            mTextTerm.setTextColor(gray);
            mTextClient.setTextColor(gray);
        }

        if (mTask.getPerformer() == null || mTask.getPerformer().equals(mSettings.getUserName())) {
            mImgClient.setImageResource(R.drawable.user_gray);
            mTextClient.setText(R.string.task_assign);
        } else {
            mImgClient.setImageResource(R.drawable.user_green);
            mTextClient.setText(mTask.getPerformer());
        }

        showDate();
    }

    /**
     * Show task date.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    private void showDate() {
        if (mTask.getTermBegin() != null) {
            mImgTerm.setImageResource(R.drawable.term_orange_small_l);
            mTextTerm.setText(Utils.taskTermFormatter(getActivity(), mTask, true));
        } else {
            mTextTerm.setText(R.string.task_set_term);
            mImgTerm.setImageResource(R.drawable.term_gray_small);
        }
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }

        mLastClickTime = System.currentTimeMillis();
        switch (v.getId()) {
        case R.id.layout_performer:
            SetPerformerDialog.newInstance(this, mTask.getPerformer()).showDialog(getFragmentManager());
            break;

        case R.id.layout_set_term:
            SetTermDialog.newInstance(this, mTask).showDialog(getFragmentManager());
            break;
        }
    }

    @Override
    public void onFragmentResult(Object obj, int type) {
        switch (type) {
        case SetTermDialog.REQUEST_CODE:
            mTask = (Task) obj;
            break;

        case SetPerformerDialog.REQUEST_CODE:
            String name = (String) obj;
            mTask.setPerformer(name);
        default:
            break;
        }

        updateViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            getFragmentManager().popBackStack();
            return true;

        case R.id.save_task:
            final String title = mEditText.getText().toString();
            if (TextUtils.isEmpty(title.trim())) {
                Utils.showToast(getActivity(), R.string.error_empty_task_title);

            } else {
                setBlock(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            saveNewTask(title);
                        } catch (Exception e) {
                            mHandler.post(mStopRun);
                        }
                    }
                }).start();
            }
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveNewTask(String title) {
        sSavingNewTask = true;
        LTCalendarView.clearCalendarData(mApp);
        
        // get task title and comment
        final String[] array = title.split(NEWLINE);
        final StringBuilder builder = new StringBuilder();
        for (int position = 1; position < array.length; position++) {
            builder.append(array[position]);
            if (position < array.length - 1) {
                builder.append(NEWLINE);
            }
        }
        mTask.setName(array[0]);
        mTask.setComment(builder.toString());
        mTask.setReaded(true);

        final DbHelper dbHelper = DbHelper.getInstance(mApp);
        if (TextUtils.isEmpty(mTask.getPerformer())) {
            mTask.setPerformer(mSettings.getUserName());
        }

        // TODO check this with T3
        final Date date = new Date(Utils.getCurrentTimeWithSavings());
        mTask.setCreationTime(date);
        mTask.setPerformTime(date);
        mTask.setCompleteTime(date);

        /*
         * if we assign task for particular user which email doesn't exists in database, then we need to update sliding
         * menu
         */
        boolean isPerformerExists = false;
        final List<String> allEmails = dbHelper.getAllEmails();
        for (String email : allEmails) {
            if (mTask.getPerformer().equals(email)) {
                isPerformerExists = true;
                break;
            }
        }
        // add task to database
        dbHelper.addTask(mApp, mTask, mCategory, false);
        // send broadcast intent in order to update sliding menu "I assigned" section
        if (!isPerformerExists) {
            final Intent intent = new Intent();
            intent.setAction(ServiceConstants.ACTION_UPDATE_SLIDINGMENU_ASSIGNED_SECTION);
            LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
        }

        dbHelper.recalculateVerticalTaskSubtasks(mApp, mSettings.getUserName(), mTask);
//        Emp.updateTaskEmpSort(mApp, mTask);

        /*
         * get fragments back stack count: 1 - previous fragment is TasksListFragment >= 2 - previous fragment is
         * SubtasksListFragment
         */
        int fragmentsCount = getFragmentManager().getBackStackEntryCount();
        /*
         * current date with set time to 00:00:00.000
         */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        /*
         * begin selected data for filtering
         */
        Calendar beginFilterSelectedDate = Calendar.getInstance();
        beginFilterSelectedDate.setTimeInMillis(mSettings.getFilterSelectedDate());
        beginFilterSelectedDate.set(Calendar.HOUR_OF_DAY, 0);
        beginFilterSelectedDate.set(Calendar.MINUTE, 0);
        beginFilterSelectedDate.set(Calendar.SECOND, 0);
        beginFilterSelectedDate.set(Calendar.MILLISECOND, 0);
        /*
         * end selected data for filtering
         */
        Calendar endFilterSelectedDate = Calendar.getInstance();
        endFilterSelectedDate.setTimeInMillis(mSettings.getFilterSelectedDate());
        endFilterSelectedDate.set(Calendar.HOUR_OF_DAY, 23);
        endFilterSelectedDate.set(Calendar.MINUTE, 59);
        endFilterSelectedDate.set(Calendar.SECOND, 59);
        endFilterSelectedDate.set(Calendar.MILLISECOND, 999);
        /*
         * if fragments count greater than 2 (i.e. previous fragment is SubtasksListFragment), then transfer changes to
         * subtasks list independently of selected filter email/new task performer email, otherwise (i.e. previous
         * fragment is TasksListFragment) if current filter is "Today" and new task performer email equals to current
         * user email and task begin term not equals to null and lower or equals to end of current date or if current
         * filter is "Inbox" and new task performer email equals to current user email and task term begin equals to
         * null or if current filter is "I assigned" and selected email equals to new task performer email or if current
         * filter is one of "Projects", "Categories", then transfer changes to tasks list
         */
        if (fragmentsCount >= 2) {
            mListener.onTaskAdded(mTask);

        } else if ((mApp.getSettings().getTaskMode() == 0
                && mApp.getSettings().getUserName().equals(mTask.getPerformer()) && mTask.getTermBegin() != null && ((mTask
                .getTermEnd().getTime() >= beginFilterSelectedDate.getTimeInMillis() && mTask.getTermBegin().getTime() <= endFilterSelectedDate
                .getTimeInMillis()) || (beginFilterSelectedDate.getTimeInMillis() <= calendar.getTimeInMillis()
                && mTask.getTermEnd().getTime() < beginFilterSelectedDate.getTimeInMillis() && !dbHelper.hideTask(
                mTask, mSettings.getUserName()))))
                || (mApp.getSettings().getTaskMode() == 1
                        && mApp.getSettings().getUserName().equals(mTask.getPerformer()) && mTask.getTermBegin() == null)
                || (mApp.getSettings().getTaskMode() == 2 && mApp.getSettings().getChooseEmail().getName()
                        .equals(mTask.getPerformer()))
                || mApp.getSettings().getTaskMode() == 3
                || mApp.getSettings().getTaskMode() == 4) {
            mListener.onTaskAdded(mTask);
        }

        setIncreaseByParentTasksCount(sInstance != null ? sInstance.getTargetFragment() : getTargetFragment());

        mHandler.post(mStopRun);
    }

    @SuppressWarnings("deprecation")
	private void setIncreaseByParentTasksCount(Fragment fragment) {
        if (fragment != null && fragment instanceof SubtasksListFragment) {
            // delete fragment from back stack
            SubtasksListFragment.sIncreaseByParentTasksCount = 1;
        }
    }

    private Runnable mStopRun = new Runnable() {
        @Override
        public void run() {
            try {
                sSavingNewTask = false;

                if (sInstance != null) {
                    sInstance.setBlock(false);
                    sInstance.getFragmentManager().popBackStack();
                    sInstance = null;

                } else {
                    setBlock(false);
                    getFragmentManager().popBackStack();
                }
            } catch (NullPointerException e) {
                Utils.toLog(e);
            }
        }
    };

    public void setBlock(boolean setBlock) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    setBlock ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        super.setBlock(setBlock);
    };

    public static boolean isSavingNewTask() {
        return sSavingNewTask;
    }
}