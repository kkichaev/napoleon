package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.RouteDeviation;
import com.grsoft.network.ObjectExportListener;

import java.util.List;

public class RouteDeviationSender extends Hitching implements ObjectExportListener {
    List<RouteDeviation> data;
    public RouteDeviationSender(List<RouteDeviation> data) {
        super(RouteDeviation.class);
        this.data = data;
    }

    @Override public int size() { return data.size(); }
    @Override public DataObject get(int i) { return data.get(i); }

    @Override
    public void onEnd() {
        DbWriter w = new DbWriter();
        for(RouteDeviation r : data) {
            r.exported = 1;
            w.insertRecord(r);
        }
        w.close();
    }
}
