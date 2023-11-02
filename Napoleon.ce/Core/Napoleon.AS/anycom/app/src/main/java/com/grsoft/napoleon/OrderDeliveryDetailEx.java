package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.OrderFulfillment;
import com.grsoft.dataobjects.OrderFulfillmentItem;
import com.grsoft.dataobjects.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
    Map<String, String> remarks = new HashMap<>();

    @Override protected int getItemLayoitId() {return R.layout.order_delivery_row;}

    @Override
    protected void loadItems() {
        super.loadItems();
        String where = "created=" + Long.toString(doc.getData().created.getTime());
        for(OrderFulfillment oi : DbReader.fetch(OrderFulfillment.class, where)) {
            for(OrderFulfillmentItem ofi: oi.items) {
                remarks.put(ofi.id, ofi.remark);
            }
        }
    }

    @Override
    protected void drawItem(View view, DeliveryItem dlvItem, OrderItem ordItem, int color) {
        String text = remarks.get(dlvItem.id);
        TextView tv = view.findViewById(R.id.remark);
        tv.setText(text == null ? "" : text);
        tv.setTextColor(color);
    }
}
