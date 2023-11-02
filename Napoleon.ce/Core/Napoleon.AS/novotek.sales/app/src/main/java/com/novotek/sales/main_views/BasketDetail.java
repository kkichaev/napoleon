package com.novotek.sales.main_views;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.novotek.dataobjects.Basket;
import com.novotek.dataobjects.Partner;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BasketDetail extends BaseView {

    static final String TAG = BasketDetail.class.toString();

    @Override
    protected int getResourceId() { return R.layout.basket_detail; }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        Basket b = model.getBasket();
        Partner p = model.getPartner().getValue();

        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());

        TextView tv = v.findViewById(R.id.address);
        tv.setText(p.address);

        RadioButton rb = v.findViewById(R.id.payment_type);
        rb.setText(p.payment);

        TextInputLayout til = v.findViewById(R.id.remark);
        til.getEditText().setText(b.remark);

        float sum = b.sum();
        Spanned ssum = Html.fromHtml(getString(R.string.order_sum, sum));
        tv = v.findViewById(R.id.sum);
        tv.setText(ssum);
        tv = v.findViewById(R.id.sum_total);
        tv.setText(ssum);

        tv = v.findViewById(R.id.weight);
        int weight = (int)(b.weight() / 1000 + 0.5);
        if(weight > 0) {
            tv.setText(getString(R.string.order_weight_kg, weight));
        } else {
            weight = (int)(b.weight() + 0.5);
            tv.setText(getString(R.string.order_weight_g, weight));
        }

        tv = v.findViewById(R.id.deliveryDate);
        tv.setText(getDeliveryString(b.dlvDate));
        tv.setOnClickListener(view -> ((MainActivity)getActivity()).selectDeliveryDate());

        model.getDeliveryDate().observe(getViewLifecycleOwner(), ddate -> {
            ((TextView)v.findViewById(R.id.deliveryDate)).setText(getDeliveryString(ddate));
        });

        v.findViewById(R.id.doButton).setOnClickListener(view -> {
            b.remark = til.getEditText().getText().toString();
            model.sendBasket(getContext());
        });

        model.getRequestInProgress().observe(getViewLifecycleOwner(), progress -> {
            v.findViewById(R.id.llWait).setVisibility(progress ? View.VISIBLE : View.GONE);
        });

        model.getRequestResult().observe(getViewLifecycleOwner(), res -> {
            if(res) {
                model.clearRequestResult();
                model.getBasket().clear();
                ((MainActivity)getActivity()).openBasketSent();
            }
        });

        model.getRequestError().observe(getViewLifecycleOwner(), err -> {
            if(err != null)
                ((MainActivity)getActivity()).openBasketError();
        });

        return v;
    }

    String getDeliveryString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return sdf.format(date);
//        return sdf.format(date) + String.format(", %02d:%02d - %02d:%02d",
//                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
//                (c.get(Calendar.HOUR_OF_DAY) + 1) % 24, c.get(Calendar.MINUTE)
//        );
    }
}
