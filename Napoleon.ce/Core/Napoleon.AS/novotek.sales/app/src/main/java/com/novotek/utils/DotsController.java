package com.novotek.utils;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.novotek.sales.R;

public class DotsController {
    ImageView[] dotViews;
    LinearLayout dots;

    public DotsController(RecyclerView view, LinearLayout dots) {
        this.dots = dots;

        if(view != null) {
            view.setOnScrollChangeListener((v, cx, i1, i2, i3) -> {
                if (dotViews.length == 0)
                    return;

                float step = (view.computeHorizontalScrollRange() - view.computeHorizontalScrollExtent()) / dotViews.length;
                int cv = (int) (view.computeHorizontalScrollOffset() / step);
                setCurrent(cv);
            });
        }
    }

    public void setCurrent(int pos) {
        if (pos >= dotViews.length)
            pos = dotViews.length - 1;
        for (int i = 0; i < dotViews.length; i++) {
            dotViews[i].setImageResource(i == pos ? R.drawable.ic_white : R.drawable.ic_grey);
        }
    }

    public void update(int count) {
        dots.removeAllViews();
        dotViews = new ImageView[count];

        for(int i=0; i<count; i++) {
            ImageView tv = new ImageView(dots.getContext());

            tv.setImageResource(i == 0 ? R.drawable.ic_white : R.drawable.ic_grey);
            dotViews[i] = tv;

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 8;
            lp.rightMargin = 8;

            dots.addView(tv, lp);
        }
    }
}
