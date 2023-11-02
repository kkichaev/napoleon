package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgTara;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

import java.util.HashMap;
import java.util.Map;

public class OrderImplEx extends OrderImpl{

    Map<String, Integer> tara = null;

    @Override
    public int getItemValue(Price item) {
        if(((PriceEx)item).tara != 0) {
            if (tara == null) {
                PriceImpl pi = new PriceImpl();
                PriceEx pe = (PriceEx) pi.getData();

                Map<String, Integer> cqty = new HashMap<>();
                DocList dl = OrderDoc.instance().docList(data.id, "", "(params & " +
                        Integer.toString(ParamState.ofExported) + ") = 0 and created != " +
                        Long.toString(data.created.getTime()));

                for(Document<?> d : dl) {
                    for(OrderItem oi :((OrderImpl)d).getData().items) {
                        pe.id = oi.id;
                        if(pi.read() && pe.tara != 0) {
                            Integer cq = cqty.get(oi.id);
                            cqty.put(pe.id, (cq == null) ? oi.qty : cq + oi.qty);
                        }
                    }
                }
                pi.close();
                tara = new HashMap<>();

                OrgImpl oi = new OrgImpl();
                oi.read("id", data.id);
                for (OrgTara ot : ((OrgEx) oi.getData()).tara) {
                    Integer cq = cqty.get(ot.id);
                    tara.put(ot.id, ot.qty + (cq == null? 0 : cq));
                }
            }
            OrderItem oi = (OrderItem) findItem(item.id);

            Integer qty = tara.get(item.id);
            if (qty != null)
                return qty - ((oi == null) ? 0 : oi.qty);
            return ((oi == null) ? 0 : -oi.qty);
        }
        return super.getItemValue(item);
    }
}
