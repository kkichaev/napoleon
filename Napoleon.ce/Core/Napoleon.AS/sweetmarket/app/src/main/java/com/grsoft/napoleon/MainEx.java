package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class MainEx extends Main {

    @Override
    protected int getResourceID() {
        return R.layout.mainex;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        findViewById(R.id.main_info).setOnLongClickListener(createTotalSumLongClick());
    }

    @Override
    public void updateTotalSum(long sum, int weight) {
        updateTotalSum(sum, weight, 0);
    }

    @Override
    public void updateTotalSum(long sum, int weight, int count) {
        AgentInfo ai = AgentInfo.get();
        TextView tv = findViewById(R.id.main_balance);
        tv.setText(Util.IntToScaleStr(ai.debet, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        tv = findViewById(R.id.main_overdue);
        tv.setText(Util.IntToScaleStr(ai.overdue, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        tv = findViewById(R.id.main_turnover);
        tv.setText(Util.IntToScaleStr(ai.income, Consts.SUM_SCALE, Util.DEC_DELIM, false));

        tv = findViewById(R.id.main_sum);
        tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
    }
}
