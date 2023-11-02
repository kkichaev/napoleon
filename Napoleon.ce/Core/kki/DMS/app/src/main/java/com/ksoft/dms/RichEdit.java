package com.ksoft.dms;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RichEdit extends androidx.appcompat.widget.AppCompatEditText {
    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;
    private  ImageClickListener imageClickListener;

    interface ImageClickListener{
        void onClick(View v, String src);
    }

    public void setImageClickListener(ImageClickListener listener){
        imageClickListener = listener;
    }

    public RichEdit(@NonNull Context context) {
        super(context);
    }

    public RichEdit(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RichEdit(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSelectionChanged(int start, int end) {
        ImageSpan[] sp = getText().getSpans(start, end, ImageSpan.class);
        setCursorVisible(sp.length == 0);
    }

    private void onClickImage(String source) {
        if (imageClickListener != null)
            imageClickListener.onClick(this, source);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                break;

            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (isAClick(startX, endX, startY, endY)) {
                    if (imageClick(event, true))
                        return true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
    }

    private boolean imageClick(MotionEvent event, boolean fire) {
        Layout layout = getLayout();
        float x = event.getX() + getScrollX();
        float y = event.getY() + getScrollY();
        int line = layout.getLineForVertical((int) y);
        int offset = layout.getOffsetForHorizontal(line, x);

        if(offset>0) {
            ImageSpan[] sp = getText().getSpans(offset, offset, ImageSpan.class);

            if (sp.length > 0 && fire){
                onClickImage(sp[0].getSource());
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


}
