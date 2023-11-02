package com.grsoft.napoleon;

import android.os.Bundle;

public class DeliveryDetailEx extends DeliveryDetail{
    @Override
    protected int getContentViewId() {
        return R.layout.deliverydetailex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnPayment).setOnClickListener(v ->
                DeliveryPayment.open(DeliveryDetailEx.this, delivery));
    }
}
