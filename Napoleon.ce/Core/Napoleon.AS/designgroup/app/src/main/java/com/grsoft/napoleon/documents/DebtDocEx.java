package com.grsoft.napoleon.documents;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;

import java.util.Map;

public class DebtDocEx extends DebtDoc {
    public static void init() {
        instance = new DebtDocEx();
    }

    @Override
    protected String getOrgWhere(String orgId) {
        if(orgId == null) return "";

        OrgImpl org = new OrgImpl();
        org.read("id", orgId);

        StringBuilder result = new StringBuilder();
        result.append("id='").append(((OrgEx)org.getData()).ido).append("'");
        return result.toString();
    }

    @Override
    protected void writeSumMap(Map<String, Long> sums, boolean clearSums) {
        final OrgSum os = new OrgSum();

        if(clearSums)
            DataBaseManager.getDataBase().execSQL(String.format("DELETE FROM '%s' WHERE type='%s'",
                    DataObjectInfo.getInstance().getTableName(os.getClass()), name));

        final DbWriter w = new DbWriter();
        os.type = this.name;
        for( final Map.Entry<String, Long> v : sums.entrySet() ) {

            DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {
                @Override
                public boolean travel(DataTraveler<Org> item) {
                    os.id = item.data.id;
                    os.sum = v.getValue();
                    w.insertRecord(os);
                    return true;
                }
            }, "ido='" + v.getKey() + "'");
        }
        w.close();
    }
}
