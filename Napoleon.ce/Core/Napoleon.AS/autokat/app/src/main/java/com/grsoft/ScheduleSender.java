package com.grsoft;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrgSchedule;
import com.grsoft.network.ObjectExportListener;

import java.util.List;

public class ScheduleSender extends Hitching implements ObjectExportListener {

    List<OrgSchedule> data;
    public ScheduleSender() {
        super(OrgSchedule.class, "OrgSchedule");

        data = DbReader.fetch(OrgSchedule.class, "params = 0");
    }

    @Override
    public int size() { return data.size(); }

    @Override
    public DataObject get(int i) {
        return data.get(i);
    }

    @Override
    public void onEnd() {
        super.onEnd();
        DbWriter w = new DbWriter();
        for(OrgSchedule o : data) {
            o.params |= OrgSchedule.EXPORTED;
            w.insertRecord(o);
        }
        w.close();
    }
}
