package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.ListView;

import com.grsoft.dataobjects.OrderEx;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail{
    ActionAdapter adapter =  new ActionAdapter(this);

    @Override
    protected void setContentView() {
        setContentView(R.layout.orderdeliveryex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lv = findViewById(R.id.lvActions);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh((OrderEx) doc.getData());
    }
}
