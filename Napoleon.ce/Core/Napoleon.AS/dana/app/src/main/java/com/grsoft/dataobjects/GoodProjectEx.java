package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

public class GoodProjectEx extends GoodProject {
    static public Map<Object, DisableOrg> dsbl = new HashMap<>();


    @Override
    public String toString() {
        String ret = name;
        DisableOrg dso = dsbl.get(idOrg);
        if(dso != null) {
            if (dso.block > 0) {
                ret += ", запрет продажи";
            } else if (dso.creditDisable > 0) {
                ret += ", запрет кредита";
            }
        }
        return ret;
    }
}
