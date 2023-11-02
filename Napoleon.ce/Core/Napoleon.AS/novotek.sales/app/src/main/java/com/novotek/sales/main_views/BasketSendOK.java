package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

public class BasketSendOK extends BaseView {
    static final String TAG = BasketSendOK.class.toString();

    @Override
    protected int getResourceId() {
        return R.layout.basket_sent_ok;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.doButton).setOnClickListener(view -> {
            ((MainActivity)getActivity()).openItem(R.id.itCatalog);
        });

        v.findViewById(R.id.traceOrder).setOnClickListener(view -> {
            Toast.makeText(getContext(), R.string.not_impl, Toast.LENGTH_LONG).show();
        });
        return v;
    }
}
