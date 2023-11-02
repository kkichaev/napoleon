package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.Price;

public class PriceCountEx extends PriceCount {
    @Override
    protected void refreshData() {
        super.refreshData();
        findViewById(R.id.tvPackName).setVisibility(View.GONE);
        Price p = price.getData();
        cbPackets.setText(p.packName);
    }
}
