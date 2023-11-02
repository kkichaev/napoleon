package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
    public String ido = "";

    @Scale(value = Consts.SUM_SCALE)
    public int balance = 0;

    public String fullName() {
        if(ido.length() == 0)
            return name;

        String ret = "";
        OrgEx oe = new OrgEx();
        DbReader r = new DbReader();
        if(r.select(oe, oe.getTableName(), "id='" + ido + "'")) {
            ret = oe.name + " / ";
        }
        r.close();

        return ret + name;
    }
}
