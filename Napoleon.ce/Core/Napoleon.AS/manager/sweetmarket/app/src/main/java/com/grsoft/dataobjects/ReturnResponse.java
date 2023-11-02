package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

@TableInfo(name="ReturnResponse", keyFields = "userid,created")
@ServerInfo(name="ReturnResponse")
public class ReturnResponse extends DataObject {
    public String userid = "";
    public String id = "";
    public Date created = new Date();
    public String remark = "";

    public int response = 0;

    @Scale(value = Consts.SUM_SCALE)
    public long sum = 0;
}
