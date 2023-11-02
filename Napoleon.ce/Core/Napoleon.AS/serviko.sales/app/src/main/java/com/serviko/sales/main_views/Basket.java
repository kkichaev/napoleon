package com.serviko.sales.main_views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serviko.dataobjects.BasketItem;
import com.serviko.dataobjects.priceTree.PriceTree;
import com.serviko.sales.BasketDetailDlg;
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;
import com.serviko.utils.PriceController;

import java.util.ArrayList;
import java.util.List;

public class Basket extends BaseView {

    public static String TAG = Basket.class.toString();

    RecyclerView rv;
    View progress;
    View btnSend;

    @Override
    int getResourceId() {
        return R.layout.basket_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        model.basketQty.observe(this, qty -> {
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
            sending();
        });
        rv = v.findViewById(R.id.lvItems);

        progress = v.findViewById(R.id.llProgress);
        model.getRequestInProgress().observe(this, aBoolean -> {
            progress.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!aBoolean);
        });

        model.getRequestResult().observe(this, aBoolean -> {
            if(aBoolean) {
                model.clearRequsetResult();
                model.getBasket().clear();
            }
        });

        model.getRequestError().observe(this, err -> {
            if(err != null) {
                ((MainActivity) getActivity()).loadFragment(new BasketError(), true);
            }
        });

        return v;
    }

    private void sending() {
        if(model.getBasket().size() > 0) {
            if(model.getBasket().assignDlvDate) {
                sendBasket();
            } else {
                (new BasketDetailDlg(model.getBasket(), dlg -> {
                    sendBasket();
                })).show(getParentFragmentManager(), "");
            }
        }
    }

    void sendBasket() {
        model.sendBasket(getContext());
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
        View basketMode;
        View vempty = v.findViewById(R.id.llEmpty);
        View vbasket = v.findViewById(R.id.llBasket);
        if (qty == 0) {
            basketMode = vempty;
            vempty.setVisibility(View.VISIBLE);
        } else {
            basketMode = vbasket;
            vempty.setVisibility(View.GONE);
        }
        basketMode.setVisibility(View.VISIBLE);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new Adapter());

        com.serviko.dataobjects.Basket b = model.getBasket();
        TextView tv;
        tv = v.findViewById(R.id.tvItems);
        tv.setText(getContext().getString(R.string.basket_qty, b.size()));

        tv = v.findViewById(R.id.tvSum);
        tv.setText(Html.fromHtml(getContext().getString(R.string.basket_sum, b.sum())));
    }

    class Adapter extends RecyclerView.Adapter<PriceController> {

        List<com.serviko.dataobjects.Price> items = new ArrayList<>();

        public Adapter() {
            PriceTree pt = model.getPartner().getValue().getPrice();
            for(BasketItem bi : model.getBasket().items) {
                com.serviko.dataobjects.Price item = pt.find(bi.item.id);
                if(item != null) {
                    items.add(item);
                }
            }
        }

        @NonNull
        @Override
        public PriceController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.price_row_basket_view, parent, false);
            PriceController vh = new PriceController(model.getBasket(), v, true, true, this::getItemBitmap);
            return vh;
        }

        private Bitmap getItemBitmap(com.serviko.dataobjects.Price price, ImageView imageView) {
            String url = model.makeUrl(price.code, false);
            Bitmap bmp = model.getPhoto(url);
            if (bmp == null) {
                requestImage(url, imageView);
            }
            return bmp;
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
