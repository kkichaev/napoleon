package com.ashberrysoft.leadertask.modern.activity;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.cache.TaskFileCache;
import com.ashberrysoft.leadertask.modern.cache.TaskMessageCache;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.EditTaskFragment;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskFootstepHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.j256.ormlite.dao.Dao;

import static android.R.attr.padding;
import static android.R.attr.width;
import static com.ashberrysoft.leadertask.activities.FeaturesActivity.EDIT_CONTACTS_FRAGMENT_TAG;
import static com.ashberrysoft.leadertask.modern.fragment.TasksFragment.mTempTask;


public class EditTaskActivity extends BaseActivity {

    public static final String EXTRA_TASK = "EXTRA_TASK";
    public static final String EXTRA_TASK_UUID = "EXTRA_TASK_UUID";
    public static final String EXTRA_TASK_OLD = "EXTRA_TASK_OLD";
    public static final String EXTRA_TASK_NEW = "EXTRA_TASK_NEW";
    public static final String EXTRA_IS_DIALOG_FOR_TIME = "EXTRA_IS_DIALOG_FOR_TIME";
    public static final String EXTRA_TASK_MESSAGES = "EXTRA_TASK_MESSAGES";
    public static final String EXTRA_TASK_FILES = "EXTRA_TASK_FILES";
    public static final String EXTRA_TASK_FILES_DELETED = "EXTRA_TASK_FILES_DELETED";
    public static final String EXTRA_TASK_MESSAGES_SAVE_FROM = "EXTRA_TASK_MESSAGES_SAVE_FROM";
    private static Uri mSharedFileUri = null;
    public static final int PROPERTIES_CONTAINER = R.id.main_container_properties;

    private static OnUpdateSearchAdapter mListener;
    private static OnTaskWasAddedListener mListenerSaved;

    public interface OnTaskWasAddedListener {

        void onTaskSaved(LTask task);

    }

    public interface OnUpdateSearchAdapter {
        public void onUpdateTaskInAdapter(LTask updatedTask);
    }

    public static Intent newInstance(Context context, LTask task, boolean taskNew, boolean isDialogForEndTime) {
        final Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_TASK_NEW, taskNew);
        intent.putExtra(EXTRA_IS_DIALOG_FOR_TIME, isDialogForEndTime);

