package com.serviko.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.serviko.dataobjects.ws.ErrResult;
import com.serviko.sales.R;

public class BasketError extends BaseView {
    public static String TAG = BasketError.class.toString();

    @Override
    int getResourceId() { return R.layout.basket_error; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ErrResult errres = model.getRequestError().getValue();
        model.clearRequestError();

        String error = errres == null ? "" : errres.error;
        ((TextView)v.findViewById(R.id.error_message)).setText(error);

        v.findViewById(R.id.back).setOnClickListener(view ->
                getParentFragmentManager().popBackStack(getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE));

        v.findViewById(R.id.btnRepeat).setOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });
        return v;
    }
}
