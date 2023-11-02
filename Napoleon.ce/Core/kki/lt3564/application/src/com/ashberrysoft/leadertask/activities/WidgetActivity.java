package com.ashberrysoft.leadertask.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.PicturePicker;
import com.ashberrysoft.leadertask.utils.PicturePicker.OnPickManagerListener;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.widget.BaseWidget;
import com.ashberrysoft.leadertask.widget.BaseWidget.WidgetType;
import com.v2soft.AndLib.ui.activities.IBaseActivity;

public class WidgetActivity extends AppCompatActivity implements IBaseActivity<LTApplication>, OnPickManagerListener {

    // VALUE's
    private ProgressDialog mProgress;
    private WidgetType mWidgetType;
    private PicturePicker mPicturePicker;

    public static void startActivity(Context context, WidgetType type) {
        final Intent intent = new Intent(context, WidgetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BaseWidget.EXTRA_WIDGET_TYPE, type.ordinal());

        context.startActivity(intent);
    }

    public String getPath(Uri uri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        LTApplication mApp = (LTApplication) getActivity().getApplication().getApplicationContext();
        if (!LTSettings.getInstance().getUserProfile().isValid()) {
            Utils.showToast(this, R.string.t_error_no_auth);
            finish();
            return;
        }
        if (getIntent() != null && getIntent().getType() != null) {
            if (LTSettings.getInstance().getUserProfile().isValid()) {
                if (getIntent().getType().indexOf("image") != -1) {
                    final Uri uri = (Uri) getIntent().getExtras().get("android.intent.extra.STREAM");
                    String path;
                    if (uri.toString().indexOf("media/external/images/media") != -1) { //
                        path = getRealPathFromUri(mApp, uri);
                    } else {
                        path = getPath(uri, this);
                        //path = uri.getPath();
                    }

                    try {
                        final File src = new File(path.replace(SharedStrings.CONTENT_FILE, SharedStrings.EMPTY));
                        final File dst = new File(mApp.getAppFolder(), src.getName());

                        if (!src.equals(dst)) {
                            Utils.FileWorker.copyFile(src, dst);
                        }

                        if (dst != null && dst instanceof File) {
                            new FileSaveTask(dst, true).execute();


                        } else {
                            Utils.showToast(getActivity(), R.string.t_error_file_saving);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    finish();
                } else {
                    Utils.showToast(getActivity(), R.string.t_error_file_saving);
                    finish();
                }
            } else {
                Utils.showToast(mApp, R.string.t_error_no_auth);
                finish();
            }
        } else if (getIntent() != null){

            mWidgetType = WidgetType.values()[getIntent().getIntExtra(BaseWidget.EXTRA_WIDGET_TYPE, WidgetType.PHOTO.ordinal())];
            switch (mWidgetType) {

                case PHOTO:
                    mPicturePicker = new PicturePicker(getActivity(), this);
                    mPicturePicker.selectSource();
                    break;

                case TASK:
                    final String text = getIntent().getStringExtra("EXTRA_TEXT");
                    final LTask task = TaskHelper.createNewTaskWithParams(LTSettings.getInstance().getUserName(), LTSettings.getInstance().getUserName(), 0, null, null, null, null);
                    task.setName(text);

                    startActivity(EditTaskActivity.newInstance(this, task, true, false));

                    finish();
                    break;

                default:
                    Utils.showToast(getActivity(), mWidgetType.toString());
                    finish();
                    break;
            }
        }
        else {
            finish();
        }
    }

    private Activity getActivity() {
        return this;
    }

    @Override
    public void showError(String message) {}

    @Override
    public void showError(int messageResource) {}

    @Override
    public void setLoadingProcess(boolean value, Object tag) {}

    @Override
    public void setBlockingProcess(boolean value, Object tag) {

    }

    @Override
    public LTApplication getApplicationObject() {
        return (LTApplication) getApplicationContext();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (mPicturePicker != null) {
            mPicturePicker.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private final class FileSaveTask extends AsyncTask<Void, Void, Void> {

        private final File mFile;
        private final boolean mIsShare;

        public FileSaveTask(File file, boolean isShare) {
            super();
            mIsShare = isShare;
            mFile = file;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            final LTSettings settings = LTSettings.getInstance();

            final UUID taskUid = UUID.randomUUID();

            final LTask task = TaskHelper.createNewTaskWithParams(settings.getUserName(), settings.getUserName(), 0, null, null, null, null);

            task.setUid(String.valueOf(taskUid).toUpperCase());
            if (!mIsShare) {
                task.setName(TaskHelper.getTaskFormat().format(TimeHelper.currentTimeMillisWithoutTimeZone()));
            } else {
                task.setName(mFile.getName());
            }
            final TaskFile taskFile = new TaskFile(null, taskUid, null, mFile.getName(), mFile.length(), task.getEmailCustomer(), 1);

            final List<TaskFile> taskFiles = new ArrayList<>(1);
            taskFiles.add(taskFile);

            new TaskSaveHelper(false, getActivity(), task, true, null, null,//
                    0, taskFiles, new ArrayList<TaskFile>(0), false).run();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Utils.showToast(getActivity(), R.string.t_task_input);

            getActivity().finish();
        }
    }


    @Override
    public void setBlock(boolean blocking) {

    }

    @Override
    public boolean displayMediaFile(File file) {
        try {
            new FileSaveTask(file, false).execute();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}