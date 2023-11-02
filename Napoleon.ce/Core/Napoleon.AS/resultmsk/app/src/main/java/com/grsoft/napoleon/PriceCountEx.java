package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount{
    @Override
    protected int getContentViewId() {return R.layout.pricecountex;}

    @Override
    protected void refreshData() {
        super.refreshData();

        int rrc = ((PriceEx)price.getData()).costRRC;
        ((TextView)findViewById(R.id.tvCostRRC)).setText(Util.IntToScaleStr(rrc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
    }
}
