package com.serviko.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.dataobjects.ws.GetKupecItem;
import com.serviko.sales.R;

public class KupecActionView extends BaseView {
    static final String TAG = KupecActionView.class.toString();

    @Override
    int getResourceId() {
        return R.layout.kupec_action_view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout lv = v.findViewById(R.id.items);

        model.getPartner().observe(getViewLifecycleOwner(), partner -> {
            lv.removeAllViews();

            for(GetKupecItem i : partner.kupecAction) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.kupec_row, null);
                TextView tv;

                tv = view.findViewById(R.id.name);
                tv.setText(i.name);

                tv = view.findViewById(R.id.cost_tt);
                tv.setText(String.format("%.02f",i.costDisc));

                tv = view.findViewById(R.id.cost_board);
                tv.setText(String.format("%.02f",i.cost));

                tv = view.findViewById(R.id.cost_margin);
                tv.setText(String.format("%d%%",(int)(i.margin + 0.5)));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lv.addView(view, lp);
            }
        });
        return v;
    }
}
