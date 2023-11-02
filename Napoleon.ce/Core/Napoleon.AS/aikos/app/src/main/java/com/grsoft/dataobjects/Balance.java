package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="balance", keyFields = "id")
@ServerInfo(name="Balance")
public class Balance extends DataObject {
    public String id = "";

    @Scale(value=100)
    public long sum = 0;

    public List<BalanceItem> documents = new ArrayList<>();

    static public List<String> getOverdueOrgs() {
        Date now = new Date();
        List<String> ret = new ArrayList<>();

        for(Balance b : DbReader.fetch(Balance.class)) {
            for(BalanceItem bi : b.documents) {
                if(bi.sum > 0 && bi.payDate.compareTo(now) < 0) {
                    ret.add(b.id);
                    break;
                }
            }
        }
        return ret;
    }
}
