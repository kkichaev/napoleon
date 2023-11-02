package com.ashberrysoft.leadertask.modern.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.network.DownloadFile;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.adapter.EditTaskFilesAdapter;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskCategoriesDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskChronometryDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskContactsDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskEmailsDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskMarkerDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskPerformerDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskProjectDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskRepeatDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskStatusDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskTermDialog;
import com.ashberrysoft.leadertask.modern.dialog.TaskTermDialogNew;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.MenuLoader;
import com.ashberrysoft.leadertask.modern.view.EditTaskHeadersHolder;
import com.ashberrysoft.leadertask.modern.view.list_item.TaskFileListItemView.OnTaskFileListener;
import com.ashberrysoft.leadertask.utils.ChronoHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;
import com.ashberrysoft.leadertask.views.CommentListItem;
import com.ashberrysoft.leadertask.views.CustomEditTextNew;

import static com.ashberrysoft.leadertask.R.id.date;
import static com.ashberrysoft.leadertask.R.id.date_of_create;
import static com.ashberrysoft.leadertask.R.id.fact;

public class EditTaskFragment extends BaseFragment//
        implements View.OnClickListener, OnTaskFileListener, DialogInterface.OnClickListener, CustomEditTextNew.BackPressedListener, EditTaskHeadersHolder.OnMarkerClick {

    private static final String ACTION_DOWNLOAD_RESULT = "ACTION_DOWNLOAD_RESULT";
    private static final String ACTION_MEDIA_RESULT = "ACTION_MEDIA_RESULT";
    private static final String EXTRA_TASK_FILE = "EXTRA_TASK_FILE";
    private static final String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";
    private static final String EXTRA_TEMP_FILE = "EXTRA_TEMP_FILE";
    private static final int FILE_SELECT_CODE = 0;
    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat SDF_24_HOUR = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat SDF_12_HOUR = new SimpleDateFormat("hh:mm a");
    public static final String CLASS_PATH = EditTaskFragment.class.getSimpleName();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onClickBack() {

    }

    @Override
    public void onClickMarker(String marker) {
        if (!marker.isEmpty() && marker.equals("all")) {
            TaskMarkerDialog.newInstance(this, mTask.getUidMarker()).showDialog(getFragmentManager());
        } else {
            if (!Utils.equals(marker, mTask.getUidMarker())) {
                mTask.setUidMarker(marker);
                mTask.setUsnFieldUidMarker(mTask.getUsnFieldUidMarker() + 1);
                mTask.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(getApp(), marker));

                mHeadersHolder.setMarker(marker);
            }
        }
    }

    private enum RequestCode {
        NONE, CAMERA, GALLERY, ANY_FILE;
    }

    // VIEW
    private EditTaskHeadersHolder mHeadersHolder;

    // VALUE's
    private LTask mTask;
    private boolean mTaskNew;
    private List<TaskFile> mTaskFilesDeleted;

    private boolean mKeyboardShowed;
    private TaskFile mTempTaskFile;
    private File mTempFile;
    private RequestCode mRequestCode;
    private static Uri mSharedUri;
    ListView mListView;
    TextView mDateOfCreate;
    RelativeLayout mSaveTaskFooter;
    Button mSaveTask;
    // VIEW
    //LinearLayout mLinearLayoutComments;
    private CustomEditTextNew mEditTextComment;
    private Button mAddComment;
    private boolean isReassign;
    public static boolean isShowAll;
    //private boolean isAddingComment;
    // ADAPTER
    //private CommentsAdapter mAdapterComments;

    // ADAPTER
    private EditTaskFilesAdapter mAdapter;

    public static EditTaskFragment newInstance(Uri uri) {
        mSharedUri = uri;
        return new EditTaskFragment();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final List<TaskFile> taskFiles;
        if (b != null) {
            mTask = (LTask) b.getSerializable(EditTaskActivity.EXTRA_TASK);
            mTaskNew = b.getBoolean(EditTaskActivity.EXTRA_TASK_NEW, false);
            taskFiles = (List<TaskFile>) b.getSerializable(EditTaskActivity.EXTRA_TASK_FILES);
            mTaskFilesDeleted = (List<TaskFile>) b.getSerializable(EditTaskActivity.EXTRA_TASK_FILES_DELETED);

            mRequestCode = RequestCode.values()[b.getInt(EXTRA_REQUEST_CODE, 0)];
            mTempFile = (File) b.getSerializable(EXTRA_TEMP_FILE);

            mKeyboardShowed = true;

        } else {
            final EditTaskActivity activity = (EditTaskActivity) getActivity();

            mTask = activity.getTask();
            mTaskNew = activity.isTaskNew();
            taskFiles = activity.getTaskFiles();
            mTaskFilesDeleted = activity.getTaskFilesDeleted();

            mRequestCode = RequestCode.NONE;

            mKeyboardShowed = !mTaskNew;
        }

        mAdapter = new EditTaskFilesAdapter(SortFilesList(taskFiles), this);
        //mAdapterComments = new CommentsAdapter(getActivity(), mMessages);
        if(mSharedUri != null) {
            final File file = new File(mSharedUri.toString().replace(SharedStrings.CONTENT_FILE, SharedStrings.EMPTY));
            mSharedUri = null;
            addTaskFile(file);
        }
        mHeadersHolder = new EditTaskHeadersHolder(getActivity(), mTaskNew, this);
        mHeadersHolder.setData(mTask, b);
        mHeadersHolder.setViewsOnClickListener(this);

        //TaskNotifyHelper.getInstance(getApp()).cancelNotify(mTask);
    }

    public boolean checkTaskName() {
        return mHeadersHolder.checkTaskName();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.fragment_edit_task_new, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        v.setBackgroundColor(getResources().getColor(R.color.white));

        mListView = (ListView) v.findViewById(R.id.edit_list_view);
        mSaveTask = (Button) v.findViewById(R.id.b_save_task);
        mSaveTaskFooter = (RelativeLayout) v.findViewById(R.id.save_task_footer);

        mHeadersHolder.addHeaders(mListView);
        mHeadersHolder.addHeadersContacts(mListView);

        mSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditTaskActivity) getActivity()).saveTask();
            }
        });

        View viewCreation = getActivity().getLayoutInflater().inflate(R.layout.task_date_creation, null);
        if (viewCreation != null) {
            mDateOfCreate = (TextView) viewCreation.findViewById(date_of_create);
            long timeTmp = mTaskNew ? TimeHelper.currentTimeMillisWithoutTimeZone() : mTask.getCreateTime();

            Date tmpDate2 = new Date(timeTmp);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeTmp);
            calendar.add(Calendar.HOUR_OF_DAY, tmpDate2.getTimezoneOffset()/60);

            long time = calendar.getTimeInMillis();
            mDateOfCreate.setText(getActivity().getResources().getString(R.string.task_create) + " " + SDF_DATE.format(time) + ", "+ (DateFormat.is24HourFormat(getContext()) ? SDF_24_HOUR.format(time) : SDF_12_HOUR.format(time)));
            mListView.addFooterView(viewCreation);
        }

        if (EditTaskActivity.mTaskMessages != null) {
            if (EditTaskActivity.mTaskMessages.size() <= 2 ){
                isShowAll = true;
            } else{
                isShowAll = false;
            }
        } else {
            isShowAll = true;
        }

        if (EditTaskActivity.mTaskMessages != null && EditTaskActivity.mTaskMessages.size() > 0 && !mTaskNew) {
            int count = 0;
            if (!isShowAll) {
                count = EditTaskActivity.mTaskMessages.size() - 2;
            }
            CommentListItem view1 = null;
            CommentListItem view2 = null;
            final ArrayList <CommentListItem> allView = new ArrayList<>();
            for (int i = count; i < EditTaskActivity.mTaskMessages.size(); i++) {
                TaskMessage message = EditTaskActivity.mTaskMessages.get(i);
                CommentListItem view = new CommentListItem(getActivity());
                view.setData(message);
                //mLinearLayoutComments.addView(view);
                if (!isShowAll && i == EditTaskActivity.mTaskMessages.size() - 2) {
                    view1 = view;
                }
                if (!isShowAll && i == EditTaskActivity.mTaskMessages.size() - 1) {
                    view2 = view;
                }
                allView.add(view);
            }
            if (!isShowAll) {
                final View viewShowAll = getActivity().getLayoutInflater().inflate(R.layout.footer_show_comments, null);
                TextView textView = (TextView) viewShowAll.findViewById(R.id.show_all_comments);
                final CommentListItem finalView = view1;
                final CommentListItem finalView1 = view2;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isShowAll = true;
                        mListView.removeFooterView(viewShowAll);
                        mListView.removeFooterView(finalView);
                        mListView.removeFooterView(finalView1);
                        allView.clear();

                        for (int i = 0; i < EditTaskActivity.mTaskMessages.size(); i++) {
                            TaskMessage message = EditTaskActivity.mTaskMessages.get(i);
                            CommentListItem viewTmp = new CommentListItem(getActivity());
                            viewTmp.setData(message);

                            allView.add(viewTmp);
                        }

                        for (CommentListItem v : allView) {
                            mListView.addFooterView(v);
                        }
                    }
                });

                mListView.addFooterView(viewShowAll);

            }
            for (CommentListItem view : allView) {
                mListView.addFooterView(view);
            }
        }

        mEditTextComment = (CustomEditTextNew) v.findViewById(R.id.comments_new);
        mEditTextComment.setListener(this);
        mEditTextComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String str = ""+s;
                if(str.trim().length() > 0) {
                    mAddComment.setVisibility(View.VISIBLE);
                } else {
                    mAddComment.setVisibility(View.GONE);
                }
            }
        });

        mAddComment = (Button) v.findViewById(R.id.add_comment);
        mAddComment.setVisibility(View.GONE);
        mAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage();
            }
        });
        mListView.setAdapter(mAdapter);
    }

    private void resetComment(TaskMessage message) {
        CommentListItem view = new CommentListItem(getActivity());
        view.setData(message);

        mListView.addFooterView(view);
    }

    public void needToAddComment() {
        //if (!mTaskNew) {
            //что-то ввели в строку воода коммента
            final String message = mEditTextComment.getText().toString().trim();
            // или добавили коммент
            if (message.length() > 0) {
                addMessage();
            }
        //}
    }

    public void addMessage() {
        final String message = mEditTextComment.getText().toString().trim();
        mEditTextComment.setText(null);

        if (message.length() > 0) {
            final Date date = new Date();
            UUID taskUID ;
            if (mTask != null) {
                if (mTask.getUid() != null) {
                    taskUID = UUID.fromString(mTask.getUid());
                } else {
                    taskUID = null;
                }
            } else {
                taskUID = null;
            }

            final TaskMessage taskMessage = new TaskMessage(UUID.randomUUID(), getSettings().getUserName(), message, taskUID, date, date, false, 0, 0, 0);

            if (EditTaskActivity.mTaskMessages != null) {
                EditTaskActivity.mTaskMessages.add(taskMessage);
            } else {
                EditTaskActivity.mTaskMessages = new ArrayList<>();
                EditTaskActivity.mTaskMessages.add(taskMessage);
            }

            //((EditTaskActivity) getActivity()).setTaskMessages(((EditTaskActivity) getActivity()).mTaskMessages);
            ((EditTaskActivity) getActivity()).newAddedComment();

            Utils.hideInput(mEditTextComment);
            mEditTextComment.clearFocus();
            resetComment(taskMessage);
            mTask.setUsnEntity(0);
            mListView.smoothScrollToPosition(mListView.getAdapter().getCount() -1);

            ((EditTaskActivity) getActivity()).saveOneMassage();
            Utils.startSync(getApp());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mKeyboardShowed) {
            mKeyboardShowed = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        b.putSerializable(EditTaskActivity.EXTRA_TASK, mTask);
        b.putBoolean(EditTaskActivity.EXTRA_TASK_NEW, mTaskNew);
        b.putSerializable(EditTaskActivity.EXTRA_TASK_FILES, (Serializable) mAdapter.getData());
        b.putSerializable(EditTaskActivity.EXTRA_TASK_FILES_DELETED, (Serializable) mTaskFilesDeleted);

        b.putInt(EXTRA_REQUEST_CODE, mRequestCode.ordinal());
        b.putSerializable(EXTRA_TEMP_FILE, mTempFile);

        mHeadersHolder.onSavedInstanceState(b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_layout:
                if(isPerformerOrCustomerInTask(mTask)) {
                    TaskStatusDialog.newInstance(this, mTask, false).showDialog(getFragmentManager());
                }
                break;

            case R.id.prop_reassing:
                isReassign = true;
                TaskPerformerDialog.newInstance(this, mTask, false).showDialog(getFragmentManager());
                break;
            case R.id.performer:
                isReassign = false;
                TaskPerformerDialog.newInstance(this, mTask, false).showDialog(getFragmentManager());
                break;

            case R.id.term:
                //if (((EditTaskActivity) getActivity()).isUsingNewTermDialog()) {
                    TaskTermDialogNew.newInstance(this, mTask).showDialog(getFragmentManager());
                /*} else {
                    TaskTermDialog.newInstance(this, mTask, false).showDialog(getFragmentManager());
                }*/
                break;

            case R.id.chronometry:
                if(isPerformerOrCustomerInTask(mTask)) {
                    TaskChronometryDialog.newInstance(this, mTask).showDialog(getFragmentManager());
                }
                break;

            case R.id.marker:
                TaskMarkerDialog.newInstance(this, mTask.getUidMarker()).showDialog(getFragmentManager());
                break;

            case R.id.project:
                TaskProjectDialog.newInstance(this, mTask, false).showDialog(getFragmentManager());
                break;

            case R.id.categories:
                TaskCategoriesDialog.newInstance(this, mTask).showDialog(getFragmentManager());
                break;

            case R.id.files:
                if (getSettings().getLicenseType() == getSettings().LICENSE_TYPE_FREE ||
                        getSettings().getLicenseType() == getSettings().LICENSE_TYPE_NONE){
                    LicenseDialog.newInstance().showDialog(getActivity().getFragmentManager());
                }
                else {
                    AlertDialogAddFiles();
                }
                break;

            case R.id.contacts:
                if (getSettings().getLicenseType() == getSettings().LICENSE_TYPE_FREE ||
                        getSettings().getLicenseType() == getSettings().LICENSE_TYPE_NONE){
                    LicenseDialog.newInstance().showDialog(getActivity().getFragmentManager());
                }
                else {
                    TaskContactsDialog.newInstance(this, mTask).showDialog(getFragmentManager());
                }
                break;

            case R.id.term_repeat:
                TaskRepeatDialog.newInstance(this, mTask).showDialog(getFragmentManager());
                break;

            case R.id.access:
                TaskEmailsDialog.newInstance(this, mTask).showDialog(getFragmentManager());
                break;
            default:
                break;
        }
    }

    public void addFileClick() {
        if (getSettings().getLicenseType() == getSettings().LICENSE_TYPE_FREE ||
                getSettings().getLicenseType() == getSettings().LICENSE_TYPE_NONE){
            LicenseDialog.newInstance().showDialog(getActivity().getFragmentManager());
        }
        else {
            AlertDialogAddFiles();
        }
    }

    @Override
    protected IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DOWNLOAD_RESULT);
        filter.addAction(ACTION_MEDIA_RESULT);

        return filter;
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_DOWNLOAD_RESULT:
                setBlocking(false);
                if (intent.hasExtra(EXTRA_TASK_FILE)) {
                    mAdapter.notifyDataSetChanged();

                    final TaskFile file = (TaskFile) intent.getSerializableExtra(EXTRA_TASK_FILE);
                    onTaskFileClick(file, false);

                } else {
                    Utils.showToast(getActivity(), R.string.error_file_not_downloaded);
                }
                break;

            case ACTION_MEDIA_RESULT:
                setBlocking(false);
                if (intent.hasExtra(EXTRA_TASK_FILE)) {
                    final File file = (File) intent.getSerializableExtra(EXTRA_TASK_FILE);
                    addTaskFile(file);

                } else {
                    Utils.showToast(getActivity(), R.string.t_error_file_saving);
                }
                break;

            default:
                super.onBroadcastReceive(context, intent);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(rcvUpdate, new IntentFilter(UPDATE_UI_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity().isFinishing())
            getActivity().unregisterReceiver(rcvUpdate);
    }

    public final  static String UPDATE_UI_ACTION = "UPDATE_UI_ACTION";
    public final static String EMAIL = "email";

    public BroadcastReceiver rcvUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReceivingObjects(TaskPerformerDialog.CODE, intent.getStringExtra(EMAIL));
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    public void onReceivingObjects(int code, Object... objects) {
        switch (code) {
            case TaskStatusDialog.CODE:
                final int statusNew = (int) objects[0];
                LTask mTaskOld = mTask.clone();

                mTask.setStatus(statusNew);
                mTask.setUsnFieldStatus(mTask.getUsnFieldStatus() + 1);

                if (mTaskOld.getStatus() != mTask.getStatus()) {
                    // если статусы не равны и было готово к сдаче и статус поменялся -

                    if (mTaskOld.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                        // если был в работе и изменился
                        if (mTask.getPlan() != 0) {
                            if (mTask.getEmailPerformer().equals(getSettings().getUserName())) {
                                //TaskNotifyHelper.getInstance(getContext()).deleteOldTaskNotifyChrono(mTask, TaskNotifyHelper.ChonoCode);
                            }
                        }

                        //int wasInWork = (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone()-mTask.getInWorkTime())/1000);
                        int wasInWork = (int)ChronoHelper.instance.getFactTiming(mTask.getTime(), mTask.getInWorkTime());
                        mTask.setTime(wasInWork);
                        mTask.setUsnTime(mTask.getUsnTime() + 1);
                    } else {
                        if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                            // если стало в работе а был другой
                            mTask.setInWorkTime(TimeHelper.getInstance().currentTimeMillisWithoutTimeZone());
                            mTask.setUsnInWorkTime(mTask.getUsnInWorkTime() + 1);

                            if (mTask.getPlan() != 0) {
                                //TaskNotifyHelper.getInstance(getContext()).updateTaskNotifyChrono(mTask, TaskNotifyHelper.ChonoCode);
                            }
                        }
                    }
                    //

                    mHeadersHolder.setStatus(mTask);
                }
                break;

            case TaskChronometryDialog.CODE:
                final LTask task2 = (LTask) objects[0];
                mTask.setPlan(task2.getPlan());
                mTask.setUsnPlan(task2.getUsnPlan());
                if (task2.getTime() == 0) {
                    mTask.setTime(task2.getTime());
                    mTask.setInWorkTime(task2.getInWorkTime());

                    mTask.setUsnTime(task2.getUsnTime());
                    mTask.setUsnInWorkTime(task2.getUsnInWorkTime());
                }
                mHeadersHolder.setChronometry(task2);
                break;

            case TaskPerformerDialog.CODE:
                final String performer = (String) objects[0];
                if (isReassign) {
                    isReassign = false;
                    //
                    LTask newTask = TaskHelper.createNewTaskWithParams(getSettings().getUserName(), performer.toLowerCase(), 0, mTask.getUid(), mTask.getUidProject(), mTask.getCategories(), null);
                    newTask.setName(mTask.getName());
                    newTask.setComment(mTask.getComment());
                    //copyTaskFiles(UUID.fromString(newTask.getUid()));
                    startActivity(EditTaskActivity.newInstance(getActivity(), newTask, true, false));
                    //
                } else {
                    if (!performer.equalsIgnoreCase(mTask.getEmailPerformer())) {
                        mTask.setEmailPerformer(performer.toLowerCase());
                        mTask.setUsnFieldEmailPerformer(mTask.getUsnFieldEmailPerformer() + 1);

                        if (!mTask.getEmailPerformer().equals(getSettings().getUserName()) &&
                                mTask.getEmailCustomer().equals(getSettings().getUserName())) {
                            if (mTask.getPerformerReaded() != false) {
                                mTask.setPerformerReaded(false);
                                mTask.setUsnFieldPerformerReaded(mTask.getUsnFieldPerformerReaded() + 1);
                            }
                        } else {
                            if (mTask.getPerformerReaded() != true) {
                                mTask.setPerformerReaded(true);
                                mTask.setUsnFieldPerformerReaded(mTask.getUsnFieldPerformerReaded() + 1);
                            }
                        }

                        mTask.setPerformTime(System.currentTimeMillis());
                        mTask.setUsnFieldPerformtime(mTask.getUsnFieldPerformtime() + 1);

                        mHeadersHolder.setPerformer(mTask);
                    }
                }
                break;

            /*case TaskTermDialog.CODE:
                final LTask task = (LTask) objects[0];
                if (!TimeHelper.termsEquals(task, mTask)) {
                    mTask.setTermBegin(task.getTermBegin());
                    mTask.setTermEnd(task.getTermEnd());

                    mTask.setTermBeginCustomer(task.getTermBeginCustomer());
                    mTask.setTermEndCustomer(task.getTermEndCustomer());

                    mTask.setUsnFieldTerm(task.getUsnFieldTerm() + 1);
                    mTask.setUsnFieldCustomerTerm(task.getUsnFieldCustomerTerm() + 1);

                    mHeadersHolder.setTerm(mTask);
                    MenuLoader.getInstance(getApp()).resetCalendar();
                }
                break;*/

            case TaskTermDialogNew.CODE:
                final LTask taskT = (LTask) objects[0];
                if (!TimeHelper.termsEquals(taskT, mTask)) {
                    mTask.setTermBegin(taskT.getTermBegin());
                    mTask.setTermEnd(taskT.getTermEnd());

                    mTask.setTermBeginCustomer(taskT.getTermBeginCustomer());
                    mTask.setTermEndCustomer(taskT.getTermEndCustomer());

                    mTask.setUsnFieldTerm(taskT.getUsnFieldTerm() + 1);
                    mTask.setUsnFieldCustomerTerm(taskT.getUsnFieldCustomerTerm() + 1);

                    mHeadersHolder.setTerm(mTask);
                    MenuLoader.getInstance(getApp()).resetCalendar();
                }
                break;

            case TaskMarkerDialog.CODE:
                final String marker = (String) objects[0];
                if (!Utils.equals(marker, mTask.getUidMarker())) {
                    mTask.setUidMarker(marker);
                    mTask.setUsnFieldUidMarker(mTask.getUsnFieldUidMarker() + 1);
                    mTask.setMarkerOrder(Marker.getMarkerOrderFromLowerUid(getApp(), marker));

                    mHeadersHolder.setMarker(marker);
                }
                break;

            case TaskProjectDialog.CODE:
                final Project project = (Project) objects[0];
                final String uidProject = project == null ? null : String.valueOf(project.getId()).toUpperCase();

                if (!Utils.equals(uidProject, mTask.getUidProject())) {
                    mTask.setUidProject(uidProject);
                    mTask.setUsnFieldUidProject(mTask.getUsnFieldUidProject() + 1);

                    mHeadersHolder.setProject(project);
                }
                break;

            case TaskCategoriesDialog.CODE:
                final String categories = (String) objects[0];
                if (!Utils.equals(categories, mTask.getUidProject())) {
                    mTask.setCategories(TextUtils.isEmpty(categories) ? null : categories);
                    mTask.setUsnFieldCategories(mTask.getUsnFieldCategories() + 1);

                    mHeadersHolder.setCategories(categories);
                }
                break;

            case TaskContactsDialog.CODE:
                final String contacts = (String) objects[0];
                mTask.setContacts(TextUtils.isEmpty(contacts) ? null : contacts);
                mTask.setUsnFieldContacts(mTask.getUsnFieldContacts() + 1);

                mHeadersHolder.setContacts(contacts);
                break;

            case TaskRepeatDialog.CODE:
                final LTask taskSeries = (LTask) objects[0];
                mTask = taskSeries;
                mHeadersHolder.setTerm(mTask);
                break;

            case TaskEmailsDialog.CODE:
                final String emails = (String) objects[0];
                mTask.setEmails(TextUtils.isEmpty(emails) ? null : emails);
                mTask.setUsnFieldListMembers(mTask.getUsnFieldListMembers() + 1);

                mHeadersHolder.setEmails(emails);
                break;
            default:
                super.onReceivingObjects(code, objects);
                break;
        }
    }

    private List<TaskFile> copyTaskFiles(UUID taskUid) {
        final List<TaskFile> taskFiles;
        //
        int taskHash = TaskHelper.getHashFromUid(mTask.getUid().toLowerCase());
        List<TaskFile> files = TaskFileCache.getInstance(getApp()).find(taskHash);
        //
        taskFiles = new ArrayList<TaskFile>(0);

        if (files == null) {
            files = new ArrayList<TaskFile>(0);
        }

        if ( files.size() == 0) {
            return files;
        }

        final File appFolder = ( getApp()).getAppFolder();
        int count = 1;

        for (TaskFile file : files) {

            try {
                if (!file.isFileExist()) {
                    // скачать
                    try {
                        new SimpleDownloadFileThread(getApp(), file).start();
                    } finally {
                        Utils.FileWorker.copyFile(file.getFileName(), appFolder, "c_"+file.getFileName());
                    }
                } else {
                    Utils.FileWorker.copyFile(file.getFileName(), appFolder, "c_"+file.getFileName());
                }

            } catch (IOException e) {
                Utils.toLog(e);
            }

            TaskFile tmpFile = new TaskFile(UUID.randomUUID(), taskUid, UUID.randomUUID(), "c_"+file.getFileName(), file.getFileSize(), getSettings().getUserName(), count++ );
            taskFiles.add(tmpFile);
        }
        TaskFileCache.getInstance(getApp()).updateCache(taskFiles);
        return taskFiles;
    }

    public void addTaskFile(File file) {
        final long order;
        if (mAdapter.getCount() == 0) {
            order = 1;

        } else {
            order = mAdapter.getData().get(mAdapter.getCount() - 1).getOrder() + 1;
        }

        final TaskFile taskFile = new TaskFile(null, null, null,//
                file.getName(), file.length(), LTSettings.getInstance().getUserName(), order);

        mAdapter.getData().add(taskFile);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Utils.toLog("onActivityResult");
        if (data != null) {
            Utils.toLog(data);
        }

        if (resultCode != Activity.RESULT_OK) {
            if (mTempFile != null) {
                mTempFile.delete();
                mTempFile = null;
            }
            return;
        }

        final RequestCode code = mRequestCode;
        mRequestCode = RequestCode.NONE;

        switch (code) {
            case CAMERA:
                if (mTempFile != null && mTempFile.exists()) {
                    Utils.exifRotate(mTempFile.getPath());
                    mTempFile = new File(mTempFile.getPath());
                    addTaskFile(mTempFile);
                }
                mTempFile = null;
                break;

            case GALLERY:
                if (data != null) {
                setBlocking(true);
                new CopyFromThread(getApp(), data.getData(), getActivity()).start();
            }
                break;
            case ANY_FILE:
                if (data != null) {
                    setBlocking(true);
                    new CopyFromThread(getApp(), data.getData(), getActivity()).start();
                }
                break;


            default:
                break;
        }
    }

    @Override
    protected Boolean showSlidingMenu() {
        return false;
    }

    private void imageCapture() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Utils.showToast(getActivity(), R.string.t_error_external_storage);
            return;
        }

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            Utils.showToast(getActivity(), R.string.t_error_camera);
            return;
        }

        mTempFile = new File(getApp().getAppFolder(), FileWorker.getNewCurrentPictureFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));

        startActivityForResult(intent, RequestCode.CAMERA);
    }

    private void galleryImage() {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(SharedStrings.MIME_TYPE_IMAGE);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_chooser_image)), RequestCode.GALLERY);
    }

    public void startActivityForResult(Intent intent, RequestCode code) {
        mRequestCode = code;
        super.startActivityForResult(intent, code.ordinal());
    }

    @Override
    public void onTaskFileClick(TaskFile file, boolean remove) {
        if (remove) {
            mTempTaskFile = file;
            Utils.getSimpleDialog(getActivity(), this, R.string.d_remove_file_title, R.string.d_remove_file_message);

        } else {
            if (file.isFileExist()) {
                final File f = getFileFromTaskFile(file);
                if (f.exists()) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f), FileWorker.getFileMimeType(f));
                    startActivity(Intent.createChooser(intent, getString(R.string.title_chooser_open)));

                } else {
                    file.setFileExist(false);
                    new TaskFileNotExistsThread(getApp(), file.getFileId()).start();

                    Utils.showToast(getActivity(), file.isWeakLink() ? R.string.t_error_file_was_not_uploaded : R.string.t_error_file_not_exist);
                    mAdapter.notifyDataSetChanged();
                }

            } else {
                if (file.isWeakLink()) {
                    Utils.showToast(getActivity(), R.string.t_error_file_was_not_uploaded);

                } else {
                    downloadFile(file);
                }
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE && mTempTaskFile != null) {
            if (mTempTaskFile.isWeakLink()) {
                getFileFromTaskFile(mTempTaskFile).delete();

            } else {
                mTempTaskFile.setDeleteObject(true);
                mTaskFilesDeleted.add(mTempTaskFile);
            }

            mAdapter.getData().remove(mTempTaskFile);
            mAdapter.notifyDataSetChanged();
            mTempTaskFile = null;
        }
    }

    private File getFileFromTaskFile(TaskFile file) {
        return new File(getApp().getAppFolder(), file.getFileName());
    }

    private void downloadFile(TaskFile file) {
        if (Utils.isNetworkAvailable(getApp())) {
            Utils.showToast(getActivity(), R.string.t_start_download_file);
            setBlocking(true);

            new DownloadFileThread(getApp(), file).start();

        } else {
            Utils.showToast(getActivity(), R.string.error_internet_access);
        }
    }

    private static final class DownloadFileThread extends Thread {

        private final LTApplication mApp;
        private final TaskFile mFile;

        public DownloadFileThread(LTApplication app, TaskFile file) {
            super(DownloadFileThread.class.getSimpleName());

            mApp = app;
            mFile = file;
        }

        @Override
        public void run() {
            super.run();

            final String fileUid = String.valueOf(mFile.getFileId());
            final String fileName = mFile.getFileName();

            final Intent intent = new Intent(ACTION_DOWNLOAD_RESULT);
            try {
                new DownloadFile(mApp, fileUid, fileName, mApp.getSettings().getUserProfile(), mApp.getAppFolder(), 0).downloadFile();
                mFile.setFileExist(true);

                intent.putExtra(EXTRA_TASK_FILE, mFile);

            } catch (Exception e) {

            } finally {
                LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
            }
        }
    }

    private static final class SimpleDownloadFileThread extends Thread {

        private final LTApplication mApp;
        private final TaskFile mFile;

        public SimpleDownloadFileThread(LTApplication app, TaskFile file) {
            super(SimpleDownloadFileThread.class.getSimpleName());

            mApp = app;
            mFile = file;
        }

        @Override
        public void run() {
            super.run();

            final String fileUid = String.valueOf(mFile.getFileId());
            final String fileName = mFile.getFileName();

            try {
                new DownloadFile(mApp, fileUid, fileName, mApp.getSettings().getUserProfile(), mApp.getAppFolder(), 0).downloadFile();

            } catch (Exception e) {

            }
        }
    }


    private static final class TaskFileNotExistsThread extends Thread {

        private final ContentResolver mCr;
        private final String mTaskFileUid;

        public TaskFileNotExistsThread(Context context, UUID taskFileUid) {
            mCr = context.getContentResolver();
            mTaskFileUid = String.valueOf(taskFileUid);
        }

        @Override
        public void run() {
            super.run();

            final ContentValues cv = new ContentValues(1);
            cv.put(TaskFileContract.FILE_EXIST, 0);

            mCr.update(TaskFileContract.CONTENT_URI, cv, TaskFileContract.selectionFieldFileUid(mTaskFileUid), null);
        }
    }

    private static final class CopyFromThread extends Thread {

        private final LTApplication mApp;
        public final Uri mUri;
        private final Activity mActivity;

        public CopyFromThread(LTApplication app, Uri uri, Activity activity) {
            super();

            mApp = app;
            mUri = uri;
            mActivity = activity;
        }

        @Override
        public void run() {
            super.run();

            final Intent intent = new Intent(ACTION_MEDIA_RESULT);
            final String path = getImagePathFromInputStreamUri(mUri, mActivity);

            try {
                if (path == null) {
                    return;
                }

                Utils.exifRotate(path);

                final File file = FileWorker.copyAnyFile(path, mApp.getAppFolder());

                intent.putExtra(EXTRA_TASK_FILE, file);

            } catch (Exception e) {

            } finally {
                LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
            }
        }

        public static String getImagePathFromInputStreamUri(Uri uri, Activity activity) {
            InputStream inputStream = null;
            String filePath = null;

            if (uri.getAuthority() != null) {
                try {
                    inputStream = activity.getContentResolver().openInputStream(uri); // context needed
                    File photoFile = createTemporalFileFrom(inputStream, activity, uri);

                    filePath = photoFile.getPath();

                } catch (FileNotFoundException e) {
                    // log
                } catch (IOException e) {
                    // log
                }finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return filePath;
        }

        private static File createTemporalFileFrom(InputStream inputStream, Activity activity, Uri mUri) throws IOException {
            File targetFile = null;

            if (inputStream != null) {
                int read;
                byte[] buffer = new byte[8 * 1024];

                targetFile = createTemporalFile(activity, mUri);
                OutputStream outputStream = new FileOutputStream(targetFile);

                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();

                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return targetFile;
        }

        private static File createTemporalFile(Activity activity, Uri mUri) {
            return new File(activity.getExternalCacheDir(), getFileName(mUri, activity)); // context needed
        }

        public static String getFileName(Uri uri, Activity activity) {
            String result = null;
            if (uri.getScheme().equals("content")) {
                Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result;
        }
    }

    private void AlertDialogAddFiles()
    {
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            final String[] mCatsName = {getString(R.string.m_add_from_camera), getString(R.string.m_add_from_gallery), getString(R.string.choose_new_file)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setItems(mCatsName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    ChooseFileTipe(item);
                }
            });
            builder.setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    private void ChooseFileTipe(int item)
    {
        switch (item) {
            case 0:
                imageCapture();
                break;
            case 1:
                galleryImage();
                break;
            case 2:
                showFileChooser();
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_new_file)), RequestCode.ANY_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            //Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<TaskFile> SortFilesList(List<TaskFile> taskFiles)
    {
        // сортировка файлов
        for (int i=0;i<taskFiles.size(); i++)
        {
            TaskFile Item = taskFiles.get(i);
            int itemIndex = i;
            for (int j=i+1; j<taskFiles.size(); j++)
            {
                if(Item.getEmailCreator().equals(getApp().getSettings().getUserName())
                        && !Item.getEmailCreator().equals(taskFiles.get(j).getEmailCreator()))
                {
                    //меняем местами
                    TaskFile tempItem = taskFiles.get(itemIndex);
                    taskFiles.set(itemIndex, taskFiles.get(j));
                    taskFiles.set(j, tempItem);

                    Item = taskFiles.get(j);
                    itemIndex = j;
                }
            }
        }
        return taskFiles;
    }

    private boolean isPerformerOrCustomerInTask(LTask task)
    {
        String userName = LTSettings.getInstance().getUserName();
        boolean isCustomer = userName.equals(task.getEmailCustomer());
        boolean isPerformer = userName.equals(task.getEmailPerformer());
        if(isCustomer || isPerformer) {
            return true;
        } else {
            return false;
        }
    }

}