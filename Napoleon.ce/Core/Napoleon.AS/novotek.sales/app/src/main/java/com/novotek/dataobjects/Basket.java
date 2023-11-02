package com.novotek.dataobjects;

import com.novotek.dataobjects.priceTree.PriceTree;
import com.novotek.sales.main_views.DeliveryDateSelect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Basket {
    public interface Handler {
        void Changed(Basket basket);
    }

    Handler handler;
    public List<BasketItem> items = new ArrayList<>();
    boolean canRemove;

    public String remark;
    public Date dlvDate;
    public String uid = UUID.randomUUID().toString();

    public boolean assignDlvDate = false;

    public Basket() {
        Calendar c = Calendar.getInstance();
        Date d = new Date((new Date()).getTime() + 24 * 3600 * 1000);
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, DeliveryDateSelect.getAvailHour(d));
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        dlvDate = c.getTime();
    }

    public void setHandler(Handler h) { handler = h; }

    public int size() { return items.size(); }

    public void setCanRemove(boolean canRemove) { this.canRemove = canRemove; }

    public void removeEmpty() {
        List<BasketItem> ri = new ArrayList<>();
        for(BasketItem bi : items) {
            if(bi.qty == 0) {
                ri.add(bi);
            }
        }

        if(ri.size() > 0) {
            items.removeAll(ri);
            if(handler != null)
                handler.Changed(this);
        }
    }

    public void setFrom(Order order, PriceTree pt) {
        clear();
        for(OrderItem oi : order.items) {
            Price p = pt.get(oi.item_id);
            if(p != null) {
                BasketItem i = new BasketItem();
                i.item = p;
                i.cost = p.price;
                i.qty = (int)(oi.count + 0.005);
                items.add(i);
            }
        }
        if(handler != null)
            handler.Changed(this);
    }

    public int getQty(String id) {
        for(BasketItem i : items)
            if(i.item.id.equals(id))
                return i.qty;
        return 0;
    }

    public void commit() {
        List<BasketItem> remove = new ArrayList<>();

        for(BasketItem i : items)
            if(i.qty == 0)
                remove.add(i);
        if(remove.size() != 0) {
            items.removeAll(remove);
            if(handler != null)
                handler.Changed(this);
        }
    }

    public void clear() {
        assignDlvDate = false;
        items.clear();
        uid = UUID.randomUUID().toString();

        dlvDate = new Date((new Date()).getTime() + 24 * 3600 * 1000);
        remark = "";

        if(handler != null)
            handler.Changed(this);
    }

    public void changeQty(Price item, int newQty, boolean packMode) {
        BasketItem i = find(item);
        if(i == null) {
            if(newQty == 0) {
                return;
            }
            i = add(item);
        }
        if(i.qty != newQty || packMode != i.packMode) {
            if(newQty == 0) {
                if(canRemove)
                    items.remove(i);
                else {
                    i.qty = 0;
                    i.packMode = packMode;
                }
            } else {
                i.qty = newQty;
                i.packMode = packMode;
                i.cost = item.price;
            }
            if(handler != null)
                handler.Changed(this);
        }
    }

    public BasketItem find(Price item) {
        for(BasketItem i : items)
            if(i.item.id.equals(item.id))
                return i;

        return null;
    }

    BasketItem add(Price item) {
        BasketItem bi = new BasketItem();
        bi.item = item;
        bi.cost = 0;//item.cost;
        bi.discount = 0;//item.discount;
        bi.qty = 0;

        items.add(bi);
        return bi;
    }

    public float sum() {
        float sum = 0;
        for(BasketItem i : items)
            sum += i.sum();

        return sum;
    }

    public float weight() {
        float w = 0;
        for(BasketItem i : items) {
            w += i.item.weight * i.qty;
        }
        return w;
    }
}
