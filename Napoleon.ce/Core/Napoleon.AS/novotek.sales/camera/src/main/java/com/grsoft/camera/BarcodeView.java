package com.grsoft.camera;

import android.annotation.SuppressLint;
import android.content.Context;
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
    int holeHalfWidth;
    int holeHalfHeight;

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

        holeHalfWidth = (int)getResources().getDimension(R.dimen.hole_size_width) / 2;
        holeHalfHeight = (int)getResources().getDimension(R.dimen.hole_size_height) / 2;
    }

    public SizeF getHoleCoef() {
        return new SizeF((float)(holeHalfWidth * 2)/ getWidth(), (float)(holeHalfHeight * 2) / getHeight() );
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        Rect hole_rect = new Rect(
                cx - holeHalfWidth, cy - holeHalfHeight,
                cx + holeHalfWidth, cy + holeHalfHeight);

        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);

        Canvas c = new Canvas(bmp);
        c.drawColor(bkg_color);
        c.drawRect(hole_rect, hole_paint);

        canvas.drawBitmap(bmp, 0, 0, null);
    }
}
