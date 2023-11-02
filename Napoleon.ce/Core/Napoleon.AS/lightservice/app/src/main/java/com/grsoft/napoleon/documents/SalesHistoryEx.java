package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SalesHistoryEx extends SalesHistory {

    Map<Long, Integer> cost = new HashMap<>();

    @Override
    protected void putItem(Document<?> doc, OrderItem item, int weight) {
        super.putItem(doc, item, weight);
        cost.put(doc.getDate().getTime(), item.cost);
    }

    @Override
    protected void putItem(Document<?> doc, DeliveryItem item, int weight) {
        super.putItem(doc, item, weight);
        cost.put(doc.getDate().getTime(), item.qty == 0 ? 0 : ((int)((long)item.sum  * Consts.QTY_SCALE)/ item.qty));
    }

    @Override
    protected String[] getHistoryInt(String orgId, String priceId, boolean fromOrders) {
        String items[] = null;

        try {
            SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM", Locale.getDefault());

            create(orgId, priceId, fromOrders) ;

            ArrayList<Entry<Long, Integer>> saleHistory = new ArrayList<Entry<Long,Integer>>();
            saleHistory.addAll(entrySet());

            Collections.sort(saleHistory, new CmpHistory());

            int ctr = 0;
            items = new String[saleHistory.size() * 3];
            for (Entry<Long, Integer> entry: saleHistory) {
                Integer val = cost.get(entry.getKey());
                items[ctr++] = simpleDateFormat.format(new Date(entry.getKey()));
                items[ctr++] = Util.IntToScaleStr(entry.getValue(), Consts.QTY_SCALE);
                items[ctr++] = Util.IntToScaleStr(val == null ? 0: val, Consts.SUM_SCALE);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}