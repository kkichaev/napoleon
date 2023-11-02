package com.novotek.sales.main_views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.NameObj;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.priceTree.FolderBase;
import com.novotek.dataobjects.priceTree.FolderSrc;
import com.novotek.dataobjects.priceTree.PriceTree;
import com.novotek.dataobjects.priceTree.SubFolder;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.PriceController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Price extends BaseView implements PriceController.Events {
    static String TITLE_KEY = "title";
    static String ITEMS_KEY = "items";
    static String QUERY_KEY = "query";
    static String PARENT_KEY = "parent";
    static String BRAND_KEY = "brand";

    static final int SMALL_WIDTH = 310;
    static final int LARGE_WIDTH = 780;
    float dpiCoef;
    boolean searchMode = false;
    String parent = null;

    public Price(FolderSrc parent, FolderBase folder) {
        Bundle b = new Bundle();
        ArrayList<String> items = new ArrayList<>();
        for(com.novotek.dataobjects.Price p : ((SubFolder)folder).items) {
            items.add(p.id);
        }
        b.putStringArrayList(ITEMS_KEY, items);
        b.putString(TITLE_KEY, folder.name.toString());
        b.putString(PARENT_KEY, parent.name.name_en);
        setArguments(b);
    }

    public Price(ArrayList<String> src, String title) {
        Bundle b = new Bundle();
        b.putStringArrayList(ITEMS_KEY, src);
        b.putString(TITLE_KEY, title);
        b.putBoolean(BRAND_KEY, true);
        setArguments(b);
    }

    public Price(String queryText, ArrayList<String> src, String title, String parent, boolean fromBrand) {
        Bundle b = new Bundle();
        b.putString(QUERY_KEY, queryText);
        b.putStringArrayList(ITEMS_KEY, src);
        b.putString(TITLE_KEY, title);
        b.putString(PARENT_KEY, parent);
        b.putBoolean(BRAND_KEY, fromBrand);
        setArguments(b);
    }

    public static String TAG = Price.class.toString();

    static boolean rowMode = true;

//    FindGoodsController fgc;

    RecyclerView rv;
    List<NameObj> filters = new ArrayList<>();
    Adapter adapter;
    FilterAdapter filterAdapter;

    public static NameObj selectedFilter = null;
    TextView filterView, filterTitle;

    String searchText = "";
    protected List<com.novotek.dataobjects.Price> src;

    @Override
    protected int getResourceId() { return R.layout.price_view; }

    @Override
    public String getFragmentTag() { return TAG; }

    protected boolean useFilter() { return true; }

    boolean fromBrand;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.back).setOnClickListener(view -> onBackPressed());

        rv = v.findViewById(R.id.lvItems);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        dpiCoef = 160 / dm.xdpi;

        filterTitle = v.findViewById(R.id.filter_title);

        src = new ArrayList<>();

        SearchView search = v.findViewById(R.id.search);
        search.setOnCloseListener(() -> {
            if(searchMode) {
                onBackPressed();
                return true;
            } else {
                searchText = "";
                adapter.refresh();
                v.findViewById(R.id.tvTitle).setVisibility(View.VISIBLE);
                updateFilters(v, src);
            }
            return false;
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchText = s.toLowerCase();
                List<com.novotek.dataobjects.Price> filtred = adapter.refresh();
                updateFilters(v, filtred);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        search.setOnSearchClickListener(view -> {
            if((int)(rv.getWidth() * dpiCoef) < LARGE_WIDTH)
                v.findViewById(R.id.tvTitle).setVisibility(View.GONE);
        });

        Bundle args = getArguments();
        String query = null;
        if(args != null) {
            parent = args.getString(PARENT_KEY);
            TextView tvTitle = ((TextView) v.findViewById(R.id.tvTitle));

            String title = args.getString(TITLE_KEY, "");
            tvTitle.setText(title);

            Partner partner = model.getPartner().getValue();
            if (partner != null) {
                PriceTree pt = partner.getPrice();
                List<String> ssrc = args.getStringArrayList(ITEMS_KEY);
                for (String s : ssrc) {
                    com.novotek.dataobjects.Price p = pt.get(s);
                    if (p != null) {
                        src.add(p);
                    }
                }
            }
            query = args.getString(QUERY_KEY);

            fromBrand = args.getBoolean(BRAND_KEY);
        }

        updateFilters(v, src);


//        fgc = new FindGoodsController(v, (MainActivity) getActivity());

        ImageView iv = v.findViewById(R.id.layout);
        iv.setOnClickListener(view -> {
            rowMode = !rowMode;
            iv.setImageResource(rowMode ? R.drawable.ic_icon_tile : R.drawable.ic_icon_row);
            setLayoutManager(src);
        });
        iv.setImageResource(rowMode ? R.drawable.ic_icon_tile : R.drawable.ic_icon_row);

        setLayoutManager(src);

        if(query != null) {
            search.setIconified(false);
            search.setQuery(query, true);
            searchMode = true;
        }
        return v;
    }

    private void updateFilters(View v, List<com.novotek.dataobjects.Price> price) {
        filters.clear();
        for(com.novotek.dataobjects.Price p : price) {
            if(!p.filterType.empty() && !filters.contains(p.filterType)) {
                filters.add(p.filterType);
            }
        }
        RecyclerView filterView = v.findViewById(R.id.filter);
        if(useFilter() && filters.size() > 0) {
            filterAdapter = new FilterAdapter();
            filterView.setAdapter(new FilterAdapter());
            filterView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            filterView.setVisibility(View.GONE);
        }

        updateFilterTitle();
    }

    void onFilterClick(NameObj obj, TextView filterView) {
        TextView prevView = this.filterView;
        if(selectedFilter == obj) {
            selectedFilter = null;
            this.filterView = null;
        } else {
            selectedFilter = obj;
            this.filterView = filterView;
        }

        if(filterView != null) {
            filterView.setBackgroundResource(R.color.colorPrimary);
            filterView.setTextColor(Color.WHITE);
        }
        if(prevView != null){
            prevView.setBackgroundResource(R.color.gray_bg);
            prevView.setTextColor(Color.BLACK);
        }

        updateFilterTitle();

        adapter.refresh();
    }

    private void updateFilterTitle() {
        if(selectedFilter != null) {
            filterTitle.setText(selectedFilter.toString());
            filterTitle.setVisibility(View.VISIBLE);
        } else {
            filterTitle.setVisibility(View.GONE);
        }
    }


    void setLayoutManager(List<com.novotek.dataobjects.Price> src) {
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
        adapter = new Adapter(src);
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
        if (fromBrand)
            ((MainActivity) getActivity()).openBrands();
        else if (parent == null)
            getParentFragmentManager().popBackStack();
        else {
            ((MainActivity) getActivity()).openFolder(model.getPartner().getValue().getPrice().find(parent), null);
        }
    }

    @Override
    public void itemClicked(com.novotek.dataobjects.Price item, PriceController ctrl) {
        ((MainActivity)getActivity()).openPriceItem(item);
    }

    class FiltersHolder extends RecyclerView.ViewHolder {

        public FiltersHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(NameObj obj) {
            TextView tv = itemView.findViewById(R.id.name);
            tv.setText(obj.toString());
            tv.setOnClickListener(v-> {
                onFilterClick(obj, tv);
            });
            // случай когда открыли карточку товара из фильтрованного каталога
            if(selectedFilter == obj) {
                if(filterView != tv) filterView = tv;
                filterView.setBackgroundResource(R.color.colorPrimary);
                filterView.setTextColor(Color.WHITE);
            }
        }
    }

    class FilterAdapter extends RecyclerView.Adapter<FiltersHolder> {

        @NonNull
        @Override
        public FiltersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.price_filter, parent, false);
            return new FiltersHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FiltersHolder holder, int position) {
            holder.update(filters.get(position));
        }

        @Override
        public int getItemCount() {
            return filters.size();
        }
    }

    class Adapter extends RecyclerView.Adapter<PriceController> {

        List<com.novotek.dataobjects.Price> items = new ArrayList<>();
        List<com.novotek.dataobjects.Price> source = new ArrayList<>();

        public Adapter(List<com.novotek.dataobjects.Price> source) {
            this.source = source;
            refresh();
        }

        public List<com.novotek.dataobjects.Price> refresh() {
            items.clear();
            for(com.novotek.dataobjects.Price p : source) {
                if(searchText.length() > 0 && !p.name.toLowerCase().contains(searchText))
                    continue;

                if(selectedFilter == null || p.filterType.equals(selectedFilter))
                    items.add(p);
            }
            Collections.sort(items);
            notifyDataSetChanged();
            return items;
        }

        @NonNull
        @Override
        public PriceController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(rowMode ? R.layout.price_row_view : R.layout.price_card_view,
                    parent, false);

            model.getBasket().setCanRemove(true);
            PriceController vh = new PriceController(model.getBasket(), v, rowMode, images, Price.this);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PriceController holder, int position) {
            holder.updateView(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }
    }

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

    public static Parcelable state;
}
