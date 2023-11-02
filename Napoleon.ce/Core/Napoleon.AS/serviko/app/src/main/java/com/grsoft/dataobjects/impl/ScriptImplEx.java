package com.grsoft.dataobjects.impl;

import android.app.Activity;
import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.RouteDeviation;
import com.grsoft.napoleon.NapoleonApp;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScriptImplEx extends ScriptImpl {
    @Override
    protected boolean initChildDoc(Context c, int index, CreatableDocument<?> doc, ScriptItem item) {
        boolean res = super.initChildDoc(c, index, doc, item);
        if(isDocInited(index)) {
            if(c instanceof Activity) {
                OrgImpl oi = new OrgImpl();
                oi.read("id", data.id);
                
                if (isShortVisitTime(item)) {
                    ((NapoleonApp) ((Activity)c).getApplication()).addAlert(oi.getData(), RouteDeviation.SHORT_VISIT_TIME);
                }
            }
        }
        return res;
    }

    private boolean isShortVisitTime(ScriptItem item) {
        try {
            StringBuilder sb = new StringBuilder();
            ConfigImpl ci = new ConfigImpl();
            if (ci.getValue(sb, "MinVisitDuration")) {
                int val = Integer.parseInt(sb.toString()) * 60 * 1000;
                ScriptItem si = data.items.get(0);
                Date start = si.state == ScriptItem.DOC_INITED ? si.date : data.created;
                long diff = item.date.getTime() - start.getTime();
                return diff < val;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean isDocInited(int index) {
        for( ; index < data.items.size(); index++ ) {
            ScriptItem si = data.items.get(index);
            if(!si.isCompleete())
                return false;
        }
        return true;
    }
}
