package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
    @Override
    protected int getContentViewId() {
        return R.layout.pricecountex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(document instanceof OrderImpl) {
            ((OrderImpl)document).setUpdateQtyHandler(this);
        }
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(document instanceof OrderImpl) {
            String remark = "";
            OrderItemEx oie = (OrderItemEx) ((OrderImpl) document).findItem(price.getData().id);
            if (oie != null) {
                remark = oie.remark;
            }
            ((EditText) findViewById(R.id.edRemark)).setText(remark);
        } else {
            findViewById(R.id.edRemark).setVisibility(View.GONE);
        }
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        ((OrderItemEx)item).remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
    }
}
