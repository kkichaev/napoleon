package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.novotek.dataobjects.priceTree.FolderSrc;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.ImageGetController;

import java.util.List;

public class CategoriesMain extends Fragment {
    Model model;
    ImageGetController images = new ImageGetController();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.categories_main, container, false);
        model = new ViewModelProvider(getActivity()).get(Model.class);

        ListView lv = v.findViewById(R.id.lvItems);
        model.getPartner().observe(getViewLifecycleOwner(), partner -> {
            FoldersAdapter a = new FoldersAdapter(getContext(), partner.getPrice().folders, images);
            lv.setAdapter(a);
        });

        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            FolderSrc f = (FolderSrc) adapterView.getAdapter().getItem(i);
            ((MainActivity)getActivity()).openFolder(f, null);
        });

        model.getPicEvent().observe(getViewLifecycleOwner(), ctr -> images.update());
        return v;
    }
}
