package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReturnInfo extends DataObject {
    public static final int REQUEST_NEW = -1;
    public static final int REQUEST_REJECT = 0;
    public static final int REQUEST_CONFIRM = 1;

    public String userid = "";
    public Date created = new Date();
    public String id = "";
    public String org = "";
    public String agent = "";
    public int response = -1;

    public String address = "";

    @Scale(value= Consts.SUM_SCALE)
    public int orglimit = 0;

    @Scale(value= Consts.SUM_SCALE)
    public int agentlimit = 0;


    public boolean confirmed() { return response == REQUEST_CONFIRM; }
    public boolean rejected() { return response == REQUEST_REJECT; }
    public boolean newRequest() { return response == REQUEST_NEW; }

    static String stmt() {
        return
            "select d.userid, d.created, d.id, d.name as org, a.name as agent, ifnull(d.response, -1) as response " +
            ", ifnull(ai.\"limit\", 0) as agentlimit, ifnull(d.\"limit\", 0) as orglimit, d.address " +
            " from (select r.userid, r.created, r.id, o.name, o.address, rr.response, o.\"limit\" from Return r " +
            "    left join mgr_org o on r.id = o.id " +
            "    left join ReturnResponse rr on r.userid = rr.userid and r.created = rr.created" +
            " ) d " +
            "    left join ManagerAgent a on d.userid = a.id " +
            "    left join AgentInfo ai on d.userid = ai.userid " +
            "  order by created desc";
    }

    public static List<ReturnInfo> get() {
        List<ReturnInfo> res = new ArrayList<>();
        DbReader r = new DbReader();

        ReturnInfo ri = new ReturnInfo();
        boolean bdo = r.selectStmt(ri, stmt());
        while(bdo) {
            res.add(ri);
            ri = new ReturnInfo();
            bdo = r.selectNext(ri);
        }
        r.close();
        return res;
    }
}
