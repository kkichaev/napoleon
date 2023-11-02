package com.grsoft.napoleon;

import com.grsoft.dataobjects.MoneyProxy;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class MoneProxyFormEx extends MoneyProxyForm {

    @Override
    String getTitle(Org org) {
        String ret = super.getTitle(org);
        ret += "<br/>Долг контрагента: <b>" + Util.IntToScaleStr(((OrgEx)org).balance, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
        return ret;
    }

}
