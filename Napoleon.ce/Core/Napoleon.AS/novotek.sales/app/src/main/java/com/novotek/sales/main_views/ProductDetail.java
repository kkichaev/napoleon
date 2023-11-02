package com.novotek.sales.main_views;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.novotek.dataobjects.Brand;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.Price;
import com.novotek.dataobjects.ProjectData;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.DotsController;
import com.novotek.utils.Favorites;
import com.novotek.utils.ImageGetController;
import com.novotek.utils.PriceController;

import java.util.ArrayList;
import java.util.List;

public class ProductDetail extends BaseView {
    public static String TAG = ProductDetail.class.toString();
    static final String PRICE_KEY = "product";

    Price product = new Price();
    DotsController dots;
    Favorites favorites;

    @Override protected int getResourceId() { return R.layout.product_detail_view_new; }

    @Override public String getFragmentTag() { return TAG; }

    public ProductDetail(Price item) {
        Bundle b = new Bundle();
        b.putString(PRICE_KEY, item.id);
        setArguments(b);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.title_characteristics).setOnClickListener((w)->{
            View panel = v.findViewById(R.id.characteristics_panel);
            panel.setVisibility(panel.isShown() ? View.GONE :  View.VISIBLE );
        });

       // v.findViewById(R.id.title_description)
//        Bundle b = getArguments();
//        Partner partner = model.getPartner().getValue();
//        if( b!= null ) {
//            Price p = partner.getPrice().price.get(b.getString(PRICE_KEY, ""));
//            if( p != null ) {
//                product = p;
//            }
//        }
//
//        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());
//
//
//        favorites = new Favorites(getContext());
//        final ImageView fav = v.findViewById(R.id.favorites);
//        fav.setImageResource(favorites.contains(product) ? R.drawable.ic_favorite : R.drawable.ic_favorite_gray);
//        v.findViewById(R.id.fav_card).setOnClickListener(view -> {
//            // add to favorites
//            favorites.change(product);
//            fav.setImageResource(favorites.contains(product) ? R.drawable.ic_favorite : R.drawable.ic_favorite_gray);
//        });
//
//        // prefetch images
//        ImageView iv = new ImageView(getContext());
//        for(String u : product.url)
//            images.setImage(u, iv);
//
//        ImageFragment.images = images;
//        dots = new DotsController(null, v.findViewById(R.id.image_count));
//        dots.update(product.url.size());
//
//        ViewPager2 vp = v.findViewById(R.id.photos);
//        vp.setAdapter(new Adapter(product, this));
//        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                dots.setCurrent(position);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                dots.setCurrent(position);
//            }
//        });
//
//
////        RecyclerView rv = v.findViewById(R.id.photos);
////        Adapter adapter = new Adapter(product);
////        rv.setAdapter(adapter);
////        rv.setLayoutManager(new SlowLinearManager(getContext(), RecyclerView.HORIZONTAL, false));
////
////        dots = new DotsController(rv, v.findViewById(R.id.image_count));
////        dots.update();
//
//        TextView tv = v.findViewById(R.id.name);
//        tv.setText(product.name);
//
//        String costStr = PriceController.formatCost(getContext(), product.price);
//        tv = v.findViewById(R.id.cost);
//        tv.setText(Html.fromHtml(costStr));
//
//        if(product.weight > 0) {
//            tv = v.findViewById(R.id.weight);
//            tv.setText(getString(R.string.prod_weight, (int)(product.weight + 0.5)));
//        }
//
//        ((TextView)v.findViewById(R.id.describe)).setText(product.description);
//
//        Brand brnd = ProjectData.brands.get(product.brand);
//        Button brand = v.findViewById(R.id.brand);
//        brand.setText(product.brand.toString());
//        if(brnd != null) {
//            brand.setOnClickListener(view -> ((MainActivity)getActivity()).openBrand(brnd));
//        }
//
//        tv = v.findViewById(R.id.qty);
//        tv.setText(getString(R.string.qty_pack, (int)(product.qty + 0.05)));
//
//        Button add = v.findViewById(R.id.add);
//        add.setText(Html.fromHtml(getString(R.string.put_tp_basket, costStr)));
//        add.setOnClickListener(view -> {
//            com.novotek.dataobjects.Basket basket = model.getBasket();
//            int qty = basket.getQty(product.id);
//            basket.changeQty(product, qty+1, false);
//            getParentFragmentManager().popBackStack();
//        });
        return v;
    }

//    static class SlowLinearManager extends LinearLayoutManager {
//        public SlowLinearManager(Context context, int orientation, boolean reverseLayout) {
//            super(context, orientation, reverseLayout);
//        }
//
//        @Override
//        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//            dx = dx * 2 / 3;
//            int val = super.scrollHorizontallyBy(dx, recycler, state);
////            Log.d(TAG, "scrollHorizontallyBy " + val + " dx " + dx);
//            return val;
//        }
//    }

    class Adapter extends FragmentStateAdapter {

        List<String> urls = new ArrayList<>();

        public Adapter(Price item, Fragment owner) {
            super(owner);
            urls.addAll(item.url);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new ImageFragment(urls.get(position));
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }
    }

    public static class ImageFragment extends Fragment {
        static final String ARG = "URL";
        public static ImageGetController images;

        public ImageFragment(String url) {
            Bundle b = new Bundle();
            b.putString(ARG, url);
            setArguments(b);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.image_view, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            String url = getArguments().getString(ARG);
            images.setImage(url, (ImageView) view);
        }
    }

//    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
//
//        List<String> urls = new ArrayList<>();
//        public Adapter(Price item) {
//            urls.addAll(item.url);
//        }
//
//        class Holder extends RecyclerView.ViewHolder {
//
//            public Holder(@NonNull View itemView) {
//                super(itemView);
//            }
//
//            public void update(String url) {
//                images.setImage(url, (ImageView) itemView);
//            }
//        }
//
//        @NonNull
//        @Override
//        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View v = LayoutInflater.from(getContext()).inflate(R.layout.product_detail_image, parent, false);
//            return new Holder(v);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull Holder holder, int position) {
//            String u = urls.get(position);
//            holder.update(u);
//        }
//
//        @Override
//        public int getItemCount() {
//            return urls.size();
//        }
//    }
 }
