package com.ashberrysoft.leadertask.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class CommunicationListItemView extends RelativeLayout implements OnClickListener {

    public interface OnCommunicationListItemListener {
        public void onCommunicationRemove(int position);
    }

    // VIEW
    private TextView mName;
    private TextView mValue;
    private ImageView mImage;
    private ImageView mImageDelete;

    // VALUE's
    private int mPosition;

    // LISTENER
    private OnCommunicationListItemListener mListener;

    public CommunicationListItemView(Context context) {
        super(context);
        initialization();
    }

    public CommunicationListItemView(Context context, OnCommunicationListItemListener listener) {
        super(context);

        initialization();
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_communication, this);

        mName = (TextView) findViewById(R.id.text_view);
        mValue = (TextView) findViewById(R.id.text_view_main);
        mImage = (ImageView) findViewById(R.id.image_view);
        mImageDelete = (ImageView) findViewById(R.id.image_view_remove);
        mImageDelete.setImageResource(R.drawable.file_remove_gray);
        mImageDelete.setOnClickListener(this);
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Activity activity = (Activity) getContext();
                final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
                ad.setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", mValue.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Utils.showToast(activity, getResources().getString(R.string.saved_clipboard));
                        dialog.dismiss();
                    }
                });
                ad.create().show();
                return false;
            }
        });
    }

    public void setData(int position, String allString, boolean canDelete, final Context mainContext) {
        String type = "";
        String name = "";
        String value = "";
        int count = 0;
        for(int j=0; j<allString.length(); j++)
        {
            int indexSub = allString.indexOf("\t", j);
            String subStringComm = "";
            if (indexSub != -1) {
                subStringComm = allString.substring(j, indexSub);
            }
            else {
                subStringComm = allString.substring(j, allString.length());
                indexSub = allString.length();
            }
            if (count==0) {
                type = subStringComm;
            } else if (count==1) {
                value = subStringComm;
            }else if (count==2) {
                name = subStringComm;
            }

            j = indexSub;
            count++;
            if (j+1 == allString.length() ) {
                name = "";
                break;
            }
        }

        mPosition = position;
        switch (type) {
            case "msg":
                mImage.setImageResource(R.drawable.comm_msg);
                break;

            case "eml":
                mImage.setImageResource(R.drawable.comm_email);
                this.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto: "+mValue.getText().toString()));
                        mainContext.startActivity(Intent.createChooser(emailIntent, mainContext.getString(R.string.what_to_use)));
                    }
                });
                break;

            case "www":
                mImage.setImageResource(R.drawable.comm_www);
                this.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent browser;
                        if (mValue.getText().toString().indexOf("http://") == -1 && mValue.getText().toString().indexOf("https://") == -1)
                        {
                            browser = new Intent(Intent.ACTION_VIEW,  Uri.parse("http://"+mValue.getText().toString()));
                        } else {
                            browser = new Intent(Intent.ACTION_VIEW,  Uri.parse(mValue.getText().toString()));
                        }
                        mainContext.startActivity(browser);
                    }
                });
                break;

            default:
            case "tel":
                mImage.setImageResource(R.drawable.comm_phone);
                this.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mValue.getText().toString()));
                        mainContext.startActivity(intent);
                    }
                });
                break;
        }

        mValue.setText(value);
        mName.setText(name == null || name == "" ? "" : "(" + name + ")");

        if (!canDelete) {
            mImageDelete.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
            case R.id.image_view_remove:
                mListener.onCommunicationRemove(mPosition);
            default:
                break;
            }
        }
    }

    public void setCustomListener(OnCommunicationListItemListener listener) {
        mListener = listener;
    }
}