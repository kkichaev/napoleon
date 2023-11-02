package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int wcost = ((PriceEx)price.getData()).wcost;

        if (wcost > 0){
            ((TextView)findViewById(R.id.tvWCost)).setText(Util.IntToScaleStr(wcost, Consts.SUM_SCALE));
        }else
            findViewById(R.id.trWCost).setVisibility(View.GONE);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.pricecountex;
    }
}
