package com.grsoft.dataobjects.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;


import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

public class ScriptImplEx extends ScriptImpl {
    private boolean hasSales(ScriptDef scriptDef) {
        boolean res = false;

        for(ScriptDefItem i : scriptDef.items)
            if (i.curType.equals("Sales")){
                res = true;
                break;
            }

        return res;
    }

    @Override
    protected boolean initInternal(Context c, String orgId, GpsCoord gpsCoord, ScriptDef srciptDef) {
        if (SalesBanImpl.isOrgBanned(orgId) && hasSales(srciptDef)) {
            Toast.makeText(c, R.string.sales_ban, Toast.LENGTH_SHORT).show();
            return false;
        }else
            return super.initInternal(c, orgId, gpsCoord, srciptDef);
    }
}
