package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grsoft.network.ProgressHelper;
import com.grsoft.network.ProgressValue;
import com.grsoft.network.UpdateProcessInfo;
import com.grsoft.view.SimpleMessageBox;

public class SyncProgress extends ProgressHelper {

    protected static final int REFRESH_INTERVAL = 1000; // in ms
    private long lastUpdate;

    protected static final int TITLE_UPDATE = R.string.base_updating;
    protected static final int REQUEST_STR = R.string.wait_for_server_answer;
    protected static final int WRITE_STR = R.string.save_to_base;
    protected static final int TITLE_SEND = R.string.data_sending;
    protected static final int ENDREQUESTSEND_MSG = R.string.data_sending;
    protected static final int STEP_SEND_MSG = R.string.wait_form_server_answer;
    protected static final int GPS_SEND_MSG = R.string.wait_for_send_gps;

    AlertDialog dialog;
    View view;
    public SyncProgress(Activity owner) {
        super(owner);
    }

    @Override
    protected void createProgressDialog(int title, int message) {
        if(dialog == null) {

            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(title);

            view = View.inflate(context, R.layout.sync_progress, null);
            b.setView(view);

            TextView tv = view.findViewById(R.id.progress_message);
            tv.setText(message);

            setPicCount(0);
            updateProgress(0);
            updatePicProgress(0);

            dialog = b.create();
            dialog.show();
        } else {
            dialog.setTitle(title);
            dialog.setMessage(context.getString(message));
        }
    }

    @Override
    public void onUpdate(ProgressValue value) {
        UpdateProcessInfo.UpdateStatus status = value.status;

        long now = SystemClock.uptimeMillis();
        if( status == UpdateProcessInfo.UpdateStatus.STEP && now - lastUpdate < REFRESH_INTERVAL )
            return;
        lastUpdate = now;

        updateStatus(value);
    }

    protected void updateStatus(ProgressValue value) {
        UpdateProcessInfo.UpdateStatus status = value.status;
        int progress = value.progress;

        switch(status) {
            case BEGIN_UPDATE:
                createProgressDialog(TITLE_UPDATE, REQUEST_STR);
                break;

            case BEGIN_SEND:
                createProgressDialog(TITLE_SEND, REQUEST_STR);
                break;

            case BEGIN_SEND_VISITS:
                createProgressDialog(TITLE_SEND, R.string.sending_data);
                setMax(progress);
                break;

            case ENDREQUEST_UPDATE:
                setMax(progress);
                setMessage(WRITE_STR);
                break;

            case ENDREQUEST_SEND:
                setMax(3);
                setMessage(ENDREQUESTSEND_MSG);
                updateProgress(2);

            case STEP:
                updateProgress(progress);
                break;

            case STEP_SEND:
                setMessage(STEP_SEND_MSG);
                updateProgress(3);
            case END:
                break;

            case END_OF_PROCESS:
                if (dialog != null && dialog.isShowing() )
                    try{
                        dialog.dismiss();
                    }catch(Exception e){

                    }
                break;

            case SHOW_MESSAGE:
                if (value.simpleMessageBox != null) {
                    value.simpleMessageBox.show();
                }
                break;
            case GPS_UPDATE:
                setMessage(GPS_SEND_MSG);
                break;
            default:
                break;
        }
    }

    void setMessage(int msg) {
        ((Activity)context).runOnUiThread(() -> {
            TextView tv = view.findViewById(R.id.progress_message);
            tv.setText(msg);
        });
    }

    void setMax(int count) {
        ((Activity)context).runOnUiThread(() -> {
            ProgressBar pb = view.findViewById(R.id.progress);
            pb.setMax(count);
//            updateProgress(0);
        });
    }

    public void setPicCount(int count) {
        ((Activity)context).runOnUiThread(() -> {
            ProgressBar pb = view.findViewById(R.id.progress_photo);
            pb.setMax(count);
//            updatePicProgress(0);
        });
    }

    void updateProgress(int count) {
        ((Activity)context).runOnUiThread(() -> {
            update(R.id.progress_prc, R.id.progress_cnt, R.id.progress, count, "");
        });
    }

    public void updatePicProgress(int count) {
        ((Activity)context).runOnUiThread(() -> {
            update(R.id.progress_photo_prc, R.id.progress_photo_cnt, R.id.progress_photo, count, "Фото ");
        });
    }

    void update(int prcId, int textId, int progressId, int count, String prefix ) {
        ProgressBar pb = view.findViewById(progressId);
        int max = pb.getMax();
        TextView tv;

        if(count > max)
            count = max;
        tv = view.findViewById(prcId);
        tv.setText(String.format("%d%%", max == 0 ? 0 : (int)(count * 100 / max)));

        tv = view.findViewById(textId);
        tv.setText(prefix + String.format("%d/%d", count, max));

        pb.setProgress(count);
    }
}
