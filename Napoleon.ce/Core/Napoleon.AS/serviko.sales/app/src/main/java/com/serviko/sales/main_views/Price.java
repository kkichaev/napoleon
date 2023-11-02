package com.serviko.sales.main_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serviko.dataobjects.priceTree.TreeElement;
import com.serviko.sales.R;
import com.serviko.sales.main_views.price_filter.FilterView;
import com.serviko.sales.main_views.price_filter.PriceFilter;
import com.serviko.sales.main_views.price_filter.PriceOrdering;
import com.serviko.sales.main_views.price_filter.SortingView;
import com.serviko.utils.PriceController;
import com.serviko.view.PriceViewLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Price extends BaseView implements ChildFilterFragment.Handler {

    static final int SMALL_WIDTH = 310;
    static final int LARGE_WIDTH = 780;

    public static String TAG = Price.class.toString();

    boolean rowMode = true;
    float dpiCoef;

    RecyclerView rv;
    Adapter adapter;

    Fragment ordering;
    Fragment filter;

    @Override
    int getResourceId() { return R.layout.price_view; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.back).setOnClickListener(view -> onBackPressed());

        rv = v.findViewById(R.id.lvItems);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        dpiCoef = 160 / dm.xdpi;

        TextView tvTitle = ((TextView)v.findViewById(R.id.tvTitle));
        tvTitle.setText(model.currentFolder.item.name);

        // clear filters on enter
        model.priceFilter = new PriceFilter();
        model.priceSort = new PriceOrdering();

        SearchView sv = v.findViewById(R.id.search);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                model.priceFilter.searchText = query.toUpperCase();
                adapter.refresh();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sv.setOnCloseListener(() -> {
            tvTitle.setVisibility(View.VISIBLE);
            if(model.priceFilter.searchText.length() > 0) {
                model.priceFilter.searchText = "";
                adapter.refresh();
            }
            return false;
        });

        sv.setOnSearchClickListener(view -> {
            if((int)(rv.getWidth() * dpiCoef) < LARGE_WIDTH)
                tvTitle.setVisibility(View.GONE);
        });

        ImageView iv = v.findViewById(R.id.layout);
        iv.setOnClickListener(view -> {
            rowMode = !rowMode;
            iv.setImageResource(rowMode ? R.drawable.ic_icon_tile : R.drawable.ic_icon_row);
            setLayoutManager();
        });

        v.findViewById(R.id.ordering).setOnClickListener(view -> {
            if(ordering == null) {
                closeFilter();

                ordering = new SortingView();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.filter_fragment, ordering)
                        .commit();
            } else {
                closeOrdering(false);
            }
        });
        v.findViewById(R.id.filter).setOnClickListener(view -> filtering(true));
        setLayoutManager();

        ((PriceViewLayout)v.findViewById(R.id.price_view_layout)).setHandler(() -> {
            closeFilter();
            closeOrdering(false);
        });
        return v;
    }

    @Override
    public void backing() {
        filtering(false);
    }

    public void closeFilter() {
        if(filter != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(filter)
                    .commit();
            filter = null;
        }
    }

    public void filtering(boolean closeIfPresents) {
        closeOrdering(false);

        if(!closeIfPresents || filter == null) {
            filter = new FilterView(closeIfPresents);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.filter_fragment, filter)
                    .commit();
        } else {
            filterPrice();
            closeFilter();
        }
    }

    public void filterPrice() {
        adapter.refresh();
        closeFilter();
    }

    public void filterSetFragment(Fragment newFragment) {
        filter = newFragment;
        getChildFragmentManager().beginTransaction()
                .replace(R.id.filter_fragment, filter)
                .commit();
    }

    public void closeOrdering(boolean refresh) {
        if(ordering != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(ordering)
                    .commit();
            ordering = null;
            if(refresh)
                adapter.refresh();
        }
    }

    void setLayoutManager() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int cw = (int) (dm.widthPixels * dpiCoef);
        int scrollPosition = 0;

        int spanCount =  cw <= SMALL_WIDTH ? 1 : cw >= LARGE_WIDTH ? 3 :  2;

        // If a layout manager has already been set, get current scroll position.
        if (rv.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) rv.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        RecyclerView.LayoutManager lm;
        if(rowMode) {
            lm = new LinearLayoutManager(getContext());
        } else {
            lm = new GridLayoutManager(getContext(), spanCount);
        }
        rv.setLayoutManager(lm);
        adapter = new Adapter();
        rv.setAdapter(adapter);

        rv.scrollToPosition(scrollPosition);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override public void handleOnBackPressed() { onBackPressed(); }
        };

        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        super.onAttach(context);
    }

    void onBackPressed() {
        model.popFolder();
        getParentFragmentManager().popBackStack();
    }

    class Adapter extends RecyclerView.Adapter<PriceController> {

        List<TreeElement> items = new ArrayList<>();

        public Adapter() {
            refresh();
        }

        public void refresh() {
            items.clear();
            for(TreeElement te : model.currentFolder.items) {
                if(model.priceFilter.inSet(te))
                    items.add(te);
            }
            Collections.sort(items, model.priceSort);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PriceController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(rowMode ? R.layout.price_row_view : R.layout.price_card_view,
                    parent, false);

            model.getBasket().setCanRemove(true);
            PriceController vh = new PriceController(model.getBasket(), v, rowMode, true, this::getItemBitmap);
            return vh;
        }

        private Bitmap getItemBitmap(com.serviko.dataobjects.Price price, ImageView imageView) {
            String url = model.makeUrl(price.code, false);
            Bitmap bmp = model.getPhoto(url);
            if(bmp == null) {
                requestImage(url, imageView);
            }
            return bmp;
        }

        @Override
        public void onBindViewHolder(@NonNull PriceController holder, int position) {
            holder.updateView(items.get(position).item);
        }

        @Override
        public int getItemCount() { return items.size(); }
    }
}
