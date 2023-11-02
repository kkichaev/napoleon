package com.grsoft.napoleon.documents;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebtDocEx extends com.grsoft.napoleon.modules.print.DebtDoc{
    static public void init() {
        instance = new DebtDocEx();
    }

    @Override
    public DocList docList(String orgId, String order, String where) {
        String wstr = "";
        if(orgId != null) {
            OrgImpl oi = new OrgImpl();
            OrgEx o = (OrgEx)oi.getData();
            o.id = orgId;
            oi.read();
            oi.close();

            wstr = "(id='" + o.ido + "')";
        }

        if( where != null && where.length() > 0 ) {
            if(wstr.length() > 0) {
                wstr += " AND ";
            }
            wstr += where;
        }
        return super.createDebtDocList(wstr, order, true);
    }

    @Override
    protected void writeSumMap(Map<String, Long> sums, boolean clearSums) {
        OrgSum os = new OrgSum();

        if(clearSums)
            DataBaseManager.getDataBase().execSQL(String.format("DELETE FROM '%s' WHERE type='%s'",
                    DataObjectInfo.getInstance().getTableName(os.getClass()), name));

        Map<String, List<String>> orgs = new HashMap<>();
        for(OrgEx o : DbReader.fetch(OrgEx.class)) {
            List<String> ids = orgs.get(o.ido);
            if(ids == null) {
                ids = new ArrayList<>();
                orgs.put(o.ido, ids);
            }
            ids.add(o.id);
        }
        DbWriter w = new DbWriter();
        os.type = this.name;
        for( Map.Entry<String, Long> v : sums.entrySet() ) {
            long sum = v.getValue();
            List<String> ids = orgs.get(v.getKey());
            if(ids == null) continue;
            for(String id : ids) {
                os.id = id;
                os.sum = sum;
                w.insertRecord(os);
            }
        }
        w.close();
    }
}
