package com.grsoft.napoleon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DanaAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailEx extends OrderDetail {
    ActionAdapter adapter =  new ActionAdapter(this);

    @Override
    protected void setContentView() {
        setContentView(R.layout.orderdetailex);
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
