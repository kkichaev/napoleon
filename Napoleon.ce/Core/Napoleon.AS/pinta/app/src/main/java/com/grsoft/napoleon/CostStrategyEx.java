package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.GoodsDiscount;
import com.grsoft.dataobjects.GoodsDiscountItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

import java.util.ArrayList;
import java.util.List;

public class CostStrategyEx extends CostStrategy {
    static GoodsDiscount dsc = null;

    public static void clear() {
        dsc = null;
    }

    public static void load(Order doc) {
        if(dsc == null || !dsc.id.equals(doc.id) || !dsc.org.equals(doc.firmCode)) {
            dsc = new GoodsDiscount();
            DbReader r = new DbReader();
            String docDate = Long.toString(doc.date.getTime());
            String where = "id='" + doc.id + "' and org='" + doc.firmCode + "' and end > " + docDate + " and start < " + docDate;
            r.select(dsc, dsc.getTableName(), where);
            r.close();
        }
    }

    public static List<String> getActionItems(Order doc) {
        List<String> ret = new ArrayList<>();

        load(doc);
        for(GoodsDiscountItem gdi : dsc.items) {
            ret.add(gdi.id);
        }

        return ret;
    }

    @Override
    public int getItemCost(Price p, Document<?> doc) {
        if(doc instanceof OrderImpl) {
            load((Order) doc.getData());
            int cost = dsc.getCost(p.id);
            if(cost != 0)
                return cost;
        }
        return super.getItemCost(p, doc);
    }
}
