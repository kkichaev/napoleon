package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.novotek.dataobjects.Partner;
import com.novotek.sales.R;

public class CompanyCard extends BaseView {

    static final String TAG = CompanyCard.class.toString();

    @Override
    protected int getResourceId() {
        return R.layout.company_card;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());

        Partner p = model.getPartner().getValue();

        TextView tv = v.findViewById(R.id.name);
        tv.setText(p.name);

        tv = v.findViewById(R.id.address);
        tv.setText(p.address);

        tv = v.findViewById(R.id.position);
        tv.setText(p.position);

        tv = v.findViewById(R.id.phone);
        tv.setText(p.phone);

        tv = v.findViewById(R.id.payment_type);
        tv.setText(p.payment);
        return v;
    }
}
