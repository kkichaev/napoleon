package com.grsoft.manager;

import android.content.Context;

public class DrawerHelperEx extends DrawerHelper {
    @Override
    protected int getLeftMenuID() {
        return R.menu.main_navigation_ex;
    }

    @Override
    protected void childItemClick(Context context, int id) {
        if(id == R.id.return_requests) {
            ReturnRequest.open(context);
        }
    }
}
