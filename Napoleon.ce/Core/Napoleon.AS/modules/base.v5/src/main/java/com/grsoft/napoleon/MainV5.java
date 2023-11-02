package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

public class MainV5 extends Main{
    @Override
    protected BaseAdapter createFoldersMainAdapter() {
        return new FoldersMainAdapterV5(this);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        CfgNplW c = (CfgNplW) ConfigManager.getConfig();
        if(c.serverCode.length() == 0) {
            Setting.open(this, Configuration.class);
        }
    }
}
