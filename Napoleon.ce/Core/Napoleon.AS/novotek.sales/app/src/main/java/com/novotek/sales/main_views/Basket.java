package com.novotek.sales.main_views;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.BasketItem;
import com.novotek.dataobjects.Price;
import com.novotek.dataobjects.ProjectData;
import com.novotek.dataobjects.priceTree.PriceTree;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.PriceController;

import java.util.ArrayList;
import java.util.List;

public class Basket extends BaseView implements PriceController.Events {

    public static String TAG = Basket.class.toString();

    RecyclerView rv;
    View progress;
    View btnSend;
    boolean belowMin;

    @Override
    protected int getResourceId() {
        return R.layout.basket_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        model.basketQty.observe(getViewLifecycleOwner(), qty -> {
            onBasketChanged(qty, v);
        });

        v.findViewById(R.id.btnCatalog).setOnClickListener(view -> {
            ((MainActivity) getActivity()).openItem(R.id.itCatalog);
        });

        v.findViewById(R.id.btnRemove).setOnClickListener(vew -> {
            model.getBasket().clear();
        });

        btnSend = v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(view -> {
            ((MainActivity)getActivity()).openBasketDetail();
        });
        rv = v.findViewById(R.id.lvItems);

        progress = v.findViewById(R.id.llProgress);
        model.getRequestInProgress().observe(getViewLifecycleOwner(), aBoolean -> {
            progress.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!aBoolean && !belowMin);
        });

        model.getRequestResult().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean) {
                model.clearRequestResult();
                model.getBasket().clear();
            }
        });

        model.getRequestError().observe(getViewLifecycleOwner(), err -> {
            if(err != null) {
                ((MainActivity) getActivity()).loadFragment(new BasketError(), true);
            }
        });

        TextView tv = v.findViewById(R.id.min_sum_text);
        tv.setText(Html.fromHtml(getString(R.string.min_order, ProjectData.commonInfo.min_order)));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        model.getBasket().setCanRemove(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        model.getBasket().setCanRemove(true);
        model.getBasket().removeEmpty();
    }

    private void onBasketChanged(Integer qty, View v) {

        com.novotek.dataobjects.Basket b = model.getBasket();
        float sum = b.sum();
        belowMin = (sum <  ProjectData.commonInfo.min_order);

        View minAlert = v.findViewById(R.id.min_sum_alert);
        Button btn = v.findViewById(R.id.btnSend);
        minAlert.setVisibility(belowMin ? View.VISIBLE : View.GONE);
        btn.setEnabled(!belowMin);
        btn.setBackgroundColor(getResources().getColor(belowMin ? R.color.silver : R.color.colorPrimary));

        View basketMode;
        View vempty = v.findViewById(R.id.llEmpty);
        View vbasket = v.findViewById(R.id.llBasket);
        View rmv = v.findViewById(R.id.btnRemove);
        if (qty == 0) {
            basketMode = vempty;
            vempty.setVisibility(View.VISIBLE);
            rmv.setVisibility(View.INVISIBLE);
        } else {
            basketMode = vbasket;
            vempty.setVisibility(View.GONE);
            rmv.setVisibility(View.VISIBLE);
        }
        basketMode.setVisibility(View.VISIBLE);

        if(rv.getAdapter() == null) {
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new Adapter());
        }

        TextView tv;
        tv = v.findViewById(R.id.tvItems);
        tv.setText(getContext().getString(R.string.basket_qty, b.size()));

        tv = v.findViewById(R.id.tvSum);
        tv.setText(Html.fromHtml(getContext().getString(R.string.basket_sum, sum)));
    }

    @Override
    public void itemClicked(Price item, PriceController ctrl) {
        ((MainActivity)getActivity()).openPriceItem(item);
    }

    class Adapter extends RecyclerView.Adapter<PriceController> {

        List<Price> items = new ArrayList<>();

        public Adapter() {
            PriceTree pt = model.getPartner().getValue().getPrice();
            for(BasketItem bi : model.getBasket().items) {
                Price item = pt.get(bi.item.id);
                if(item != null) {
                    items.add(item);
                }
            }
        }

        @NonNull
        @Override
        public PriceController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.price_row_basket_view, parent, false);
            PriceController vh = new PriceController(model.getBasket(), v, true, images, Basket.this);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PriceController holder, int position) {
            holder.updateView(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
