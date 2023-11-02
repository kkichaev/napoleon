package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.cache.CachedEmployee;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CursorFileListItemView extends RelativeLayout implements OnClickListener {

    public interface OnCursorFileListItemListener {
        public void onFileClick(String fileUID, String fileName, boolean fileExist);

        public void onFileRemove(String fileUID);
    }

    // VIEW's
    private TextView mName;
    private TextView mSize;
    private ImageView mRemove;

    // VALUE's
    private String mFileUID;
    private String mFileName;
    private boolean mFileExist;
    private static int[] sCursorFields;
    private LTApplication mApp;
    private CachedEmployee mCachedEmployee;

    // LISTENER
    private OnCursorFileListItemListener mListener;

    public CursorFileListItemView(Context context) {
        super(context);
        initialization();
    }

    public CursorFileListItemView(Context context, OnCursorFileListItemListener listener) {
        this(context);
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_file, this);

        mApp = (LTApplication) getContext().getApplicationContext();

        mName = (TextView) findViewById(R.id.file_name);
        mSize = (TextView) findViewById(R.id.file_size);
        mRemove = (ImageView) findViewById(R.id.file_remove);
        final ImageView fileIcon = (ImageView) findViewById(R.id.file_icon);

        if (mApp.getSettings().isThemeDark()) {
            mRemove.setImageResource(R.drawable.file_remove_white);
            fileIcon.setImageResource(R.drawable.attached_files64_white);
        } else {
            mRemove.setImageResource(R.drawable.file_remove_gray);
            fileIcon.setImageResource(R.drawable.attached_files64);
        }

        findViewById(R.id.file_icon).setOnClickListener(this);
        findViewById(R.id.file_info).setOnClickListener(this);
        mRemove.setOnClickListener(this);

        mCachedEmployee = CachedEmployee.getInstance(getContext());
    }

    public void setData(Cursor c) {
        if (sCursorFields == null) {
            sCursorFields = new int[5];
            sCursorFields[0] = c.getColumnIndex(TaskFileContract.FIELD_FILEUID);
            sCursorFields[1] = c.getColumnIndex(TaskFileContract.FIELD_FILENAME);
            sCursorFields[2] = c.getColumnIndex(TaskFileContract.FILE_EXIST);
            sCursorFields[3] = c.getColumnIndex(TaskFileContract.FIELD_EMAILCREATOR);
            sCursorFields[4] = c.getColumnIndex(TaskFileContract.FIELD_FILESIZE);
        }

        mFileUID = c.getString(sCursorFields[0]);
        mFileName = c.getString(sCursorFields[1]);
        mFileExist = c.getInt(sCursorFields[2]) == 1;

        mName.setText(mFileName);
        final String emailCreator = c.getString(sCursorFields[3]);

        final StringBuilder size = new StringBuilder();
        if (!TextUtils.isEmpty(emailCreator) && mApp.getSettings().getUserName().equals(emailCreator)) {
            mRemove.setVisibility(View.VISIBLE);
        } else {
            mRemove.setVisibility(View.GONE);
            size.append(mCachedEmployee.getName(emailCreator));
            size.append(SharedStrings.COMMA_C);
            size.append(SharedStrings.SPACE_C);
        }

        size.append(FileWorker.getFileSize(getContext(), c.getLong(sCursorFields[4])));
        if (!mFileExist) {
            size.append(getResources().getString(R.string.task_file_exist));
        }

        mSize.setText(size.toString());
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        switch (v.getId()) {
        case R.id.file_icon:
        case R.id.file_info:
            mListener.onFileClick(mFileUID, mFileName, mFileExist);
            break;

        case R.id.file_remove:
            mListener.onFileRemove(mFileUID);
        default:
            break;
        }
    }

    public void setCustomListener(OnCursorFileListItemListener listener) {
        mListener = listener;
    }
}