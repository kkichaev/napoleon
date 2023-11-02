package com.ashberrysoft.leadertask.modern.view.list_item;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ContactFileListItemView extends RelativeLayout//
        implements OnClickListener {

    public interface OnContactFileListener {
        public void onContactFileClick(ContactFile file, boolean remove);
    }

    // VIEW's
    private final TextView mName;
    private final TextView mSize;
    private final ImageView mRemove;

    // VALUE's
    private final LTSettings mSettings;
    private final EmployeeCache mEmployeeCache;
    private final StringBuilder mSb;
    private final String mContactFileExist;

    private ContactFile mFile;

    // LISTENER
    private OnContactFileListener mListener;

    public ContactFileListItemView(Context context, OnContactFileListener listener) {
        this(context);
        mListener = listener;
    }

    protected ContactFileListItemView(Context context) {
        super(context);
        inflate(getContext(), R.layout.list_item_file, this);

        mSettings = LTSettings.getInstance(getContext());
        mEmployeeCache = EmployeeCache.getInstance(getContext());
        mSb = new StringBuilder();
        mContactFileExist = getResources().getString(R.string.task_file_exist);

        mName = (TextView) findViewById(R.id.file_name);
        mSize = (TextView) findViewById(R.id.file_size);
        mRemove = (ImageView) findViewById(R.id.file_remove);
        {

            final int removeResId;
            final int fileResId;

            if (mSettings.isThemeDark()) {
                removeResId = R.drawable.file_remove_white;
                fileResId = R.drawable.attached_files64_white;

            } else {
                removeResId = R.drawable.file_remove_gray;
                fileResId = R.drawable.attached_files64;
            }

            mRemove.setImageResource(removeResId);
            ((ImageView) findViewById(R.id.file_icon)).setImageResource(fileResId);
        }
        findViewById(R.id.file_info).setOnClickListener(this);
        mRemove.setOnClickListener(this);

    }

    public void setData(ContactFile file) {
        mFile = file;

        mName.setText(mFile.getFileName());
        final String emailCreator = mFile.getEmailCreator();

        Utils.clearStringBuilder(mSb);
        if (!TextUtils.isEmpty(emailCreator) && mSettings.getUserName().equals(emailCreator)) {
            Utils.changeVisibility(mRemove, View.VISIBLE);

        } else {
            Utils.changeVisibility(mRemove, View.GONE);

            mSb.append(mEmployeeCache.find(emailCreator));
            mSb.append(SharedStrings.COMMA_C);
            mSb.append(SharedStrings.SPACE_C);
        }

        mSb.append(FileWorker.getFileSize(getContext(), mFile.getFileSize()));
        if (!mFile.isFileExist()) {
            mSb.append(mContactFileExist);
        }

        mSize.setText(mSb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.file_info:
            mListener.onContactFileClick(mFile, false);
            break;

        case R.id.file_remove:
            mListener.onContactFileClick(mFile, true);
            break;

        default:
            break;
        }
    }
}