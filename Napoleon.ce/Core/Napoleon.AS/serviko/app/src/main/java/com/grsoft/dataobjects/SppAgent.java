package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name = "sppAgent", keyFields = "id,planGroup")
@ServerInfo(name="SppAgent")
public class SppAgent extends DataObject{
    public String id = "";
    public String planGroup = "";

    @Scale(value = Consts.SUM_SCALE)
    public int monthPlan = 0;

    @Scale(value = Consts.SUM_SCALE)
    public int monthFact = 0;

    public List<SppAgentItem> items = new ArrayList<>();
}
