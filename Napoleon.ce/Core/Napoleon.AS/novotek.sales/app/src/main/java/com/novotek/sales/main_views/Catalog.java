package com.novotek.sales.main_views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.Price;
import com.novotek.dataobjects.priceTree.FolderBase;
import com.novotek.dataobjects.priceTree.FolderSrc;
import com.novotek.dataobjects.priceTree.FolderOld;
import com.novotek.dataobjects.priceTree.SubFolder;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.FindGoodsController;

import java.util.ArrayList;
import java.util.List;

public class Catalog extends BaseView {

    static String FOLDER_ARG = "folder";

    public static String TAG = Catalog.class.toString();

    public Catalog() {}

    public Catalog(FolderBase folder) {
        Bundle b = new Bundle();
        b.putString(Catalog.FOLDER_ARG, folder.name.name_en);
        setArguments(b);
    }

    protected ListView lvItems;
    FindGoodsController fgc;
    FolderSrc parentFolder = null;

    @Override
    protected int getResourceId() {
        return R.layout.catalog_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.brands).setOnClickListener((x)->((MainActivity)getActivity()).openBrands());

        lvItems = v.findViewById(R.id.lvItems);
        lvItems.setOnItemClickListener((adapterView, view, i, l) -> {
            FolderBase f = (FolderBase) adapterView.getAdapter().getItem(i);
            ((MainActivity)getActivity()).openFolder(f, parentFolder);
        });

        TextView tv = v.findViewById(R.id.title);
        Bundle b = getArguments();
        if(b != null) {
            String folder = b.getString(FOLDER_ARG, null);
            if (folder != null) {
                parentFolder = model.getPartner().getValue().getPrice().find(folder);
                if (parentFolder != null) {
                    final String folderName = parentFolder.name.toString();
                    tv.setText(folderName);
                    tv.setOnClickListener(view -> ((MainActivity)getActivity()).openCatalog());

                    FoldersAdapter adapter = new FoldersAdapter(getContext(), parentFolder.folders, images);
                    lvItems.setAdapter(adapter);

                    final ArrayList<String> products = new ArrayList<>();
                    for(SubFolder f : parentFolder.folders) {
                        for(Price p : f.items) {
                            products.add(p.id);
                        }
                    }

                    fgc = new FindGoodsController(v, (MainActivity) getActivity(), text -> {
                        ((MainActivity)getActivity()).openProductsSearch(text, products, folderName, false);
                        return true;
                    });
                    return v;
                }
            }

        }

        fgc = new FindGoodsController(v, (MainActivity) getActivity(), text -> {
            ((MainActivity)getActivity()).openProductsSearch(text,
                    model.getPartner().getValue().getPrice().allProducts(),
                    getString(R.string.all_products), false);
            return true;
        });


        tv.setCompoundDrawables(null, null, null, null);
        model.getPartner().observe(getViewLifecycleOwner(), partner -> initFolderList(partner));
        return v;
    }

    protected void initFolderList(Partner partner) {
        FoldersAdapter adapter = new FoldersAdapter(getContext(), partner.getPrice().folders, images);
        lvItems.setAdapter(adapter);
    }
}
