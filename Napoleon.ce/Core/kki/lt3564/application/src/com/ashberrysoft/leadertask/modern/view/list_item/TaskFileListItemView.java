package com.ashberrysoft.leadertask.modern.view.list_item;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;

import java.io.File;
import java.net.URLConnection;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class TaskFileListItemView extends RelativeLayout//
        implements OnClickListener {

    public interface OnTaskFileListener {
        public void onTaskFileClick(TaskFile file, boolean remove);
    }

    // VIEW's
    private final TextView mName;
    private final TextView mSize;
    private final ImageView mRemove;
    private final ImageView mPreview;

    // VALUE's
    private final LTSettings mSettings;
    private final EmployeeCache mEmployeeCache;
    private final StringBuilder mSb;
    private final String mTaskFileExist;
    private Handler mHandler;

    private TaskFile mFile;

    // LISTENER
    private OnTaskFileListener mListener;

    public TaskFileListItemView(Context context, OnTaskFileListener listener) {
        this(context);
        mListener = listener;

    }

    protected TaskFileListItemView(Context context) {
        super(context);
        inflate(getContext(), R.layout.list_item_file_preview, this);

        mSettings = LTSettings.getInstance(getContext());
        mEmployeeCache = EmployeeCache.getInstance(getContext());
        mSb = new StringBuilder();
        mTaskFileExist = getResources().getString(R.string.task_file_exist);

        mName = (TextView) findViewById(R.id.file_name);
        mSize = (TextView) findViewById(R.id.file_size);
        mRemove = (ImageView) findViewById(R.id.file_remove);
        mPreview = (ImageView) findViewById(R.id.file_preview);

        final int fileResId = R.drawable.add_file;

        ((ImageView) findViewById(R.id.file_icon)).setImageResource(fileResId);

        findViewById(R.id.file_icon).setOnClickListener(this);
        findViewById(R.id.file_info).setOnClickListener(this);
        mRemove.setOnClickListener(this);

    }

    public void setData(TaskFile file) {
        mFile = file;

        mName.setText(mFile.getFileName());
        final String emailCreator = mFile.getEmailCreator();

        Utils.clearStringBuilder(mSb);
        if (!TextUtils.isEmpty(emailCreator) && mSettings.getUserName().equals(emailCreator)) {
            Utils.changeVisibility(mRemove, View.VISIBLE);

        } else {
            Utils.changeVisibility(mRemove, View.INVISIBLE);

            mSb.append(mEmployeeCache.find(emailCreator));
            mSb.append(SharedStrings.COMMA_C);
            mSb.append(SharedStrings.SPACE_C);
        }

        mSb.append(FileWorker.getFileSize(getContext(), mFile.getFileSize()));
        if (!mFile.isFileExist()) {
            mSb.append(mTaskFileExist);
        }

        if (isImageFile((LTApplication) getContext().getApplicationContext(), mFile.getFileName())) {
            // если картинка
            if (!mFile.isFileExist()) {
                mPreview.setImageResource(R.drawable.ic_photo);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPreview.setImageDrawable(Utils.getBitmapFromFolder((LTApplication) getContext().getApplicationContext(), mFile.getFileName()));
                    }
                }).run();
            }
        } else {
            // если не картинка
            mPreview.setImageResource(R.drawable.ic_unknown_file);
        }


        mSize.setText(mSb);
    }

    public static boolean isImageFile(LTApplication mApp, String fileName) {
        final File imgFile = new File(mApp.getAppFolder(), fileName);

        String mimeType = URLConnection.guessContentTypeFromName(imgFile.getAbsolutePath());
        return mimeType != null && mimeType.startsWith("image");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.file_icon:
        case R.id.file_info:
            mListener.onTaskFileClick(mFile, false);
            break;

        case R.id.file_remove:
            mListener.onTaskFileClick(mFile, true);
            break;

        default:
            break;
        }
    }
}