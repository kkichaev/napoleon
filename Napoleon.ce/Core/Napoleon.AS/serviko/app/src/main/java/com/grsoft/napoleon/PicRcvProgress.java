package com.grsoft.napoleon;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PicRcvProgress {
    int totalCount;
    PopupWindow win;
    ProgressBar pb;
    TextView tv;

    public PicRcvProgress(int totalCount, View atView) {
        this.totalCount = totalCount;

        View v = LayoutInflater.from(atView.getContext()).inflate(R.layout.pic_rcv_progress, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        win = new PopupWindow(v, width, height, false);

        int hVal = 150;
        DisplayMetrics metrics = atView.getContext().getResources().getDisplayMetrics();
        hVal = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, hVal, metrics);
        win.showAtLocation(atView, Gravity.BOTTOM, 0, hVal);

        pb = v.findViewById(R.id.progress);
        tv = v.findViewById(R.id.progress_message);
        pb.setMax(totalCount);
        tv.setText(makeText(0));
    }

    public void close() {
        try {
            win.dismiss();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    String makeText(int cur) {
        return String.format("Загрузка фото %d из %d", cur, totalCount);
    }
}
