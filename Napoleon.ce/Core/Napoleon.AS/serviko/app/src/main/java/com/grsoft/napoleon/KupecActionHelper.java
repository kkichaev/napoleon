package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.KupecAction;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;

import java.util.ArrayList;
import java.util.List;

public class KupecActionHelper {
    public static List<String> getItemsByOrg(String id){
        List<String> res = new ArrayList<>();
        OrgImpl org = new OrgImpl();
        org.read("id", id);
        List<KupecAction> act = DbReader.fetch(KupecAction.class, String.format("regionID=%s", ((OrgEx)org.getData()).regionID));
        for(KupecAction k : act)
            res.add(k.id);

        return res;
    }
}
