package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.CheckBox;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;

public class CreateSalesEx extends CreateSales {
    @Override
    protected int getSalesLayoutId() { return R.layout.createsalesex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SalesEx se = (SalesEx) salesImpl.getData();

        ((CheckBox)findViewById(R.id.cbBlack)).setChecked(se.isBlack == 1);
    }

    @Override
    protected void postOkDone(Sales sales) {
        super.postOkDone(sales);

        boolean isblack = ((CheckBox)findViewById(R.id.cbBlack)).isChecked();
        ((SalesEx)sales).isBlack = isblack ? 1 : 0;
        sales.useTax = isblack ? 0 : 1;
    }
}
