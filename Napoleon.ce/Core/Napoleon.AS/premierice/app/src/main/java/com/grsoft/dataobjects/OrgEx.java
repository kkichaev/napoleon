package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {

    public static final int WRK_CND_NAL_BUH_FACT = 1;
    public static final int WRK_CND_NAL_BUH_DELAY = 2;
    public static final int WRK_CND_NAL_TORG_FACT = 4;
    public static final int WRK_CND_NAL_TORG_DELAY = 8;
    public static final int WRK_CND_BEZNAL_BUH_DELAY = 16;
    public static final int WRK_CND_BONUS_DOC = 32;

    public int isVip = 0;
    public String stopMsg = "";
    public String route = "";

    public int workCondition = 0;

    @Scale(value = Consts.SUM_SCALE)
    public int creditLimit = 0;

    public int delay = 0;
    public int deliveryDelay = 0;

    public List<Rfrgr> rfrgr = new ArrayList<>();

    @Override public boolean isStopList() { return stopMsg.length() > 0; }

    public boolean onlyNal() { return (workCondition & WRK_CND_BEZNAL_BUH_DELAY) == 0;}
    public boolean canChangeNal() {
        return ((workCondition & WRK_CND_BEZNAL_BUH_DELAY) != 0) && ((workCondition & (WRK_CND_NAL_BUH_FACT|WRK_CND_NAL_BUH_DELAY|WRK_CND_NAL_TORG_FACT|WRK_CND_NAL_TORG_DELAY)) != 0);
    }
}
