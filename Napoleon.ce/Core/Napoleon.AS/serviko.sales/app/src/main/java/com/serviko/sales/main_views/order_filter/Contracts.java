package com.serviko.sales.main_views.order_filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.dataobjects.Contract;
import com.serviko.sales.R;

import java.util.List;

public class Contracts extends OrderChildFilter {
    List<Contract> contracts;

    @Override
    protected int getResourceId() {
        return R.layout.order_filter_contracts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        OrderFilter of = model.orderFilter;

        TableLayout table = v.findViewById(R.id.table);
        contracts = model.getOrdersContracts();
        for(Contract c : contracts) {
            ViewGroup tr = (ViewGroup) View.inflate(getContext(), R.layout.order_filter_contract_row, null);
            TextView tv = tr.findViewById(R.id.tvName);
            tv.setText(c.name);

            ViewGroup.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            table.addView(tr, lp);

            ImageView iv = findIcon(tr);
            setImage(iv, of.contracts.contains(c.id));
            tr.setOnClickListener(view -> {
                if(of.contracts.contains(c.id)) {
                    of.contracts.remove(c.id);
                    setImage(iv, false);
                } else {
                    of.contracts.add(c.id);
                    setImage(iv, true);
                }
            });
        }
        return v;
    }
}
