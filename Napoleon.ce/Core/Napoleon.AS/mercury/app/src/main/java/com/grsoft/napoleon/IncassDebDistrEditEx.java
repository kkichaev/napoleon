package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
    @Override
    protected String orgInfo(Org o) {
        String ret = super.orgInfo(o);
        String info = ((OrgEx) o).info;
        if (info.length() > 0) {
            ret += "<br/>" + info;
        }

        return ret;
    }
}
