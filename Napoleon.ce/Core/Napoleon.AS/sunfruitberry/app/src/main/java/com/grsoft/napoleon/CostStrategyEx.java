package com.grsoft.napoleon;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

import java.util.HashMap;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {

    static Map<String, Integer> folders = new HashMap<>();
    static Map<String, Integer> price = new HashMap<>();
    static String id = null;
    static FolderTree ft = null;

    public static void clear() {
        id = null;
        ft = null;
    }

    void load(String oid) {
        if(id == null || id.equals(oid) == false) {
            id = oid;

            OrgImpl oi = new OrgImpl();
            oi.read("id", oid);
            folders.clear();
            price.clear();

            for(OrgCost oc : ((OrgEx)oi.getData()).cost) {
                if(oc.type == OrgCost.TYPE_ITEM)
                    price.put(oc.id, oc.discount);
                else
                    folders.put(oc.id, oc.discount);
            }
        }
    }

    @Override
    public int getCostInt(Price p, Document<?> doc, int sumType) {
        int cost = super.getCostInt(p, doc, sumType);
        if(doc != null) {
            load(doc.getId());
            Integer i = price.get(p.id);
            if(i == null) {
                if(ft == null) {
                    ft = new FolderTree();
                    ft.load();
                }
                Folder f = ft.getFolder(p.folderID);
                while(f != null) {
                    i = folders.get(f.fid);
                    if(i != null)
                        break;
                    f = ft.getParent(f);
                }
            }
            if(i != null)
                cost = costWithDiscount(cost, i, Consts.SUM_SCALE);
        }
        return cost;
    }
}
