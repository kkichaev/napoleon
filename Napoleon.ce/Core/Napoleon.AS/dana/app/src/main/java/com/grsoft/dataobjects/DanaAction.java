package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerInfo(name="RcvActions")
@TableInfo(name="actions", keyFields = "id")
public class DanaAction extends DataObject {
    public static final int GIFT_TYPE = 0;
    public static final int DISCOUNT_TYPE = 1;

    public String id = "";
    public String name = "";
    public String descr = "";

    public Date start = new Date();
    public Date finish = new Date();

    public String orgId = "";
    public String clusterId = "";
    public String itemId = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;

    @Scale(value = Consts.QTY_SCALE)
    public int used = 0;

    public int type = GIFT_TYPE;

    @Scale(value = Consts.SUM_SCALE)
    public int discount = 0;

    public int hidden = 0;

    public List<DanaActionItem> items = new ArrayList<>();

    public static List<DanaAction> active(boolean checkQty, OrgEx o) {
        String now = Long.toString(Util.getDate().getTime());
        String filter = "finish >= " + now + " and start <= " + now + " and hidden = 0 and (clusterId='' or clusterId='" +
                o.cluster + "') and (orgId='' or orgId = '" + o.id + "')";
        if(checkQty)
            filter += " and (qty = 0 or used < qty)";

        return DbReader.fetch(DanaAction.class, filter);
    }

    public boolean canApply(OrderEx o, Price current, int inputQty) {
        if(qty != 0 && used >= qty) return false;

        Map<String, Integer> orderData = mapOrderItems(o);
        if(current != null)
            orderData.put(current.id, inputQty);

        boolean ret = true;
        for(DanaActionItem dai : items) {
            Integer q = orderData.get(dai.id);
            if(q == null || q < dai.qty) {
                ret = false;
                break;
            }
        }

        return ret;
    }

    Map<String, Integer> mapOrderItems(Order o) {
        Map<String, Integer> orderData = new HashMap<>();
        for(OrderItem oi : o.items) {
            if(((OrderItemEx)oi).action.length() == 0)
                orderData.put(oi.id, oi.qty);
        }

        return orderData;
    }

    List<String> discountItems() {
        List<String> src = new ArrayList<>();
        for(DanaActionItem dai : items) {
            src.add(dai.id);
        }
        return src;
    }

    public boolean undo(OrderEx oe) {
        boolean updated = oe.removeAction(this);
        if(updated) {
            if(type == GIFT_TYPE) {
                oe.removeDiscountItem(itemId, id);
            } else {

                List<String> src = discountItems();
                oe.revertChangeCost(src);
            }
        }
        return updated;
    }

    public boolean applyAction(OrderEx oe) {
        boolean updated = false;
        if(canApply(oe, null, 0)) {
            int actQty = Consts.QTY_SCALE;
            if(type == GIFT_TYPE) {
                int actionCoef = countCoef(oe) * Consts.QTY_SCALE;
                if(qty != 0) {
                    actQty = Math.min(qty - used, actionCoef);
                } else {
                    actQty = actionCoef;
                }
                oe.addActionItem(itemId, actQty, id);
            } else {
                List<String> src = discountItems();
                actQty = oe.makeDiscount(src, discount);
            }
            oe.addAction(this, actQty);

            updated = true;
        }
        return updated;
    }

    private int countCoef(Order order) {
        double coef = 0;
        Map<String, Integer> orderData = mapOrderItems(order);

        for(DanaActionItem dai : items) {
            Integer q = orderData.get(dai.id);
            if(q != null) {
                double tc = (double)q / dai.qty;
                if(tc > coef)
                    coef = tc;
            }
        }
        return (int)(coef + 0.01);
    }
}
