package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail{

    protected void setContentView(){
        setContentView(R.layout.orderdetailex);
    }

    protected void setAdapter(){
        lvItems.setAdapter(new OrderItemsAdapter(){
            @Override
            int getResourceID() {
                return R.layout.orderdetail_list_rowex;
            }

            @Override
            protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
                super.drawInternal(view, name, color, item, pos);

                int cost = (int)CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass()).getItemCost(price.getData(), (Document<?>) doc);

                TextView tv = view.findViewById(R.id.tvCost);
                tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE));
            }
        });
    }
}
