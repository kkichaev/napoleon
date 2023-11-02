package com.serviko.sales.main_views.order_filter;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;
import com.serviko.sales.main_views.Model;
import com.serviko.sales.main_views.Orders;

public class FilterView extends Fragment {

    Model model;
    boolean slideTop;

    public FilterView(boolean slideTop) { this.slideTop = slideTop; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater ti = TransitionInflater.from(requireContext());
        setEnterTransition(ti.inflateTransition(slideTop ? R.transition.slide_from_top : R.transition.slide_left));
        setExitTransition(ti.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        View v = inflater.inflate(R.layout.order_filter_dlg, container, false);
        v.findViewById(R.id.trState).setOnClickListener(view -> {
            ((Orders)getParentFragment()).filterSetFragment(new State());
        });
        v.findViewById(R.id.trDebt).setOnClickListener(view -> {
            ((Orders)getParentFragment()).filterSetFragment(new Debt());
        });
        v.findViewById(R.id.trContract).setOnClickListener(view -> {
            ((Orders)getParentFragment()).filterSetFragment(new Contracts());
        });
        v.findViewById(R.id.filter_reset).setOnClickListener(view -> {
            model.orderFilter = new OrderFilter();
            ((Orders)getParentFragment()).filterOrders();
        });

        v.findViewById(R.id.filter_apply).setOnClickListener(view ->{
            ((Orders)getParentFragment()).filterOrders();
        });
        return v;
    }
}
