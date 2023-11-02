package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.novotek.dataobjects.Partner;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.sales.SelectPartner;
import com.novotek.utils.FindGoodsController;

import java.util.ArrayList;

public class Main extends BaseView {

    public static String TAG = Main.class.toString();
    View v;
    FindGoodsController fgc;

    @Override
    protected int getResourceId() {
        return R.layout.main_view;
    }

    @Override public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        fgc = new FindGoodsController(v, (MainActivity) getActivity(), text -> {
            ArrayList<String> src = model.getPartner().getValue().getPrice().allProducts();
            ((MainActivity)getActivity()).openProductsSearch(text, src, getString(R.string.all_products), false);
            return true;
        });

        v.findViewById(R.id.favorites).setOnClickListener(view -> {
            ((MainActivity)getActivity()).openFavorites();
        });

        TextView tv = v.findViewById(R.id.select_org);
        tv.setOnClickListener(view -> {
            SelectPartner.open(getActivity(), true);
        });

        model.partner.observe(getViewLifecycleOwner(), this::onNewPartner);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.frmActions, new Actions())
                .replace(R.id.frmCatalog, new CategoriesMain())
                .replace(R.id.frmBrands, new BrandsMain())
                .commit();


        return v;
    }

    void onNewPartner(Partner partner) {
        TextView tv = v.findViewById(R.id.select_org);
        tv.setText(partner.address);
    }
}
