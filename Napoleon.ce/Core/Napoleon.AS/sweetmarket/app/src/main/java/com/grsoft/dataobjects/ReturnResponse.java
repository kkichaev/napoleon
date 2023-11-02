package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

@TableInfo(name="ReturnResponse", keyFields = "created")
@ServerInfo(name="ReturnResponse")
public class ReturnResponse extends DataObject {
    public static final int REQUEST_REJECT = 0;
    public static final int REQUEST_CONFIRM = 1;

    //    public String userid = "";
    public String id = "";
    public Date created = new Date();
    public String remark = "";

    public int response = 0;

    @Scale(value = Consts.SUM_SCALE)
    public long sum = 0;

    public boolean rejected() { return response == REQUEST_REJECT; }
}
