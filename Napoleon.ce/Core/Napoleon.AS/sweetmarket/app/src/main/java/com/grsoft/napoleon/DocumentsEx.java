package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
    @Override
    protected String orgInfo(Org o) {
        String ret = super.orgInfo(o) + "<br/>";
        OrgEx oe = (OrgEx)o;

        ret += String.format(getString(R.string.org_info)
                , Util.IntToScaleStr(oe.debet, Consts.SUM_SCALE, Util.DEC_DELIM, false)
                , Util.IntToScaleStr(oe.overdue, Consts.SUM_SCALE, Util.DEC_DELIM, false)
                , Util.IntToScaleStr(oe.income, Consts.SUM_SCALE, Util.DEC_DELIM, false)
                , Util.IntToScaleStr(oe.limit, Consts.SUM_SCALE, Util.DEC_DELIM, false)
        );
        return ret;
    }
}
