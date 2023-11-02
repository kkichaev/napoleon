package com.grsoft.napoleon.documents;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Balance;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.network.exception.RuntimeException;

import java.util.List;

public class DebtDocEx extends DebtDoc {
    public static void init() {
        instance = new DebtDocEx();
    }

    @Override
    protected DebtDocList createDebtDocList(String where, String order, boolean LoadDelivery) {
        return new DebtDocListEx(where, order, LoadDelivery);
    }

    @Override
    public void refreshDocSum() throws RuntimeException {
        OrgSumImpl si = new OrgSumImpl();
        OrgSum os = si.getData();

        List<Balance> docs = DbReader.fetch(Balance.class);
        for(Balance d : docs) {
            os.id = d.id;
            os.sum = d.sum;
            os.type = getName();

            si.write();
        }

        si.close();
    }
}
