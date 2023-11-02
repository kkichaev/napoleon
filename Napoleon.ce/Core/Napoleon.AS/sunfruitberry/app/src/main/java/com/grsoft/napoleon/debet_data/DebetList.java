package com.grsoft.napoleon.debet_data;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DebetList extends ArrayList<DogovorData> {

    private static final long serialVersionUID = 7746543028606665599L;

    public DogovorData get(String dogId) {
        for(DogovorData dd : this) {
            if(dd.id.equals(dogId))
                return dd;
        }
        return new DogovorData(new OrgBalance());
    }

    public void add(OrgBalance blncData) {
        DogovorData pd = new DogovorData(blncData);
        add(pd);
    }

    public void add(OrgBalanceData data, Date dueDate) {
        DogovorData pd = null;
        for(DogovorData pi : this) {
            if( pi.id.equals(data.idDog) ) {
                pd = pi;
                DocData dd = new DocData(data, dueDate);
                pd.add(dd);
                break;
            }
        }
    }

    public void load(String orgId) {
        final Date dueDate = Util.getDate();

        final String baseID = orgId.split("\t")[0];
        String where = "id='" + baseID + "'";

        clear();

        DataTraveler.travel(OrgBalance.class, new DataTraveler.Travel<OrgBalance>() {
            @Override
            public boolean travel(DataTraveler<OrgBalance> item) {
                add(item.data);
                return true;
            }
        }, where);

        where = "ido='" + baseID + "' or id in (select id from Org where ido='" + baseID + "')";
        DataTraveler.travel(OrgBalanceData.class, new DataTraveler.Travel<OrgBalanceData>() {
            @Override
            public boolean travel(DataTraveler<OrgBalanceData> item) {
                add(item.data, dueDate);
                return true;
            }
        }, where);

        sort();
    }

    public void sort() {
        Collections.sort(this);
        for(DogovorData pd : this) {
            pd.sort();
        }
    }
}
