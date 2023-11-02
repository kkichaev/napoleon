package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.Brand;
import com.novotek.dataobjects.NameObj;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.ProjectData;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.BrandsAdapter;
import com.novotek.utils.ImageGetController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrandsMain extends Fragment {
    static final int MAX_BRANDS = 9;
    Model model;
    RecyclerView rv;

    ImageGetController images = new ImageGetController();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.brands_main, container, false);

        rv = v.findViewById(R.id.items);

        model = new ViewModelProvider(getActivity()).get(Model.class);
        model.getPartner().observe(getViewLifecycleOwner(), this::onNewPartner);

        model.getPicEvent().observe(getViewLifecycleOwner(), ctr -> images.update());

        v.findViewById(R.id.show_brands).setOnClickListener(view -> ((MainActivity)getActivity()).openBrands());

        return v;
    }

    void onNewPartner(Partner p) {
        rv.setAdapter(new BrandsAdapter(p.brands(MAX_BRANDS), (MainActivity) getActivity(), images, R.layout.brand_main_tile));
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
    }
}
