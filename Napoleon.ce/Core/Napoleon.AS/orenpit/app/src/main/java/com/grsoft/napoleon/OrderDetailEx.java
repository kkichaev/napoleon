package com.grsoft.napoleon;

import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail{
    PriceImpl price = new PriceImpl();

    protected void setAdapter(){
        lvItems.setAdapter(new OrderItemsAdapter(){
            private String getSumText(OrderItem item){
                String res = Util.IntToScaleWStr(getItemSum(item), Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false);
                price.read("id", item.id);
                int wcost = ((PriceEx)price.getData()).wcost;

                if (wcost > 0){
                    res += "<br>";
                    res += Util.IntToScaleWStr(wcost, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false);
                }

                return res;
            }

            @Override
            protected void drawSum(TextView tvSum, OrderItem item, int color) {
                tvSum.setText(Html.fromHtml(getSumText(item)));
                tvSum.setGravity(Gravity.RIGHT);
                tvSum.setTextColor(color);
            }
        });
    }
}
