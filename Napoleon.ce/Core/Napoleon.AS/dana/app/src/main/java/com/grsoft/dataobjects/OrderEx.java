package com.grsoft.dataobjects;

import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderEx extends Order {
    public String base = "";
    public String project = "";
    public int sf = 0;

    public List<OrderAction> actions = new ArrayList<>();

    public void addActionItem(String itemId, int qty, String actionId) {
        OrderItemEx oie = null;
        for(OrderItem oi : items) {
            if(oi.id.equals(itemId) && ((OrderItemEx)oi).action.equals(actionId)) {
                oie = (OrderItemEx) oi;
                break;
            }
        }

        if(oie == null) {
            oie = new OrderItemEx();
            oie.id = itemId;
            oie.cost = Consts.SUM_SCALE;
            oie.qty = qty;
            oie.action = actionId;
            oie.costWOD = Consts.SUM_SCALE;;

            items.add(oie);
        } else {
            oie.qty = qty;
        }
    }

    public void removeDiscountItem(String itemid, String action) {
        for(OrderItem oi : items) {
            if(oi.id.equals(itemid) && ((OrderItemEx)oi).action.equals(action)) {
                items.remove(oi);
                break;
            }
        }
    }

    public void revertChangeCost(List<String> chItems) {
        for(OrderItem oi : items) {
            if(chItems.contains(oi.id)) {
                oi.cost = ((OrderItemEx)oi).costWOD;
            }
        }
    }

    public boolean removeAction(DanaAction action) {
        boolean updated = false;
        for(OrderAction oi : actions) {
            if(oi.id.equals(action.id)) {
                actions.remove(oi);
                updated = true;
                break;
            }
        }
        return updated;
    }

    public boolean addAction(DanaAction action, int qty) {
        boolean needUpdate = true;
        for(OrderAction oi : actions) {
            if(oi.id.equals(action.id)) {
                oi.qty = qty;
                needUpdate = false;
                break;
            }
        }
        if(needUpdate) {
            OrderAction oa = new OrderAction();
            oa.id = action.id;
            oa.item = action.itemId;
            String ids = "";
            for(DanaActionItem dai : action.items) {
                ids += dai.id + ",";
            }
            oa.items = ids.substring(0, ids.length() - 1);
            oa.qty = qty;
            actions.add(oa);
        }
        return needUpdate;
    }

    public int makeDiscount(List<String> src, int discount) {
        int qty = 0;

        for(OrderItem oi : items) {
            if(src.contains(oi.id) && ((OrderItemEx)oi).action.length() == 0) {
                oi.cost = CostStrategy.costWithDiscount(((OrderItemEx)oi).costWOD, discount, Consts.SUM_SCALE);
                qty += oi.qty;
            }
        }

        return qty;
    }

    public List<DanaAction> checkActions(List<String> checked, Map<Object, DanaAction> mactions) {
        List<DanaAction> ret = new ArrayList<>();
        for(OrderAction oa : actions) {
            if(checked != null && checked.contains(oa.id)) continue;
            DanaAction da = mactions.get(oa.id);
            if(da != null && !da.canApply(this, null, 0)) {
                ret.add(da);
            }
        }
        return ret;
    }
}
