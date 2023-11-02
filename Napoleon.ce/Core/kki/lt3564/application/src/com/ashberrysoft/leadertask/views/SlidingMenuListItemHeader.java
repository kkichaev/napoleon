package com.ashberrysoft.leadertask.views;

import android.content.Context;

import com.ashberrysoft.leadertask.R;

/**
 * Отображение заголовка элементов фильтра
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 */

public class SlidingMenuListItemHeader extends SlidingMenuListItem {

    public SlidingMenuListItemHeader(Context context) {
        super(context);
    }

    @Override
    protected void inflateView() {
        inflate(getContext(), R.layout.list_item_sliding_menu_header, this);
    }
}