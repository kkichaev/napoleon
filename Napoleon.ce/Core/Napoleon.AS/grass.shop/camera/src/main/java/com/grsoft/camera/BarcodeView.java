package com.grsoft.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BarcodeView extends View {

    private Paint hole_paint;
    private int bkg_color;
    float lc = 0.15F, rc = 0.85F, tc = 0.2F, bc = 0.6F;

    public BarcodeView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public BarcodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BarcodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public BarcodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs){
        if(attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BarcodeView, 0, 0);

            try {
                lc = a.getFloat(R.styleable.BarcodeView_leftCoef, 0.15F);
                rc = a.getFloat(R.styleable.BarcodeView_rightCoef, 0.85F);
                tc = a.getFloat(R.styleable.BarcodeView_topCoef, 0.2F);
                bc = a.getFloat(R.styleable.BarcodeView_bottomCoef, 0.6F);
            } finally {
                a.recycle();
            }
        }

        hole_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hole_paint.setStyle(Paint.Style.FILL);
        hole_paint.setColor(0xFFFFFFFF);
        hole_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        bkg_color = getResources().getColor(R.color.preview_background);
    }

    public SizeF getHoleCoef(SizeF parent) {
//        float cw = (float)(parent.getWidth())/ getWidth();
//        if(cw > 1.0) cw = 1.0F;
//        float ch = (float)(parent.getHeight()) / getHeight();
//        if(ch > 1.0) ch = 1.0F;

        return new SizeF(rc-lc, bc-tc );
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect hole_rect = new Rect(
                (int)(getWidth() * lc), (int)(getHeight() * tc),
                (int)(getWidth() * rc), (int)(getHeight() * bc));

        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);

        Canvas c = new Canvas(bmp);
        c.drawColor(bkg_color);
        c.drawRect(hole_rect, hole_paint);

        canvas.drawBitmap(bmp, 0, 0, null);
    }
}
