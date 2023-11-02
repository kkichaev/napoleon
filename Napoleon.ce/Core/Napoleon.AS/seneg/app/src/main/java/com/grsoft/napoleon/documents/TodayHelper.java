package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;

import java.util.ArrayList;
import java.util.List;

public class TodayHelper {
    public static void addRootOrgs(List<String> ids) {
        List<String> adds = new ArrayList<>();
        OrgImpl oi = new OrgImpl();
        OrgEx o = (OrgEx) oi.getData();
        for(String id : ids) {
           o.id = id;
           if(oi.read()) {
               if(!adds.contains(o.ido))
                   adds.add(o.ido);
           }
        }
        oi.close();
        ids.addAll(adds);
    }
}
