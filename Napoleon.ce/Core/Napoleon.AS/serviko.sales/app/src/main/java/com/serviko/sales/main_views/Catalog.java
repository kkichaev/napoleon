package com.serviko.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.priceTree.Folder;
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;
import com.serviko.sales.main_views.price_filter.PriceFilter;
import com.serviko.sales.main_views.price_filter.PriceOrdering;

import java.util.List;

public class Catalog extends BaseView {

    public static String TAG = Catalog.class.toString();

    protected ListView lvItems;
    protected TextView tvName;
    protected Adapter adapter;

    @Override
    int getResourceId() {
        return R.layout.catalog_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        lvItems = v.findViewById(R.id.lvItems);
        lvItems.setDividerHeight(0);

        tvName = v.findViewById(R.id.tvName);

        model.getPartner().observe(getViewLifecycleOwner(), partner -> initFolderList(partner));

        return v;
    }

    protected void initFolderList(Partner partner) {
        String text = partner.name + "\n" + partner.address;
        tvName.setText(text);
        model.currentFolder = partner.getPrice().root();
        adapter = new Adapter(model.currentFolder);
        lvItems.setAdapter(adapter);
    }

    class Adapter extends BaseAdapter {

        List<Folder> folders;
        public Adapter(Folder f) {
            folders = f.childs;
        }

        @Override
        public int getCount() { return folders.size(); }

        @Override
        public Object getItem(int i) { return folders.get(i); }

        @Override
        public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.catalog_row, null);
            }

            Folder f = (Folder) getItem(i);
            Button b = view.findViewById(R.id.btnName);
            b.setText(f.item.name);
            b.setOnClickListener(v -> {
                model.currentFolder = f;

                BaseView cf = f.items.size() > 0 ? new Price() : new CatalogInt();
                ((MainActivity)getActivity()).loadFragment(cf, true);
            });

            return view;
        }
    }
}
