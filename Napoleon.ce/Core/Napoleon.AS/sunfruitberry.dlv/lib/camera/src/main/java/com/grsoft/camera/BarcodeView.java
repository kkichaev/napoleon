package com.grsoft.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
    int holeWidth;
    int holeHeight;

    public BarcodeView(@NonNull Context context) {
        super(context);
        init();
    }

    public BarcodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarcodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BarcodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        hole_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hole_paint.setStyle(Paint.Style.FILL);
        hole_paint.setColor(0xFFFFFFFF);
        hole_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        bkg_color = getResources().getColor(R.color.preview_background);

        holeWidth = (int)getResources().getDimension(R.dimen.hole_size_width) / 2;
        holeHeight = (int)getResources().getDimension(R.dimen.hole_size_height) / 2;
    }

    public SizeF getHoleCoef() {
        return new SizeF((float)(holeWidth)/ getWidth(), (float)(holeHeight) / getHeight() );
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        Rect hole_rect = new Rect(
                cx - holeWidth , cy - holeHeight,
                cx + holeWidth, cy + holeHeight);

        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);

        Canvas c = new Canvas(bmp);
        c.drawColor(bkg_color);
        c.drawRect(hole_rect, hole_paint);

        canvas.drawBitmap(bmp, 0, 0, null);
    }
}
