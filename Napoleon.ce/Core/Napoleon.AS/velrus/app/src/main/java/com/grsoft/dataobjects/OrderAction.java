package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TableInfo(name="OrderAction", keyFields="id")
@ServerInfo(name = "OrderAction")
public class OrderAction extends  DataObject {
    public static final int SET_GITFT_DISCOUNT = 0;
    public static final int SUM_GIFT = 1;

    public String id;
    public Date start;
    public Date finish;
    public String name = "";
    public String descr = "";

    public int kind = SET_GITFT_DISCOUNT;
    public String org = "";
    public String cluster = "";

    @Scale(value = Consts.SUM_SCALE)
    public int sum = 0;

    @Scale(value = Consts.SUM_SCALE)
    public int discount = 0;

    public int gift = 0;
    public int applyManyTimes = 0;

    public List<OrderActionItem> items = new ArrayList<>();
    public List<OrderActionItem> gifts = new ArrayList<>();

//    public int canApplyCount(Actionable doc);
//    public boolean apply(Actionable doc);
//    public boolean revert(Actionable doc);

    public boolean canApply(OrgEx oe) {
        return  (cluster.length() == 0 && org.length() == 0) || org.equals(oe.id) || cluster.equals(oe.cluster);
    }

    public boolean applyToAll() { return items.size() == 0;}

    interface  ActionHandler {
        void activeAction(OrderAction a, OrderActionItem i);
    }

    public boolean canApply(Actionable doc) {
        if(kind == SUM_GIFT)
        {
            return sum < doc.sum();
        }
        return true;
    }

    static void searchActions(String id, ActionHandler handler) {
        OrgImpl oi = new OrgImpl();
        oi.read("id", id);
        OrgEx oe = (OrgEx) oi.getData();

        String where = String.format("\"start\"<=%1$d and \"finish\">=%1$d", Util.getDate().getTime());
        for(OrderAction oa : DbReader.fetch(OrderAction.class, where)) {
            if(oa.canApply(oe)) {
                for(OrderActionItem oai : oa.items)
                    handler.activeAction(oa, oai);
            }

        }
    }

    public static Set<String> orgActionItems(String id) {
        Set<String> ret = new HashSet<>();
        searchActions(id, (oa, oai) -> ret.add(oai.id));
        return ret;
    }

    public static List<OrderAction> getActions(PriceEx p, String orgId) {
        List<OrderAction> ret = new ArrayList<>();

        searchActions(orgId,(oa, oai) -> {
            if(oai.id.equals(p.id))
                ret.add((oa));
        });

        return ret;
    }

    void applyGift(Actionable doc, int quant) {
        if(quant == 0) return;

        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        List<ActionBonusItem> bonus = new ArrayList<>();
        for(OrderActionItem oai : gifts) {
            p.id = oai.id;
            if(pi.read()) {
                int canAdd = oai.qty * quant;
                int stock = doc.getItemValue(p);
                int add = Math.min(stock, canAdd);
                if(add > 0) {
                    ActionBonusItem abi = new ActionBonusItem();
                    abi.id = oai.id;
                    abi.qty = add;
                    bonus.add(abi);
                }
            }
        }
        if(!bonus.isEmpty()) {
            doc.add(this, bonus);
        }
        pi.close();
    }

    public void apply(Actionable doc, PriceEx srcItem) {
        if(kind == SUM_GIFT) {
            int quant = (int)(doc.sum() / sum);
            if(applyManyTimes == 0 && quant > 0) quant = 1;
            applyGift(doc, quant);
        } else {
            PriceImpl pi = new PriceImpl();
            Price p = pi.getData();

            int checkRate = 0, maxRate = 0;
            for(OrderActionItem oai : items) {
                p.id = oai.id;
                pi.read();
                int avail = doc.getItemValue(p) + doc.getItemQty(p);
                int cr = avail / oai.qty;
                if(cr == 0) {
                    maxRate = 0;
                    break;
                }
                if(maxRate == 0 || maxRate > cr) maxRate = cr;
                if(oai.id.equals(srcItem.id)) {
                    checkRate = (applyManyTimes == 0 || gift == 0) ? 1 : cr;
                }
            }

            maxRate = Math.min(maxRate, checkRate);
            if(maxRate > 0) {
                checkAndAdd(doc, maxRate, srcItem.id, (gift == 0) ? discount : 0);
                if(gift != 0) {
                    applyGift(doc, maxRate);
                }
            }
        }
    }

    private void checkAndAdd(Actionable doc, int coef, String srcId, int discount) {
        OrderImplBase<?> dd = (OrderImplBase<?>) doc;
        CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) dd.getClass());

        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        for(OrderActionItem oai : items) {
            p.id = oai.id;
            pi.read();

            int need = oai.qty * coef;
            int costwod = (int)cs.getItemCost(p, dd);
            doc.setItem(id, pi, need, costwod, discount);
        }
        pi.close();
    }
}
