package com.grsoft.napoleon;

import com.grsoft.napoleon.chart.ChartActivity;

public class ChartActivityEx extends ChartActivity {
    @Override
    protected void addAdapterView() {
        adapter.addView(visitChartView);
        adapter.addView(akbChartView);
    }
}