        return intent;
    }

    public static Intent newInstance(Context context, String uuid) {
        final Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_UUID, uuid);

        return intent;
    }

    public static Intent newInstance(Context context, LTask task, boolean taskNew, boolean isDialogForEndTime, OnTaskWasAddedListener listener) {
        mListenerSaved = listener;
        final Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_TASK_NEW, taskNew);
        intent.putExtra(EXTRA_IS_DIALOG_FOR_TIME, isDialogForEndTime);

        return intent;
    }

    public static Intent newInstance(Context context, LTask task, boolean taskNew, OnUpdateSearchAdapter listener) {
        mListener = listener;
        final Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_TASK_NEW, taskNew);
        return intent;
    }

    public static Intent newInstance(Context context, LTask task, boolean taskNew, Uri sharedFileUri) {
        final Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_TASK_NEW, taskNew);
        mSharedFileUri = sharedFileUri;

        return intent;
    }

    // VIEW
    private RelativeLayout mViewPager;

    // VALUE's
    private LTask mTask;
    private String mTaskUUID;
    private LTask mTaskSuper;
    private LTask mTaskOldSuper;
    private boolean mTaskNew;
    private boolean useDialogForEnd;
    private boolean wasUnreared;
    private boolean isAddedNewComment = false;
    public static List<TaskMessage> mTaskMessages;
    public static boolean isShowingAll;
    private List<TaskFile> mTaskFiles;
    private List<TaskFile> mTaskFilesDeleted;

    private LTask mTaskOld;
    private int mTaskMessagesSaveFrom;
    private Toolbar toolbar;
    //private static EditTaskFragment mEditFragment;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        if (b != null) {
            mTask = (LTask) b.getSerializable(EXTRA_TASK);
            mTaskUUID = b.getString(EXTRA_TASK_UUID);
            if (mTaskUUID != null) {
                //
                ArrayList <String> tasksUids = new ArrayList<>();
                tasksUids.add(mTaskUUID);
                ArrayList <LTask> tasks = getTasksFromUids(tasksUids);
                mTask = tasks.get(0);

            }
            mTaskNew = b.getBoolean(EXTRA_TASK_NEW, false);
            useDialogForEnd = b.getBoolean(EXTRA_IS_DIALOG_FOR_TIME, false);
            mTaskOld = (LTask) b.getSerializable(EXTRA_TASK_OLD);

            if (!mTaskNew) {
                mTaskMessages = (List<TaskMessage>) b.getSerializable(EXTRA_TASK_MESSAGES);
            }
            mTaskMessagesSaveFrom = b.getInt(EXTRA_TASK_MESSAGES_SAVE_FROM);
            mTaskFiles = (List<TaskFile>) b.getSerializable(EXTRA_TASK_FILES);
            mTaskFilesDeleted = (List<TaskFile>) b.getSerializable(EXTRA_TASK_FILES_DELETED);

        } else {
            final Intent intent = getIntent();
            mTask = (LTask) intent.getSerializableExtra(EXTRA_TASK);
            mTaskUUID = intent.getStringExtra(EXTRA_TASK_UUID);
            if (mTaskUUID != null) {
                //
                ArrayList <String> tasksUids = new ArrayList<>();
                tasksUids.add(mTaskUUID);
                ArrayList <LTask> tasks = getTasksFromUids(tasksUids);
                mTask = tasks.get(0);
            }

            if (mTask != null) {
                mTaskOld = mTask.clone();
                mTaskSuper = mTask.clone();
                mTaskOldSuper = mTaskSuper.clone();

                mTaskNew = intent.getBooleanExtra(EXTRA_TASK_NEW, false);
                useDialogForEnd = intent.getBooleanExtra(EXTRA_IS_DIALOG_FOR_TIME, false);

                mTaskFilesDeleted = new ArrayList<>();
                if (mTaskNew) {
                    mTaskFiles = new ArrayList<>();
                    if (mTask != null && mTask.getUid() != null) {
                        final int taskHash = TaskHelper.getHashFromUid(mTask.getUid().toLowerCase());
                        final List<TaskFile> taskFiles = TaskFileCache.getInstance(getApp()).find(taskHash);
                        mTaskFiles = taskFiles == null ? new ArrayList<TaskFile>() : new ArrayList<>(taskFiles);
                    }
                } else {
                    final int taskHash = TaskHelper.getHashFromUid(mTask.getUid().toLowerCase());

                    mTaskMessages = TaskMessageCache.getInstance(getApp()).find(taskHash);
                    if (mTaskMessages == null) {
                        mTaskMessages = new ArrayList<>();
                    }
                    mTaskMessagesSaveFrom = mTaskMessages.size();
                    wasUnreared = !mTaskSuper.getReaded();
                    if (!mTask.getReaded()) {
                        setUnreadTask();
                    }

                    final List<TaskFile> taskFiles = TaskFileCache.getInstance(getApp()).find(taskHash);
                    mTaskFiles = taskFiles == null ? new ArrayList<TaskFile>() : new ArrayList<>(taskFiles);
                }
            }
        }

        setContentView(R.layout.activity_edit_task);
        setActionBar();
        //

        mViewPager = (RelativeLayout) findViewById(R.id.main_container_properties);
        if (b == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(PROPERTIES_CONTAINER, EditTaskFragment.newInstance(mSharedFileUri), EditTaskFragment.CLASS_PATH);
            ft.commit();
        }

        mSharedFileUri = null;
    }

    private ArrayList <LTask> getTasksFromUids(ArrayList <String> stringArrayUids) {
        ArrayList <LTask> tasks = new ArrayList<>();

        Cursor cursor = null;
        StringBuilder selection = new StringBuilder();

        try {
            for (int i = 0; i < stringArrayUids.size() ; i++) {
                String taskUid = stringArrayUids.get(i);
                StringBuilder sb = new StringBuilder();
                selection.append(LeaderTaskProviderMetaData.SelectionKeeper.equals(sb, LionMetaData.LTaskContract.Uid, taskUid));
                if (i + 1 < stringArrayUids.size()) {
                    selection.append(SharedStrings.OR);
                }
            }
            cursor = getApp().getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, selection.toString(), null, null);
            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    LTask task = new LTask(cursor);
                    tasks.add(task);
                }
            }
        } finally {
            cursor.close();
            return tasks;
        }
    }

    public boolean isUsingNewTermDialog() {
        return useDialogForEnd;
    }

    private Drawable resizeImage(int resId)
    {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        // calculate the scale
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(1.5f, 1.5f);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.save_task_menu, menu);

        menu.findItem(R.id.add_file_menu).setIcon(resizeImage(R.drawable.add_file));
        menu.findItem(R.id.points).setIcon(resizeImage(R.drawable.more));

        if (mTaskNew || !getSettings().getUserName().equals(mTask.getEmailCustomer())) {
            menu.findItem(R.id.del_task_menu).setVisible(false);
        }
        //
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View view = findViewById(R.id.points);

                if (view != null) {
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    });
                }
            }
        });

        menu.findItem(R.id.focus_menu).setIcon(resizeImage(
                mTask.getFocus() ? R.drawable.focus_active : R.drawable.focus_grey)
        );

        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        b.putSerializable(EXTRA_TASK, mTask);
        b.putString(EXTRA_TASK_UUID, mTaskUUID);
        b.putSerializable(EXTRA_TASK_OLD, mTaskOld);
        b.putBoolean(EXTRA_TASK_NEW, mTaskNew);
        if (!mTaskNew){
            b.putSerializable(EXTRA_TASK_MESSAGES, (Serializable) mTaskMessages);
        }
        b.putInt(EXTRA_TASK_MESSAGES_SAVE_FROM, mTaskMessagesSaveFrom);
        b.putSerializable(EXTRA_TASK_FILES, (Serializable) mTaskFiles);
        b.putSerializable(EXTRA_TASK_FILES_DELETED, (Serializable) mTaskFilesDeleted);
    }

    public void DontSaveTask() {
        new DeleteWeakTaskFilesThread(mTaskFiles, getApp().getAppFolder()).start();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        case R.id.save_task:
        case R.id.dont_save:
            saveTask();
            return true;
        case R.id.add_file_menu:
            EditTaskFragment fragment = (EditTaskFragment) getSupportFragmentManager().findFragmentByTag(EditTaskFragment.CLASS_PATH);
            if (fragment != null) {
                fragment.addFileClick();
            }
            return true;

        case R.id.copy_task_link:
            String str ="lt://planning?{"+mTask.getUid()+"}\n";
            ClipboardManager clipboard = (ClipboardManager) EditTaskActivity.this.getSystemService(EditTaskActivity.this.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", str);
            clipboard.setPrimaryClip(clip);
            Utils.showToast(EditTaskActivity.this, getResources().getString(R.string.saved_clipboard));
            return true;

        case R.id.not_save_menu:
            DontSaveTask();
            return true;

        case R.id.del_task_menu:
            final AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setCancelable(true);
            ad.setTitle(getResources().getString(R.string.confirm_delete_title));
            ad.setMessage(getResources().getString(R.string.confirm_delete_text));
            ad.setPositiveButton(R.string.txt_just_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DontSaveTask();
                    new TaskDeleteHelper(getApp(), mTask, true).start();

                }
            });
            ad.setNegativeButton(R.string.txt_just_nono, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            ad.show();
            return true;
        case R.id.focus_menu:
            setFocus();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void setFocus(){
        mTask.setFocus(!mTask.getFocus());
        mTask.setUsnFieldFocus(mTask.getUsnFieldFocus()+1);
        invalidateOptionsMenu();
    }

    @Override
    protected void onUserLeaveHint()
    {
        super.onUserLeaveHint();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                saveTask();
                return true;
        }
        return false;
    }

    @Override
    public int getContainerId() {
        return 0;
    }

    public LTask getTask() {
        return mTask;
    }

    public boolean isTaskNew() {
        return mTaskNew;
    }

    public List<TaskMessage> getTaskMessages() {
        return mTaskMessages;
    }

    public void setTaskMessages(List<TaskMessage> messages) {
        mTaskMessages = messages;
    }

    public List<TaskFile> getTaskFiles() {
        return mTaskFiles;
    }

    public List<TaskFile> getTaskFilesDeleted() {
        return mTaskFilesDeleted;
    }

    public void hideInput() {
        Utils.hideInput(mViewPager);
    }

    public void notifyAdapterChange() {
        try {
            if(LTSettings.getInstance(this.getApp()).getLinkTask()==null) {
                new SaveOnlyMessages().start();
            }
            else {
                openLinkTask();
            }
        } catch (Exception e) {
            Utils.toLog(e);
        } finally {
            notifyDataSetChanged();
        }
    }

    public void newAddedComment() {
        isAddedNewComment = true;
    }

    public void saveTask() {
        boolean needToSave = false;
        hideInput();
        EditTaskFragment fragment = (EditTaskFragment) getSupportFragmentManager().findFragmentByTag(EditTaskFragment.CLASS_PATH);
        if (fragment != null) {
            needToSave = fragment.checkTaskName();
        }
        if(needToSave) {
            if (mTask.getName().equals("")) {
                Toast.makeText(getApp(), R.string.error_empty_task_title, Toast.LENGTH_SHORT).show();
            } else {
                if (fragment != null) {
                    fragment.needToAddComment();
                }

                if(needToReworkTask() && isAddedNewComment) { // на доработку
                    mTask.setStatus(Status.TASK_REFINE.getStatusCode());
                    mTask.setUsnFieldStatus(mTask.getUsnFieldStatus() + 1);
                }
                fullSaving();

            }
            // обновление адаптера в поиске
            if (mListener != null) {
                mListener.onUpdateTaskInAdapter(mTask);
            }
            if (mListenerSaved != null) {
                if (mTask.getUid() != null ) {
                    mListenerSaved.onTaskSaved(mTask);
                    android.util.Log.v("Tedorius", "UUID 1 " + mTask.getUid());
                } else {
                    android.util.Log.v("Tedorius", "UUID 2 " + mTask.getUid());
                }
            }
            //
        }
        else {
            if(!mTaskNew) {
                new DeleteWeakTaskFilesThread(mTaskFiles, getApp().getAppFolder()).start();
                new TaskSaveHelper(false, getApp(), mTaskSuper, false, mTaskMessages, mTaskOldSuper, mTaskMessagesSaveFrom, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
                if(wasUnreared) {
                    new TaskFootstepHelper(getApp()).changeTotalUnreadedAndApply(mTaskSuper, false, false);
                }
                if (mListener != null) {
                    mListener.onUpdateTaskInAdapter(mTaskSuper);
                }
                if (mListenerSaved != null) {
                    mListenerSaved.onTaskSaved(mTaskSuper);
                }
            }
            finish();
        }
    }

    public void saveOneMassage() {
        new TaskSaveHelper(false, getApp(), mTaskSuper, false, mTaskMessages, mTaskOldSuper, mTaskMessagesSaveFrom, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).start();
    }

    private void fullSaving() {
        /*if(mAdapter.getConversationFragment() != null) {
            mAdapter.getConversationFragment().addMessage(); //добавить введенный коммент
        }*/
        new TaskSaveHelper(false, getApp(), mTask, mTaskNew, mTaskMessages, mTaskOld, mTaskMessagesSaveFrom, mTaskFiles, mTaskFilesDeleted, false).start();

        if(wasUnreared) {
            new TaskFootstepHelper(getApp()).changeTotalUnreadedAndApply(mTask, false, false);
        }
        if (mTaskNew && mTask.getStatus() == Status.NOTE.getStatusCode()) {
            new TaskFootstepHelper(getApp()).changeTotalNoteTask(mTask, false, true);
        }

        finish();
    }

    public boolean needToReworkTask() {
        if (!mTask.getEmailPerformer().equals(getSettings().getUserName()) && (mTask.getStatus() == Status.TASK_REJECTED.getStatusCode() || mTask.getStatus() == Status.TASK_READY.getStatusCode() )){
            return true;
        }
        return false;
    }

    private void setUnreadTask() {
        // устанавливаем флаг прочитано и записываем в базу

        if (!mTaskSuper.getReaded()) {
            wasUnreared = true;
            mTaskSuper.setReaded(true);
            mTaskSuper.setUsnFieldReaded(mTaskSuper.getUsnFieldReaded() + 1);
            mTaskSuper.setUsnEntity(0);
        }
        if (mTaskSuper.getEmailPerformer().equals(LTSettings.getInstance().getUserName()) && !mTaskSuper.getPerformerReaded()) {
            mTaskSuper.setPerformerReaded(true);
            mTaskSuper.setUsnFieldPerformerReaded(mTaskSuper.getUsnFieldPerformerReaded() + 1);
            mTaskSuper.setUsnEntity(0);
        }
        mTask = mTaskSuper;
    }

    private static final class DeleteWeakTaskFilesThread extends Thread {

        private final List<TaskFile> mTaskFiles;
        private final File mAppFolder;

        public DeleteWeakTaskFilesThread(List<TaskFile> taskFiles, File appFolder) {
            mTaskFiles = taskFiles;
            mAppFolder = appFolder;
        }

        @Override
        public void run() {
            super.run();

            for (TaskFile file : mTaskFiles) {
                if (file.isWeakLink()) {
                    new File(mAppFolder, file.getFileName()).delete();
                }
            }
        }
    }

    private final class SaveOnlyMessages extends Thread {
        @Override
        public void run() {
            super.run();
            if (mTaskMessages.size() > 0) {
                TaskMessageCache.getInstance(getApp()).updateCache(mTaskMessages);
            }
            DbHelper mDbHelper = DbHelper.getInstance(getApp());
            final Dao<TaskMessage, UUID> dao = mDbHelper.getTaskMessageDao();
            try {
                dao.callBatchTasks(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            dao.create(mTaskMessages.get(mTaskMessages.size()-1));

                        } catch (SQLException e) {
                            Utils.toLog(e);
                        }
                    return null;
                    }
                });

            } catch (Exception e) {
                Utils.toLog(e);
            }
        }
    }

    private void notifyDataSetChanged() {
        getApp().getContentResolver().notifyChange(LTaskContract.CONTENT_URI, null);
    }

    private void openLinkTask()
    {
        LTask LinkTask = LTSettings.getInstance(this.getApp()).getLinkTask();
        if(LinkTask!=null) {
            LTSettings.getInstance(this.getApp()).setLinkTask(null);
            Intent intent = new Intent(EditTaskActivity.newInstance(this.getApp(), LinkTask, false, false));
            startActivity(intent);
        }
    }

    private void setActionBar()
    {
        // Set a toolbar which will replace the action bar.
        toolbar = (Toolbar) findViewById(R.id.toolbar_properties);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

        final Drawable upArrow = getResources().getDrawable(R.drawable.baseline_arrow_back_white_24);
        upArrow.setColorFilter(getResources().getColor(R.color.toolbar_prop_color_text), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
        //actionBar.setTitle(R.string.task);
        //actionBar.setLogo(TaskStatus.getTaskStatus(mTaskOld.getStatus()).getResIdWhite());
    }

}