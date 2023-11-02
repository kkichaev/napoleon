package com.grsoft.napoleon;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class MainEx extends Main {

    @Override
    protected void setOrgBackground(int pos, Org org, View v) {
        super.setOrgBackground(pos, org, v);
        if(org instanceof  OrgEx && ((OrgEx)org).isVip != 0) {
            v.setBackgroundResource(R.drawable.list_ltred_selector);
        }
    }
}
