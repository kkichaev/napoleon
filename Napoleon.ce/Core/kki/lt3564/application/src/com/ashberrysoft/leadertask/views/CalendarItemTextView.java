package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

import java.util.Calendar;

public class CalendarItemTextView extends TextView {

    private Paint mDotPaint;
    private float mDotRadius;
    private boolean mHasTasks;

    public CalendarItemTextView(Context context) {
        super(context);
        initialization();
    }

    private void initialization() {

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStyle(Paint.Style.FILL);

        mDotRadius = getResources().getDimension(R.dimen.univ_padding_tiny);
    }

    public void setColor(int color, boolean hasTasks) {
        mDotPaint.setColor(color);
        mHasTasks = hasTasks;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHasTasks) {
            canvas.drawCircle(this.getWidth() / 2, this.getHeight() - mDotRadius * 3, mDotRadius, mDotPaint);
        }
    }
}