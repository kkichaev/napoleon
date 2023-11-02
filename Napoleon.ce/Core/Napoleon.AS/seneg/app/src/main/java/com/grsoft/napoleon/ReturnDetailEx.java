package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class ReturnDetailEx extends ReturnDetail {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
    }

    @Override
    protected String getOrgText(Org o) {
        return ((OrgEx)o).fullName();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.inventdetail);
    }

    @Override
    protected void updateTotalSum() {
        updateTotalSum(0, 0, doc.count());
    }

    @Override
    protected void setAdapter() {
        lvItems.setAdapter(new Adapter());
    }

    class Adapter extends OrderItemsAdapter {
        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item) {
            super.drawInternal(view, name, color, item);
            view.findViewById(R.id.tvSum).setVisibility(View.GONE);
        }
    }
}
