package com.ashberrysoft.leadertask.fragments;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CursorFilesAdapter;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.DownloadFile;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException;
import com.ashberrysoft.leadertask.dialogs.SetCategoryDialog;
import com.ashberrysoft.leadertask.dialogs.SetMarkerDialog;
import com.ashberrysoft.leadertask.dialogs.SetPerformerDialog;
import com.ashberrysoft.leadertask.dialogs.SetProjectDialog;
import com.ashberrysoft.leadertask.dialogs.SetStatusDialog;
import com.ashberrysoft.leadertask.dialogs.SetTermDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker.FileType;
import com.ashberrysoft.leadertask.views.CursorFileListItemView.OnCursorFileListItemListener;
import com.ashberrysoft.leadertask.views.EditTaskHeaderView;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class TaskEditFragment extends LTBaseFragment//
        implements OnCursorFileListItemListener, LoaderCallbacks<Cursor>, DialogInterface.OnClickListener {

    private enum RequestCode {
        NONE, CAMERA, GALLERY, AUDIO;
    }

    private static final String CLASS_PATH = TaskEditFragment.class.getName();
    private static final String EXTRA_TASK = CLASS_PATH + "EXTRA_TASK";
    private static final String EXTRA_CATEGORIES = CLASS_PATH + "EXTRA_CATEGORIES";
    private static final String EXTRA_REQUEST_CODE = CLASS_PATH + "EXTRA_REQUEST_CODE";
    private static final String EXTRA_TASK_NEW = CLASS_PATH + "EXTRA_TASK_NEW";

    private static final int WHAT_START_BLOCK = 0;
    private static final int WHAT_STOP_BLOCK = 1;
    private static final int WHAT_SHOW_TOAST = 2;
    private static final int WHAT_SHOW_KEYBOARD = 3;

    // VIEW's
    private ListView mListView;
    private EditTaskHeaderView mHeaderView;

    // VALUE's
    private static TaskEditFragment sInstance;
    private Handler mHandler;
    private DbHelper mDbHelper;
    private Task mTask;
    private Set<Category> mCategories;
    private RequestCode mRequestCode = RequestCode.NONE;
    private File mTempFile;
    private String mTempString;
    private boolean mKeyboardShowed;

    // ADAPTER
    private CursorFilesAdapter mAdapter;

    public static TaskEditFragment newInstance(Task task, Category category, boolean taskNew) {
        final Bundle b = new Bundle(3);
        b.putSerializable(EXTRA_TASK, task);
        b.putBoolean(EXTRA_TASK_NEW, taskNew);
        if (category != null) {
            b.putSerializable(EXTRA_CATEGORIES, category);
        }

        sInstance = new TaskEditFragment();
        sInstance.setArguments(b);

        return sInstance;
    }

    public static TaskEditFragment getInstance() {
        return sInstance;
    }

    public static void setInstanceNull() {
        sInstance = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mHandler = getHandler();
        mDbHelper = DbHelper.getInstance(getActivity());

        if (b != null) {
            mTask = (Task) b.getSerializable(EXTRA_TASK);
            
            if (b.containsKey(EXTRA_CATEGORIES)) {
                mCategories = (Set<Category>) b.getSerializable(EXTRA_CATEGORIES);
            }
            mRequestCode = RequestCode.values()[b.getInt(EXTRA_REQUEST_CODE, 0)];
            mKeyboardShowed = true;
        }

        else if (getArguments() != null) {
            mTask = (Task) getArguments().getSerializable(EXTRA_TASK);

            if (getArguments().containsKey(EXTRA_CATEGORIES)) {
                final Category category = (Category) getArguments().getSerializable(EXTRA_CATEGORIES);

                mCategories = new HashSet<>(1);
                mCategories.add(category);
            }

            final Task task = mDbHelper.getTaskDao_queryForId(mTask.getId());
            if (task != null) {
                mTask = task;
            }
            mKeyboardShowed = !getArguments().getBoolean(EXTRA_TASK_NEW);
        }

        getLoaderManager().initLoader(R.id.lm_task_edit, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        setTargetFragmentsToDialogs(this);
        return inflater.inflate(R.layout.fragment_edit_task_new, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);
        v.setBackgroundColor(getResources().getColor(
                mSettings.isThemeDark() ? R.color.sliding_menu_background : R.color.gray_task_complete));

        mListView = (ListView) v.findViewById(R.id.edit_list_view);
        setListViewParams();

        if (mCategories == null) {
            mCategories = new HashSet<Category>(0);
        }

        mHeaderView = new EditTaskHeaderView(getActivity(), mTask, mDbHelper, mListView, b, mCategories);

        mAdapter = new CursorFilesAdapter(getActivity(), null, this);
        mListView.setAdapter(mAdapter);

        mHeaderView.setViewsOnClickListener(this);

        if (!mKeyboardShowed) {
            mHandler.sendEmptyMessageDelayed(WHAT_SHOW_KEYBOARD, 400);
        }
    }

    private void setTargetFragmentsToDialogs(Fragment fragment) {
        SetPerformerDialog.setTargetFragment(fragment, getChildFragmentManager());
        SetTermDialog.setTargetFragment(fragment, getChildFragmentManager());
        SetStatusDialog.setTargetFragment(fragment, getChildFragmentManager());
        SetMarkerDialog.setTargetFragment(fragment, getChildFragmentManager());
        SetProjectDialog.setTargetFragment(fragment, getChildFragmentManager());
        SetCategoryDialog.setTargetFragment(fragment, getChildFragmentManager());
    }

    @Override
    public void onPause() {
        setTargetFragmentsToDialogs(null);
        Utils.hideInput(mApp, mListView);

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (mCategories != null) {
            b.putSerializable(EXTRA_CATEGORIES, (Serializable) mCategories);
        }
        b.putSerializable(EXTRA_TASK, mTask);
        b.putInt(EXTRA_REQUEST_CODE, mRequestCode.ordinal());
        mHeaderView.onSavedInstanceState(b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_from_camera:
            imageCapture();
            return true;

        case R.id.add_from_gallery:
            galleryImage();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.performer:
            SetPerformerDialog.newInstance(this, mTask.getPerformer()).showDialog(getChildFragmentManager());
            break;

        case R.id.term:
            SetTermDialog.newInstance(this, mTask).showDialog(getChildFragmentManager());
            break;

        case R.id.status:
            SetStatusDialog.newInstance(this, mTask).showDialog(getChildFragmentManager());
            break;

        case R.id.marker:
            SetMarkerDialog.newInstance(this, mTask.getMarkerUid()).showDialog(getChildFragmentManager());
            break;

        case R.id.project:
            SetProjectDialog.newInstance(this, mTask.getProjectUid()).showDialog(getChildFragmentManager());
            break;

        case R.id.categories:
            setBlock(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mCategories == null) {
                        try {
                            mCategories = mDbHelper.getCategoriesSetByTask(mTask);

                        } catch (SQLException e) {
                            Utils.toLog(e);
                        }
                    }

                    SetCategoryDialog.newInstance(TaskEditFragment.this, mCategories).showDialog(
                            getChildFragmentManager());
                    setBlockInUI(false);
                }
            }).start();
            break;

        default:
            break;
        }
    }

    private boolean externalStorageExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private void imageCapture() {
        if (!externalStorageExist()) {
            Utils.showToast(getActivity(), R.string.t_error_external_storage);
            return;
        }

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            Utils.showToast(getActivity(), R.string.t_error_camera);
            return;
        }

        mTempFile = new File(mApp.getAppFolder(), FileWorker.getNewCurrentPictureFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));

        startActivityForResult(intent, RequestCode.CAMERA);
    }

    private void galleryImage() {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_chooser_image)),
                RequestCode.GALLERY);
    }

    @Override
    public void onFileClick(final String fileUID, final String fileName, final boolean fileExist) {
        if (fileExist) {
            final File file = new File(mApp.getAppFolder(), fileName);
            if (file.exists()) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), FileWorker.getFileMimeType(file));

                if (getActivity() != null) {
                    startActivity(Intent.createChooser(intent, getString(R.string.title_chooser_open)));
                }
            } else {
                Utils.showToast(getActivity(), R.string.t_error_file_not_exist);

                final ContentValues cv = new ContentValues();
                cv.put(TaskFileContract.FILE_EXIST, 0);

                getActivity().getContentResolver().update(TaskFileContract.CONTENT_URI, cv,
                        TaskFileContract.selectionFieldFileUid(fileUID), null);
            }
        } else {
            if (!isNetworkAvailable()) {
                Utils.showToast(getActivity(), R.string.error_internet_access);
                return;
            }

            Utils.showToast(getActivity(), R.string.t_start_download_file);
            setBlock(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new DownloadFile(mApp, fileUID, fileName, mApp.getSettings().getUserProfile(),
                                mApp.getAppFolder(), 0).downloadFile();
                        setBlockInUI(false);
                        onFileClick(fileUID, fileName, true);
                    } catch (LeaderTaskException e) {
                        setBlockInUI(false);
                        showError(e.toString());
                    }
                }
            }).start();
        }
    }

    @Override
    public void onFileRemove(String fileUID) {
        mTempString = fileUID;
        Utils.getSimpleDialog(getActivity(), this, R.string.d_remove_file_title, R.string.d_remove_file_message);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE && mTempString != null) {
            final ContentValues cv = new ContentValues();
            cv.put(TaskFileContract.DELETE_OBJECT, 1);

            mApp.getContentResolver().update(TaskFileContract.CONTENT_URI, cv,
                    TaskFileContract.selectionFieldFileUid(mTempString), null);

            mTempString = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            if (mTempFile != null) {
                mTempFile.delete();
                mTempFile = null;
            }
            return;
        }

        switch (mRequestCode) {
        case CAMERA:
            mRequestCode = RequestCode.NONE;

            if (mTempFile == null || !mTempFile.exists()) {
                return;
            }

            addFileToTask(mApp, mTask.getId(), mTempFile, mSettings.getUserName());
            mTempFile = null;
            break;

        case GALLERY:
            mRequestCode = RequestCode.NONE;

            if (data == null) {
                return;
            }

            setBlockInUI(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String path = getPathToImage(data.getData());
                    if (path == null) {
                        setBlockInUI(false);
                        showMessageInUI(R.string.t_error_file_saving);
                        return;
                    }

                    try {
                        mTempFile = FileWorker.copyFile(FileType.PICTURE, path, mApp.getAppFolder());
                        addFileToTask(mApp, mTask.getId(), mTempFile, mSettings.getUserName());
                        mTempFile = null;
                    } catch (IOException e) {
                        showMessageInUI(R.string.t_error_file_saving);
                    } finally {
                        setBlockInUI(false);
                    }
                }
            }).start();
        default:
            break;
        }
    }

    public String getPathToImage(Uri uri) {
        if (uri == null) {
            return null;
        }

        final String result;

        final String[] projection = { MediaStore.Images.Media.DATA };
        final Cursor cursor = mApp.getContentResolver().query(uri, projection, null, null, null);
        final int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        result = cursor.getString(column);
        cursor.close();

        return result;
    }

    public static void addFileToTask(Context context, UUID taskId, File file, String creatorName) {
        final ContentResolver cr = context.getContentResolver();
        context = null;

        final Cursor cursor = cr.query(TaskFileContract.CONTENT_URI, null,
                TaskFileContract.selectionFieldTaskUid(String.valueOf(taskId)), null, null);

        final ContentValues cv = new ContentValues(3);
        final int columnFileUID = cursor.getColumnIndex(TaskFileContract.FIELD_FILEUID);
        final int columnUsnOrder = cursor.getColumnIndex(TaskFileContract.FIELD_USN_FIELD_ORDER);

        int count = 1;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            cv.clear();
            cv.put(TaskFileContract.ORDERS, count++);
            cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
            cv.put(TaskFileContract.FIELD_USN_FIELD_ORDER, cursor.getInt(columnUsnOrder) + 1);

            cr.update(TaskFileContract.CONTENT_URI, cv,
                    TaskFileContract.selectionFieldFileUid(cursor.getString(columnFileUID)), null);
        }
        cursor.close();

        final TaskFile f = new TaskFile(null, taskId, null, file.getName(), file.length(), creatorName, count);
        cr.insert(TaskFileContract.CONTENT_URI, f.getContentValues(null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onFragmentResult(Object object, int requestCode) {
        super.onFragmentResult(object, requestCode);

        switch (requestCode) {
        case SetPerformerDialog.REQUEST_CODE:
            mTask.setPerformer((String) object);
            mHeaderView.setPerformer(mTask);
            break;

        case SetTermDialog.REQUEST_CODE:
            mTask = (Task) object;
            mHeaderView.setTerm(mTask);
            break;

        case SetStatusDialog.REQUEST_CODE:
            mTask.setStatusType((TaskStatus) object);
            mHeaderView.setStatus(mTask);
            break;

        case SetMarkerDialog.REQUEST_CODE:
            final Marker marker = (Marker) object;

            mTask.setMarkerUid(marker.getId());
            mHeaderView.setMarker(marker);
            break;

        case SetProjectDialog.REQUEST_CODE:
            final Project project = (Project) object;

            mTask.setProjectUid(project != null ? project.getId() : null);
            mHeaderView.setProject(project);
            break;

        case SetCategoryDialog.REQUEST_CODE:
            mHeaderView.setCategories((Set<Category>) object);
            break;

        default:
            break;
        }
    }

    @SuppressWarnings("deprecation")
    private void setListViewParams() {
        mListView.setBackgroundColor(mSettings.isThemeDark() ? Color.BLACK : Color.WHITE);

        if (mApp.isTablet()) {
            return;
        }

        final int width = getResources().getDimensionPixelSize(R.dimen.fetn_width);
        final Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        final int displayWidth = display.getWidth();

        if (width > displayWidth) {
            return;
        }

        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
        mListView.setLayoutParams(lp);
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    public Task getTask() {
        mTask.setName(mHeaderView.getTitle());
        mTask.setComment(mHeaderView.getComment());
        mTask.setCategoriesWithSet(mCategories);

        return mTask;
    }

    public Set<Category> getCategories() {
        return mCategories;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle b) {
        switch (id) {
        case R.id.lm_task_edit:
            return new CursorLoader(getActivity(), TaskFileContract.CONTENT_URI, null,
                    TaskFileContract.selectionFieldTaskUidAndDeleteObject(mTask.getId().toString(), false), null, null);

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        switch (loader.getId()) {
        case R.id.lm_task_edit:
            mAdapter.swapCursor(c);
        default:
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {}

    public void startActivityForResult(Intent intent, RequestCode code) {
        mRequestCode = code;
        super.startActivityForResult(intent, code.ordinal());
    }

    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                case WHAT_START_BLOCK:
                    setBlock(true);
                    break;

                case WHAT_STOP_BLOCK:
                    setBlock(false);
                    break;

                case WHAT_SHOW_TOAST:
                    Utils.showToast(mApp, (String) msg.obj);
                    break;

                case WHAT_SHOW_KEYBOARD:
                    final EditText title = mHeaderView.getEditTextTitle();
                    title.requestFocus();
                    title.setSelection(title.length());

                    final InputMethodManager imm = (InputMethodManager) mApp
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);

                    // title.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    // SystemClock.uptimeMillis(),
                    // MotionEvent.ACTION_DOWN, 0, 0, 0));
                    break;

                default:
                    break;
                }
            }
        };
    }

    private void showMessageInUI(final int id) {
        final Message message = new Message();
        message.what = WHAT_SHOW_TOAST;
        message.obj = mApp.getString(id);

        mHandler.sendMessage(message);
    }

    private void setBlockInUI(boolean setBlock) {
        mHandler.sendEmptyMessage(setBlock ? WHAT_START_BLOCK : WHAT_STOP_BLOCK);
    }
}