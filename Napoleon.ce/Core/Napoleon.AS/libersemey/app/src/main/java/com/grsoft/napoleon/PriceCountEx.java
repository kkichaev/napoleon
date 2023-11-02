package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
    int itemCost = 0;
    int maxDiscount = 0;
    int discount = 0;
    TextView tvDiscount;
    OrderImpl doc;

    @Override protected int getContentViewId() { return R.layout.pricecountex; }

    @Override
    protected void postOnCreate() {
        super.postOnCreate();

        tvDiscount = findViewById(R.id.tvDiscount);
        if(document instanceof OrderImpl) {
            doc = ((OrderImpl) document);
            doc.setUpdateQtyHandler(this);
            tvDiscount.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
                        @Override
                        public void applayInput(int value, Object... params) {
                            value = -value;
                            if(value <= maxDiscount) {
                                discount = value;
                                int newCost = (int)CostStrategy.costWithDiscount(itemCost, discount, Consts.SUM_SCALE);
                                onChangeCost(newCost);
                                updateDiscount();
                            } else {
                                Toast.makeText(PriceCountEx.this, "Выбранная скидка выше лимита", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override public long getValue() { return -discount; }
                    }, Consts.SUM_SCALE, false, "Введите скидку", DiscountInputDlg.Type.OnlyDiscount);
                }
            });
        }
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(doc != null) {
            View tr = findViewById(R.id.trDiscount);

            PriceEx pe = (PriceEx) price.getData();
            maxDiscount = pe.maxDiscount;
            if (maxDiscount == 0)
                tr.setVisibility(View.GONE);
            else
                tr.setVisibility(View.VISIBLE);
            itemCost = (int)priceVal;
            OrderItemEx oie = (OrderItemEx) doc.findItem(price.getData().id);
            if(oie != null) {
                itemCost = oie.costwd;
                discount = oie.discount;
                onChangeCost((int)oie.cost);
            }
            updateDiscount();
        }
    }

    void updateDiscount() {
        String text = "<u>"+ Util.IntToScaleStr(discount, Consts.SUM_SCALE) + "%</u>";
        tvDiscount.setText(Html.fromHtml(text));
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        OrderItemEx oie = (OrderItemEx) item;
        oie.costwd = itemCost;
        oie.discount = discount;
    }
}
