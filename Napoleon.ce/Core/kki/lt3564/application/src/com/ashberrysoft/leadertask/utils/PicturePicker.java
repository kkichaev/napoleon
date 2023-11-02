package com.ashberrysoft.leadertask.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker.FileType;

public final class PicturePicker {

    public interface OnPickManagerListener {

        void setBlock(boolean blocking);

        void startActivityForResult(Intent intent, int requestCode);

        boolean displayMediaFile(File media);
    }

    private static final int ACTION_CAPTURE = 1;
    private static final int ACTION_PICK = 2;

    // BASE
    private final WeakReference<Activity> mActivity;
    private final LTApplication mApp;
    private final OnPickManagerListener mListener;

    // VALUE's
    private boolean mSelected;

    private File mFile;
    private MediaTask mTask;

    public PicturePicker(Activity activity, OnPickManagerListener listener) {
        mActivity = new WeakReference<Activity>(activity);
        mApp = (LTApplication) activity.getApplicationContext();
        mListener = listener;
    }

    public void selectSource() {
        if (mApp.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            imageCapture();
            // final Activity activity = mActivity.get();
            // if (activity != null) {
            // mDialogController.resetWasClicked();
            //
            // final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity,
            // R.style.BlackDialog));
            // builder.setTitle(R.string.d_title_pick_an_image_source);
            // builder.setItems(R.array.media_sources, mDialogController);
            // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // builder.setOnDismissListener(mDialogController);
            //
            // } else {
            // builder.setOnCancelListener(mDialogController);
            // }
            // builder.show();
            // }

        } else {
            finishActivity();
            // mDialogController.onClick(null, 1);
        }
    }

    // private final DialogController mDialogController = new DialogController();
    //
    // private final class DialogController implements DialogInterface.OnClickListener,
    // DialogInterface.OnDismissListener,
    // DialogInterface.OnCancelListener {
    //
    // private boolean mWasClicked;
    //
    // public void resetWasClicked() {
    // mWasClicked = false;
    // }
    //
    // @Override
    // public void onCancel(DialogInterface dialog) {
    // onDismiss(dialog);
    // }
    //
    // @Override
    // public void onDismiss(DialogInterface dialog) {
    // if (!mWasClicked) {
    // finishActivity();
    // }
    // }
    //
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // mWasClicked = true;
    //
    // if (mTask != null) {
    // Utils.showToast(mApp, R.string.t_error_same_process_not_stop);
    // return;
    // }
    //
    // mSelected = true;
    // switch (which) {
    // case 0:
    // imageCapture();
    // break;
    //
    // case 1:
    // final Intent pick = new Intent(Intent.ACTION_PICK);
    // pick.setType(SharedStrings.MIME_TYPE_IMAGE);
    //
    // startActivityForResult(pick, ACTION_PICK);
    // break;
    //
    // default:
    // break;
    // }
    // }
    // }

    private void imageCapture() {
        final Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            final File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            pictures.mkdirs();

            mFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", pictures);
            capture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));

        } catch (IOException e) {
            Utils.toLog(e);
        }
        
        mSelected = true;
        startActivityForResult(capture, ACTION_CAPTURE);
    }

    private void finishActivity() {
        mListener.setBlock(false);

        final Activity activity = mActivity.get();
        if (activity != null) {
            activity.finish();
        }
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        if (intent.resolveActivity(mApp.getPackageManager()) == null) {
            Utils.showToast(mApp, R.string.t_error_no_app_to_handle);

        } else {
            mListener.startActivityForResult(intent, requestCode);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultIsOk(requestCode, resultCode, intent)) {
            if (intent == null) {
                intent = new Intent();
                intent.setData(Uri.parse(SharedStrings.CONTENT_FILE + mFile.getAbsolutePath()));
                mFile = null;
            }

            mSelected = false;
            switch (requestCode) {
            case ACTION_CAPTURE:
                mTask = new MediaTask();
                if (intent != null && (intent.getData() != null || intent.getExtras() != null)) {
                    mTask.execute(intent);

                } else {
                    mTask.execute((Intent[]) null);
                }
                break;

            case ACTION_PICK:
                mTask = new MediaTask();
                mTask.execute(intent);
                break;

            default:
                break;
            }
        }
    }

    protected boolean resultIsOk(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            if (mSelected && mFile != null && mFile.length() > 0) {
                return true;
            }
        }

        if (mSelected && resultCode == Activity.RESULT_OK) {
            return true;

        } else {
            finishActivity();
            return false;
        }
    }

    private boolean onIntent(Intent intent) {
        try {
            mFile = processIntent(intent);
            return mFile != null;

        } catch (Exception e) {
            Utils.toLog(e);
            return false;
        }
    }

    protected File processIntent(Intent intent) throws Exception {
        if (intent.getExtras() != null && intent.getExtras().containsKey("data")) {
            final Bitmap bmp = intent.getParcelableExtra("data");

            final File file = new File(mApp.getAppFolder(), FileWorker.getNewCurrentPictureFileName());
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
                bmp.compress(CompressFormat.JPEG, 90, os);

                return file;

            } finally {
                if (os != null) {
                    os.close();
                }
            }
        }

        final Uri uri = intent.getData();
        final String uriString = String.valueOf(uri);

        if (uriString.startsWith(SharedStrings.CONTENT_MEDIA)) {
            final String filePath = Utils.getFilePathFromUri(mApp, uri);
            return FileWorker.copyFile(FileType.PICTURE, filePath, mApp.getAppFolder());

        } else if (uriString.startsWith(SharedStrings.CONTENT_GOOGLE_PHOTOS)) {
            final FileType type = FileType.getFileType(mApp.getContentResolver().getType(uri));
            final File imageFile = new File(mApp.getAppFolder(), FileWorker.getNewFileName(type));

            if (FileWorker.fromUriToFile(mApp, uri, imageFile)) {
                return imageFile;
            }

        } else if (uriString.startsWith(SharedStrings.CONTENT_FILE)) {
            final File src = new File(uriString.replace(SharedStrings.CONTENT_FILE, SharedStrings.EMPTY));
            final File dst = new File(mApp.getAppFolder(), FileWorker.getNewFileName(FileType.PICTURE));

            return FileWorker.copyFile(src, dst);
        }

        return null;
    }

    private final class MediaTask extends AsyncTask<Intent, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListener.setBlock(true);
        }

        @Override
        protected Boolean doInBackground(Intent... params) {
            if (params != null && params.length == 1) {
                final boolean goNext = onIntent(params[0]);

                if (!goNext) {
                    return null;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);

            if (b == null || !b || !mListener.displayMediaFile(mFile)) {
                Utils.showToast(mApp, R.string.t_error_file_saving);
                finishActivity();
            }

            mFile = null;
            mTask = null;
        }
    }
}