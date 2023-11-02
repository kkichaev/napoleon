package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailEx extends OrderDetail {

    Adapter adapter;

    boolean inDiscount = false;
    List<OrderItem> discItems = new ArrayList<>();

    @Override protected void setContentView() { setContentView(R.layout.orderdetailex); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = findViewById(R.id.btnDiscount);
        v.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) { makeDiscount(false); }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inDiscount = true;
                makeDiscount(true);
                return false;
            }
        });
    }

    void makeDiscount(boolean checkAll) {
        inDiscount = !inDiscount;
        ((ImageButton)findViewById(R.id.btnDiscount)).setImageResource( inDiscount ? R.drawable.accept_discount : R.drawable.make_discount);

        if(!inDiscount) {
            dscHelper.checkItems(checkAll);
            DiscountInputDlg.open(this, new InputNumber() {
                @Override
                public void applayInput(int value, Object... params) {

                    boolean allItems = dscHelper.isCheckAll();

                    PriceImpl pi = new PriceImpl();
                    Price p = pi.getData();

                    CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
                    Features.CAN_CHANGE_COST = false;

                    List<OrderItem> items = allItems ? doc.getData().items : discItems;
                    for(OrderItem oie : items) {
                        OrderItemEx oe = (OrderItemEx) oie;
                        if(oe.costNoDsc == 0) {
                            p.id = oe.id;
                            pi.read();

                            oe.costNoDsc = cs.getPriceCost(p, doc.getSumType(), doc);
                        }
                        oe.cost = (int)CostStrategy.costWithDiscount(oe.costNoDsc, -value, Consts.SUM_SCALE);
                        oe.discount = -value;
                    }

                    pi.close();
                    Features.CAN_CHANGE_COST = true;
                    discItems.clear();
                    doc.write();
                    adapter.notifyDataSetChanged();
                }

                @Override public long getValue() { return 0; }
            }, Consts.SUM_SCALE, true, "¬ведите скидку, %", DiscountInputDlg.Type.OnlyDiscount, dscHelper);
        }
    }

    class DscHelper extends DiscountInputDlg.Helper {
        View view;
        boolean checkAll = false;
        public void checkItems(boolean check) { checkAll = check; }

        @Override public int getLayoutId() { return R.layout.discount_input_ex; }

        @Override
        public void adjustView(View view) {
            this.view = view;
            ((CheckBox)view.findViewById(R.id.cbAllItems)).setChecked(checkAll);
        }

        public boolean isCheckAll() { return ((CheckBox)view.findViewById(R.id.cbAllItems)).isChecked(); }
    }

    DscHelper dscHelper = new DscHelper();

    @Override
    protected void setAdapter() {
        adapter = new Adapter();
        lvItems.setAdapter(adapter);
    }

    @Override
    protected void editItem(OrderItem orderItem) {
        if(inDiscount) {
            if(discItems.contains(orderItem)) {
                discItems.remove(orderItem);
            } else {
                discItems.add((OrderItemEx) orderItem);
                if(discItems.size() == doc.getData().items.size()) {
                    makeDiscount(true);
                }
            }
            adapter.notifyDataSetChanged();
        } else
            super.editItem(orderItem);
    }

    class Adapter extends OrderItemsAdapter {
        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
            super.drawInternal(view, name, color, item, pos);
            view.setBackgroundResource( discItems.contains(item) ? R.drawable.even_row_selector : R.drawable.list_selector);
        }

        @Override
        protected void drawSum(TextView tvSum, OrderItem item, int color) {
            long sum = getItemSum(item);
            String text = Util.IntToScaleWStr(sum, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false);
            if(((OrderItemEx)item).discount != 0) {
                text += "<br/><i>-" + Util.IntToScaleWStr(((OrderItemEx)item).discount, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, true) + "%</i>";
            }
            tvSum.setText(Html.fromHtml(text));
            tvSum.setGravity(Gravity.RIGHT);
            tvSum.setTextColor(color);
        }
    }
}
