package com.serviko.sales.main_views.price_filter;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;
import com.serviko.sales.main_views.Model;
import com.serviko.sales.main_views.Price;

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

        View v = inflater.inflate(R.layout.price_filter_dlg, container, false);
        v.findViewById(R.id.filter_reset).setOnClickListener(view -> {
            model.priceFilter = new PriceFilter();
            ((Price)getParentFragment()).filterPrice();
        });

        v.findViewById(R.id.filter_apply).setOnClickListener(view -> {
            ((Price)getParentFragment()).filterPrice();
        });

        v.findViewById(R.id.trManufactory).setOnClickListener(view -> {
            ((Price)getParentFragment()).filterSetFragment(new Manufacturer());
        });

        v.findViewById(R.id.trVolume).setOnClickListener(view -> {
            ((Price)getParentFragment()).filterSetFragment(new Volume());
        });

        ViewGroup trAct = v.findViewById(R.id.trAction);
        ImageView icoAct = findIcon(trAct);
        icoAct.setImageResource(model.priceFilter.actionGoods ? R.drawable.ic_select_on :
                R.drawable.ic_select_off);

        trAct.setOnClickListener(view -> {
            model.priceFilter.actionGoods = !model.priceFilter.actionGoods;
            icoAct.setImageResource(model.priceFilter.actionGoods ? R.drawable.ic_select_on :
                    R.drawable.ic_select_off);
        });
        return v;
    }

    protected ImageView findIcon(ViewGroup parent) {
        for(int i=0; i<parent.getChildCount(); i++ ) {
            View v = parent.getChildAt(i);
            if(v instanceof ImageView) {
                return (ImageView) v;
            }
        }

        return null;
    }
}
