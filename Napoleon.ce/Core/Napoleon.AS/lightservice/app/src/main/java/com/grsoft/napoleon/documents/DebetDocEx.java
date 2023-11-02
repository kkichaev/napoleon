package com.grsoft.napoleon.documents;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.Main;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebetDocEx extends DebtDoc {
    public static void init() {
        instance = new DebetDocEx();
    }

    @Override
    protected String getOrgWhere(String orgId) {
        String ret = "";
        if(orgId != null) {
            OrgImpl oi = new OrgImpl();
            if(oi.read("id", orgId)) {
                ret = "id='" + ((OrgEx)oi.getData()).ido + "'";
            }
        }
        return ret;
    }

    @Override
    public void refreshDocSum() throws RuntimeException {
        final  Map<String, List<String>> orgs = new HashMap<>();
        DataTraveler.travel(OrgEx.class, new DataTraveler.Travel<OrgEx>(){

            @Override
            public boolean travel(DataTraveler<OrgEx> item) {
                List<String> data = orgs.get(item.data.ido);
                if(data == null) {
                    data = new ArrayList<>();
                    orgs.put(item.data.ido, data);
                }
                data.add(item.data.id);
                return true;
            }
        }, "");

        DbWriter.checkDBTable(OrgSum.class);
        Map<String, Long> sums = new HashMap<String, Long>();
        DocList list = docList(null, null);
        for( int i=0; i<list.getCount(); i++ ) {
            Document<?> d = list.get(i);
            String id = d.getId();
            long sum = d.sum();
            List<String> ka = orgs.get(id);
            if(ka != null) {
                for(String oi : ka) {
                    Long val = sums.get(oi);
                    if( val != null)
                        sum += val;
                    sums.put(oi, sum);
                }
            }
        }
        list.close();

        writeSumMap(sums);
    }
}
