package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ActionBonusItem;
import com.grsoft.dataobjects.Actionable;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.SimpleItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SalesImplEx extends SalesImpl implements Actionable {

    public interface ActionHandler {
        void onActionCommit(boolean haveBonus);
    }

    ActionHandler handler = null;
    SalesImplEx bonus;

    public SalesImplEx getBonus() {
        return getBonus(false);
    }

    public void setActionHandler(ActionHandler h) {
        handler = h;
    }

    public SalesImplEx getBonus(boolean init) {
        if(bonus != null) return bonus;

        SalesEx se = (SalesEx) data;
        if(se.bonusDoc.compareTo(SalesEx.MIN_DATE) > 0) {
            bonus = new SalesImplEx();
            bonus.getData().created = se.bonusDoc;
            if(!bonus.read()) {
                bonus = null;
            }
        }
        if(bonus == null && init) {
            SalesEx src = (SalesEx) data;

            bonus = new SalesImplEx();
            bonus.initSilent(data.id, new GpsCoord(data.latitude, data.longitude, data.stltime));
            SalesEx s = (SalesEx) bonus.getData();
            s.blackBonus = src.blackBonus;
            s.bonus = 1;
            s.supplyercode = src.supplyercode;
            s.supplyer = src.supplyer;
            s.useTax = s.blackBonus > 0 ? 0 : data.useTax;
            bonus.write();

            ((SalesEx) data).bonusDoc = bonus.getData().created;
        }
        return bonus;
    }

    @Override
    public void close() {
        if(bonus != null) bonus.close();
        super.close();
    }

    @Override
    public CreatableDocument<Sales> copy() {
        if(((SalesEx)data).bonus > 0) return null;
        return super.copy();
    }

    @Override
    public boolean read() {
        boolean ret = super.read();
        bonus = null;
        return ret;
    }

    @Override
    public boolean read(long rowid, boolean useCache) {
        boolean ret = super.read(rowid, useCache);
        bonus = null;
        return ret;
    }

    @Override
    public boolean delete() {
        if(super.delete()) {
            getBonus();
            if(bonus != null)
                bonus.delete();
            return true;
        }
        return false;
    }

    @Override
    public boolean isEditable() {
        return (((SalesEx)data).canEdit() && super.isEditable());
    }

    @Override
    public void initDocNumber() {
        data.number = "";
    }

    @Override
    public int getItemValue(Price item) {
        int qty = super.getItemValue(item);

        if(((PriceEx)item).unitType == PriceEx.UNIT_PACK && item.qtyInPack != 0) {
            qty = (int)((long)qty * Consts.QTY_SCALE / item.qtyInPack);
        }
        return qty;

    }

    @Override
    public List<SimpleItem> actions() {
        return ((SalesEx)data).actions;
    }

    void addAction(String actionid) {
        SalesEx se = (SalesEx) data;
        boolean have =false;
        for(SimpleItem si : se.actions) {
            if(si.id.equals(actionid)) {
                have = true;
                break;
            }
        }
        if(!have) {
            SimpleItem si = new SimpleItem();
            si.id = actionid;
            se.actions.add(si);
        }
    }

    @Override
    public void removeActions(Set<String> actions) {
        List<SalesItemEx> rmv = new ArrayList<>();
        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        SalesEx se = (SalesEx) data;
        List<SimpleItem> rmvA = new ArrayList<>();
        for(SimpleItem si : se.actions) {
            if(actions.isEmpty() || actions.contains(si.id)) rmvA.add(si);
        }
        se.actions.removeAll(rmvA);

        for(OrderItem oi : se.items)
        {
            SalesItemEx oie = (SalesItemEx) oi;
            if(actions.isEmpty() || actions.contains(oie.action)) {
                p.id = oi.id;
                pi.read();
                if(se.bonus > 0)  {
                    rmv.add(oie);
                    p.vanQty += oie.qty;
                    pi.write();
                } else {
                    oie.action = "";
                    oie.cost = oie.costWOD;
                    oie.countTax(se, p.tax1);
                }
            }
        }
        se.items.removeAll(rmv);
        SalesImplEx b = getBonus();
        if(b != null)
            b.removeActions(actions);
    }

    @Override
    public void commit() {
        SalesImplEx b = getBonus();
        if(b != null && b.isEmpty()) {
            ((SalesEx)data).bonusDoc = SalesEx.MIN_DATE;
            bonus = null;
            b.delete();
            b = null;
        }
        if(b != null)
            b.write();
        write();
        if(handler != null) {
            handler.onActionCommit(b != null);
        }
    }

    @Override
    public void add(OrderAction action, List<ActionBonusItem> bonus) {
        SalesEx se = (SalesEx) getBonus(true).getData();
        addAction(action.id);

        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        for(ActionBonusItem abi : bonus) {
            p.id = abi.id;
            pi.read();

            SalesItemEx oie = new SalesItemEx();
            oie.cost = 100;
            oie.sum = 100;
            oie.qty = abi.qty;
            oie.id = abi.id;
            oie.action = action.id;
            oie.costWOD = 100;

            oie.countTax(se, p.tax1);
            se.items.add(oie);

            p.vanQty -= abi.qty;
            pi.write();
        }

        pi.close();
    }

    @Override
    public void setItem(String actId, PriceImpl pi, int qty, int cost, int discount) {
        addAction(actId);

        Price p = pi.getData();
        SalesItemEx oie = (SalesItemEx) findItem(p.id);
        if(oie == null) {
            oie = new SalesItemEx();
            data.items.add(oie);
        }
        if(oie.qty < qty) {
            int add = qty - oie.qty;
            oie.qty = qty;
            updatePrice(pi, -add);
        }
        oie.costWOD = cost;
        oie.action = actId;
        if(discount > 0) {
            oie.cost = (int) CostStrategy.costWithDiscount(cost, discount, Consts.SUM_SCALE);
        }
        oie.countTax(data, p.tax1);
    }
}
