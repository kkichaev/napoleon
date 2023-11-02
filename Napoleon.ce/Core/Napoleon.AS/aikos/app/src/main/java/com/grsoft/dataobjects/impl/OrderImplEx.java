package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.StoreData;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderImplEx extends OrderImpl{
    public Map<String, Integer> getItems(String id) {
        Map<String, Integer> ret = new HashMap<>();
        for(OrderItem oi : data.items) {
            ret.put(((OrderItemEx)oi).whCode, oi.qty);
        }
        return ret;
    }

    @Override
    public int getItemValue(Price item) {
        int q = item.qty;
        for(PriceQtyItem pqi : item.whQty) {
            q += pqi.qty;
        }
        return q;
    }

    public List<OrderItem> group() {
        List<OrderItem> res = new ArrayList<>();
        Map<String, OrderItem> omap = new HashMap<>();
        for(OrderItem oi : data.items) {
            OrderItem d = omap.get(oi.id);
            if(d == null) {
                OrderItem doi = new OrderItem();
                doi.id = oi.id;
                doi.qty = oi.qty;
                doi.cost = oi.cost;
                omap.put(oi.id, doi);
                res.add(doi);
            } else {
                d.qty += oi.qty;
            }
        }
        return res;
    }

    public void update(PriceImpl pi, long cost, Map<String, Integer> values, String remark, List<StoreData> stores) {
        PriceEx pe = (PriceEx) pi.getData();
        String id = pe.id;
        boolean updated = false;
        List<OrderItem> newItems = new ArrayList<>();
        for(Map.Entry<String, Integer> kv : values.entrySet()) {
            if(kv.getValue() != 0) {
                OrderItemEx oid = new OrderItemEx();
                oid.id = id;
                oid.remark = remark;
                oid.whCode = kv.getKey();
                oid.cost = (int)cost;
                oid.whCode = kv.getKey();
                oid.qty = kv.getValue();
                newItems.add(oid);
            }
        }

        List<OrderItem> upd = new ArrayList<>();
        for(OrderItem oi : data.items) {
            if(oi.id.equals(id)) {
                if(!updated) {
                    updated = true;
                    upd.addAll(newItems);
                }
                // put back qty
                String whc = ((OrderItemEx)oi).whCode;
                for(StoreData sd : stores) {
                    if(sd.key.equals(whc)) {
                        if(sd.idx == 0) {
                            pe.qty += oi.qty;
                        } else if(pe.whQty.size() >= sd.idx) {
                            pe.whQty.get(sd.idx -1).qty += oi.qty;
                        }
                    }
                }
                continue;
            }
            upd.add(oi);
        }

        if(!updated)
            upd.addAll(newItems);

        data.items.clear();
        data.items.addAll(upd);

        for(StoreData sd : stores) {
            String sid = sd.key.toString();
            Integer qty = values.get(sid);
            int idx = sd.idx;
            if(qty != null && qty > 0) {
                if(idx == 0) {
                    pe.qty -= qty;
                } else if(pe.whQty.size() >= idx){
                    pe.whQty.get(idx-1).qty -= qty;
                }
            }
        }
        pi.getData().updateWhState();
        pi.write();

        write();
        getDocumentType().refreshDocSum(data.id);
    }
}
