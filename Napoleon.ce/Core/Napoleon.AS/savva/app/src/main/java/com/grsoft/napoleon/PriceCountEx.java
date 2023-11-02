package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.grsoft.dataobjects.PriceDescription;
import com.grsoft.dataobjects.impl.PriceDescriptionImpl;

public class PriceCountEx extends PriceCount{
    PriceDescriptionImpl pdi = new PriceDescriptionImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.ivInfo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showInfo();
            }
        });
    }

    @Override
    protected int getContentViewId() { return R.layout.pricecountex; }

    @Override
    protected void refreshData() {
        super.refreshData();

        View view = findViewById(R.id.ivInfo);
        PriceDescription pd = pdi.getData();
        pd.id = price.getData().id;

        if(pdi.read()) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

        pdi.close();
    }

    void showInfo() {
        View v = View.inflate(this, R.layout.price_info, null);
        TextView tv = v.findViewById(R.id.tvTitle);
        tv.setText(price.getData().name);

        tv = v.findViewById(R.id.tvInfo);
        tv.setText(Html.fromHtml(pdi.getData().description));

        final PopupWindow pw = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        pw.setTouchable(true);

        pw.showAtLocation(v, Gravity.CENTER, 0, 0);

        v.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) { pw.dismiss(); }
        });
    }


}
