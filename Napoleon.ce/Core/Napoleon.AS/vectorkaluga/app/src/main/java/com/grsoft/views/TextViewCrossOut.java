package com.grsoft.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class TextViewCrossOut extends AppCompatTextView {
    boolean crossCut = true;
    Paint paint = new Paint();

    public TextViewCrossOut(Context context) {
        this(context, null);
    }

    public TextViewCrossOut(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextViewCrossOut(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(getCurrentTextColor());
        paint.setStrokeWidth(3);
    }

    public void setCrossOut(boolean cc) {
        crossCut = cc;
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(crossCut) {
            int r = getWidth() - getPaddingRight();
            int b = getHeight() - getPaddingBottom();
            int l = getPaddingLeft();
            int t = getPaddingTop();
            canvas.drawLine(l, b, r, t, paint);
        }
    }
}
