package com.grsoft.napoleon.documents;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.Date;

public class DebetDocEx extends DebtDoc {
    public static void init() {
        instance = new DebetDocEx();
    }

    @Override
    public void setView(Adapter adapter, View view, Document<?> doc) {
        if(doc instanceof DeliveryImpl) {
            Delivery d = (Delivery) doc.getData();

            long overdue = d.sumD > 0 ? (new Date()).getTime() - d.payDate.getTime() : 0;

            int color = overdue > 0 ? Color.RED : Color.BLACK;
            String text = getDateDocText(doc);
            text += "<br/>" + Util.simpleDateFormat.format(d.payDate);

            updTextItem(view, R.id.tvDate, Html.fromHtml(text), color, null);

            updTextItem(view, R.id.tvSum, Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false), color, new ViewUpdater() {
                @Override public void update(View v) { v.setVisibility(View.VISIBLE); }});

            text = doc.getDescription(view.getContext());
            if(overdue > 0) {
                text += "<br/>" + Long.toString(overdue / (1000 * 3600*24));
            }

            updTextItem(view, R.id.tvOther, Html.fromHtml(text), color, null);
        } else {
            super.setView(adapter, view, doc);
        }
    }
}
