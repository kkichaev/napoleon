package com.novotek.sales.main_views;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.BrandsAdapter;
import com.novotek.utils.FindGoodsController;

import java.util.ArrayList;

public class Brands extends BaseView {
    static String TAG = Brands.class.toString();
    private RecyclerView rv;
    FindGoodsController fgc;

    @Override
    protected int getResourceId() {
        return R.layout.brands_view;
    }

    @Override public String getFragmentTag() {  return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.catalog).setOnClickListener((x)->{
            ((MainActivity)getActivity()).resetStoredData();
            ((MainActivity)getActivity()).loadAnimFragment(new Catalog(), false, false);
        });

        rv = v.findViewById(R.id.items);
        model.getPartner().observe(getViewLifecycleOwner(), partner -> {
            rv.setAdapter(new BrandsAdapter(partner.brands(-1),
                    (MainActivity) getActivity(), images, R.layout.brand_view_tile));
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
        });

        ArrayList<String> products = model.getPartner().getValue().getPrice().allProducts();

        fgc = new FindGoodsController(v, (MainActivity) getActivity(), text -> {
            ((MainActivity)getActivity()).openProductsSearch(text, products, null, true);
            return true;
        });

//        v.findViewById(R.id.title).setOnClickListener(view -> getParentFragmentManager().popBackStack());
        return v;
    }

    public static Parcelable state;

    @Override
    public void onPause() {
        super.onPause();
        state = rv.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (state != null ){
            rv.getLayoutManager().onRestoreInstanceState(state);
        }
    }
}
