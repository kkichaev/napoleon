package com.serviko.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.sales.R;

public class PriceViewLayout extends FrameLayout {
    View filterFragment;
    Handler handler;

    public interface Handler {
        void clickedOutside();
    }

    public PriceViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(filterFragment == null)
            filterFragment = findViewById(R.id.filter_fragment);
        if(filterFragment != null) {
            int w = filterFragment.getWidth();
            int h = filterFragment.getHeight();
            if(w > 0 && h > 0) {
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();

                int[] l = new int[2];
                filterFragment.getLocationOnScreen(l);
                if((x < l[0] || x > l[0] + w) || (y < l[1] || y > l[1] + h)) {
                    if(handler != null) {
                        handler.clickedOutside();
                        return true;
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
