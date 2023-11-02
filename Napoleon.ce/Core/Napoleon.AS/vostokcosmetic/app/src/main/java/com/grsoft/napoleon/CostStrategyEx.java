package com.grsoft.napoleon;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderDiscount;
import com.grsoft.dataobjects.OrgPriceCost;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostStrategyEx extends CostStrategy {
    static String dogovor = "", orgid = "";
    static Map<Integer, Integer> folders;
    static Map<String, Integer> price;

    static void clear() {
        dogovor = "";
        orgid = "";
    }

    static void load(OrderEx o) {
        if(!dogovor.equals(o.dogovor) || !orgid.equals(o.id)) {
            dogovor = o.dogovor;
            orgid = o.id;

            OrgImpl oi = new OrgImpl();
            oi.read("id", orgid);

            OrgDogovorImpl odi = new OrgDogovorImpl();
            OrgDogovor od = odi.getData();
            od.id = dogovor;
            od.ido = ((OrgEx)oi.getData()).ido;
            odi.read();
            odi.close();;

            price = new HashMap<>();
            for(OrgPriceCost c : od.price) {
                price.put(c.id, c.cost);
            }

            folders = new HashMap<>();
            FolderTree ft = new FolderTree();
            ft.load();
            for(OrgFolderDiscount ofd : od.fldDsc) {
                List<Folder> flist = ft.getWithDescendats(ofd.fid);
                for(Folder f : flist) {
                    folders.put(f.id, ofd.discount);
                }
            }
        }
    }

    public int discount(Price p, OrderEx oe) {
        load(oe);
        Integer f = folders.get(p.folderID);
        return f == null ? 0 : f;
    }

    public int getPriceCost(Price p, Document<?> doc) {
        return super.getCostInt(p, doc, doc.getSumType());
    }

    @Override
    public int getCostInt(Price p, Document<?> doc, int sumType) {
        if(doc instanceof OrderImplEx) {
            OrderEx oe = (OrderEx)doc.getData();
            load(oe);
            Integer c = price.get(p.id);
            if(c != null)
                return c;

            int cost = super.getCostInt(p, doc, sumType);
            Integer f = folders.get(p.folderID);
            return f == null ? cost : costWithDiscount(cost, f, Consts.SUM_SCALE);
        }
        return super.getCostInt(p, doc, sumType);
    }
}
