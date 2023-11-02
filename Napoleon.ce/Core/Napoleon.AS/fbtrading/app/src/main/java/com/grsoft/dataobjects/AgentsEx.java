package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;

public class AgentsEx extends Agent {
    public String userid = "";

    public static AgentsEx me() {
        AgentsEx ret = new AgentsEx();

        DbReader r = new DbReader();
        r.select(ret, ret.getTableName(), "id = userid");
        r.close();

        return ret;
    }
}
