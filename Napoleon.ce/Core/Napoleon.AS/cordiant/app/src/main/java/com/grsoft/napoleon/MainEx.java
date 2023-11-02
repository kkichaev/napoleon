package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.Org;

public class MainEx extends Main{
    @Override
    public void openReports() {
        PlanActivity.open(this);
    }

    @Override
    protected void drawOrg(Org org, View view) {
        super.drawOrg(org, view);

        view.findViewById(R.id.tvOrgSum).setVisibility(View.GONE);
    }
}
