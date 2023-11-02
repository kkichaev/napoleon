package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.novotek.dataobjects.ws.ErrResult;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

public class BasketError extends BaseView {
    public static String TAG = BasketError.class.toString();

    @Override
    protected int getResourceId() { return R.layout.basket_error; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ErrResult errres = model.getRequestError().getValue();
        model.clearRequestError();

        String error = errres == null ? "" : errres.message;
        ((TextView)v.findViewById(R.id.error_message)).setText(error);

        v.findViewById(R.id.back).setOnClickListener(view ->
                getParentFragmentManager().popBackStack(getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE));

        v.findViewById(R.id.btnRepeat).setOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });

        v.findViewById(R.id.btnSupport).setOnClickListener(view -> {
            ((MainActivity)getActivity()).openFeedback();
        });
        return v;
    }
}
