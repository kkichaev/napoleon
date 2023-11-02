package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;

/**
 * EditText с линиями
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
public class LinedEditText extends EditText {

    private static Paint linePaint = getLinePaint();

    // private static final int LightblueColor = 0xFF559de5;

    public LinedEditText(Context context, AttributeSet attributes) {
        super(context, attributes);
        // TODO Bug #3515 "setMovementMethod" was commented
        // setMovementMethod(new ScrollingMovementMethod());
        setLineColor(context);
    }

    public void setLineColor(Context context) {
        /*
         * if(((LeaderTaskApplication)context.getApplicationContext()).getSettings().isTheme()){
         * linePaint.setColor(context.getResources().getColor(R.color.white)); }else{
         */
        linePaint.setColor(context.getResources().getColor(R.color.gray));
        // }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();
        int firstLineY = getLineBounds(0, bounds);
        int lineHeight = getLineHeight();
        int totalLines = Math.max(getLineCount(), getHeight() / lineHeight);

        for (int i = 0; i < totalLines; i++) {
            int lineY = firstLineY + i * lineHeight + 3;
            canvas.drawLine(bounds.left, lineY, bounds.right, lineY, linePaint);
        }

        super.onDraw(canvas);
    }

    private static Paint getLinePaint() {
        final Paint paint = new Paint();
        paint.setStyle(Style.STROKE);

        return paint;
    }
}