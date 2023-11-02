package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassDebDistrItem;
import com.grsoft.dataobjects.IncassDebDistrItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

import java.util.Map;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
    }

    @Override protected String orgInfo(Org o) {
        return ((OrgEx)o).fullName();
    }
    @Override protected int getContentViewID() { return R.layout.incass_deb_distr_ex; }

    @Override
    protected IncassDebDistrItem createItem(Map.Entry<DlvKey, Long> e) {
        IncassDebDistrItemEx ret = new IncassDebDistrItemEx();
        ret.link = ((DeliveryEx)e.getKey().delivery).link;
        return ret;
    }
}
