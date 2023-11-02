package com.novotek.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.Basket;
import com.novotek.dataobjects.BasketItem;
import com.novotek.dataobjects.Price;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

public class PriceController extends RecyclerView.ViewHolder {
    Basket basket;

    int qty;
    boolean rowMode;
    Price item;
    float cost;
    TextView tvCost;

    ImageGetController images;
    Events handler;

    public interface Events {
        void itemClicked(Price item, PriceController ctrl);
    }

    public PriceController(Basket basket, View parentView, boolean rowMode, ImageGetController images, Events handler) {
        super(parentView);
        this.basket = basket;
        this.rowMode = rowMode;

        this.images = images;
        this.handler = handler;
    }

    public static String formatCost(Context context, float cost) {
        return context.getString(R.string.order_sum, cost);
    }

    public void updateView(Price item) {
        this.item = item;
        TextView tv = itemView.findViewById(R.id.tvName);
        tv.setText(item.name);
        if(handler != null)
            tv.setOnClickListener(view -> handler.itemClicked(item, this));

        cost = item.price;
        qty = 0;

        updateRest();

        BasketItem bi = basket.find(item);
        if(bi != null) {
            qty = bi.qty;
        }

        tvCost = itemView.findViewById(R.id.btnCost);
        tvCost.setText(Html.fromHtml(formatCost(itemView.getContext(), cost)));

        itemView.findViewById(R.id.ivMinus).setOnClickListener(mv -> { changeQty(false); });
        itemView.findViewById(R.id.ivPlus).setOnClickListener(mv -> { changeQty(true); });

        updateQty(true);

        ImageView iv = itemView.findViewById(R.id.imageView);
        if(item.url.size() > 0) {
            images.setImage(item.url.get(0), iv);
        } else {
            iv.setImageResource(R.drawable.coming_soon);
        }
        if(handler != null)
            iv.setOnClickListener(view -> handler.itemClicked(item, this));

        View vqty = itemView.findViewById(R.id.tvQty);
        if(vqty instanceof EditText) {
            ((EditText)vqty).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        qty = Integer.parseInt(editable.toString());
                    } catch (Exception e) {
                        qty = 0;
                        e.printStackTrace();
                    }
                    updateQty(false);
                    updateOrder();
                }
            });
        }
    }

    private void updateRest() {
        TextView tv;
        String text = "";
        tv = itemView.findViewById(R.id.tvRest);

        if(tv != null) {
            text = itemView.getContext().getString(R.string.rest, item.qty);
            tv.setText(text);
        }
    }

    void updateOrder() {
        basket.changeQty(item, qty, false);
    }

    void changeQty(boolean add) {
        if(add) {
            item.qty--;
            qty++;
        } else {
            if(qty > 0) {
                qty--;
                item.qty++;
            }
            if(qty < 0 ) {
                qty = 0;
            }
        }
        updateRest();
        updateQty(true);
        updateOrder();
    }

    void updateQty(boolean setQty) {
        if(setQty) {
            TextView tv = itemView.findViewById(R.id.tvQty);
            tv.setText(Integer.toString(qty));
        }

        ImageView iv = itemView.findViewById(R.id.ivMinus);
        int disableDraw = R.drawable.ic_minus;
        int enableDraw = R.drawable.ic_minus_enable;

        iv.setImageResource(qty == 0 ? disableDraw : enableDraw);

        TextView sum = itemView.findViewById(R.id.tvSum);
        if( sum != null ) {
            float ts = qty * cost;
            sum.setText(Html.fromHtml(formatCost(itemView.getContext(), ts)));
        }

        iv = itemView.findViewById(R.id.ivPlus);
        iv.setEnabled(item.qty > 0);
        iv.setImageResource(item.qty > 0 ? R.drawable.ic_plus : R.drawable.ic_plus_disable);
    }

}
