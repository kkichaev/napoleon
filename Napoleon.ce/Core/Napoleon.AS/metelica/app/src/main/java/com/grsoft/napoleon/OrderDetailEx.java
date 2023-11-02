package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;

public class OrderDetailEx extends  OrderDetail{
    protected void setContentView(){
        setContentView(R.layout.orderdetailex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void updateTotalSum() {
        super.updateTotalSum();

        int val = 0;

        if (doc instanceof OrderImplEx){
            for(OrderItem oi : doc.getData().items) {
                   val += FPOperation.itemMul(((OrderItemEx)oi).cost, oi.qty, Consts.QTY_SCALE) -
                           FPOperation.itemMul(((OrderItemEx)oi).mgrCost, oi.qty, Consts.QTY_SCALE);
            }
        }

        TextView tv = findViewById(R.id.tvMargin);
        tv.setText(getString(R.string.margin, Util.IntToScaleStr(val, Consts.SUM_SCALE)));
    }
}
