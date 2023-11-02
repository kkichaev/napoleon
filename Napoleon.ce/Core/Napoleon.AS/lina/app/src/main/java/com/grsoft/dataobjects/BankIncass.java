package com.grsoft.dataobjects;


import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="BankIncass", keyFields = "created")
@ServerInfo(name="BankDoc")
public class BankIncass extends CreateDocDataObject {
    @Scale(value = Consts.SUM_SCALE)
    public long sum = 0;

    public String picture = "";
}
