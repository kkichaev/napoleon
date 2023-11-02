package com.serviko.sales.main_views.price_filter;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.serviko.sales.R;
import com.serviko.sales.main_views.ChildFilterFragment;
import com.serviko.sales.main_views.Filter;
import com.serviko.sales.main_views.Price;

public class SortingView extends ChildFilterFragment {
    @Override
    protected int getResourceId() { return R.layout.price_sorting; }

    @Override
    protected Filter getFilter() { return model.priceSort; }

    @Override
    protected Pair<Integer, String>[] bindings() {
        return new Pair[] {
                new Pair<Integer, String>(R.id.trAscending, PriceOrdering.SORT_ASC_STR),
                new Pair<Integer, String>(R.id.trDescending, PriceOrdering.SORT_DESC_STR),
                new Pair<Integer, String>(R.id.trDiscount, PriceOrdering.SORT_DISCOUNT_STR),
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater ti = TransitionInflater.from(requireContext());
        setEnterTransition(ti.inflateTransition(R.transition.slide_from_top));
        setExitTransition(ti.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    protected void onClicked(ViewGroup bv, ImageView ico, String filedName) {
        getFilter().setValue(filedName, true);
        ((Price)getParentFragment()).closeOrdering(true);
    }
}
