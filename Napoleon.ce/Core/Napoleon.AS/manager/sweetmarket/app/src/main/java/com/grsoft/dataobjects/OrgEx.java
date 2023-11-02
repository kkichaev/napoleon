package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="mgr_org", keyFields = "id")
public class OrgEx extends Org {
    @Scale(value = Consts.SUM_SCALE)
    public int income = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int debet = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int overdue = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int limit = 0;
}
