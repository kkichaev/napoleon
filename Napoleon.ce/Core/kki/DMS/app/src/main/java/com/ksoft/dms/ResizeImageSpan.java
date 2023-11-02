package com.ksoft.dms;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

public class ResizeImageSpan extends ImageSpan {

    private static final int MIN_SCALE_WIDTH = 240;

    // TextView's width.
    private int mContainerWidth;

    public ResizeImageSpan(Context  d, Uri source, int containerWidth) {
        super(d, source, 0);
        mContainerWidth = containerWidth;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = getResizedDrawableBounds(d);

        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return rect.right;
    }

    private Rect getResizedDrawableBounds(Drawable d) {
        if (d == null || d.getIntrinsicWidth() == 0) {
            return new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
        int scaledHeight;

        if (d.getIntrinsicWidth() < mContainerWidth ) {
            // Image smaller than container's width.
            if (d.getIntrinsicWidth() > MIN_SCALE_WIDTH &&
                    d.getIntrinsicWidth() >= d.getIntrinsicHeight()) {
                // But larger than the minimum scale size, we need to scale the image to fit
                // the width of the container.
                int scaledWidth = mContainerWidth;
                scaledHeight = d.getIntrinsicHeight() * scaledWidth / d.getIntrinsicWidth();
                d.setBounds(0, 0, scaledWidth, scaledHeight);
            } else {
                // Smaller than the minimum scale size, leave it as is.
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            }
        } else {
            // Image is larger than the container's width, scale down to fit the container.
            int scaledWidth = mContainerWidth;
            scaledHeight = d.getIntrinsicHeight() * scaledWidth / d.getIntrinsicWidth();
            d.setBounds(0, 0, scaledWidth, scaledHeight);
        }

        return d.getBounds();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null) {
            d = wr.get();
        }

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}
