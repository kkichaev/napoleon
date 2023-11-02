package com.serviko.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.BasketItem;
import com.serviko.dataobjects.Price;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;
import com.serviko.view.TextViewCrossOut;

public class PriceController extends RecyclerView.ViewHolder {
    Basket basket;

    int qty;
    boolean packMode, rowMode, showActionIcon;
    Price item;
    float cost;
    TextView tvCost;
    TextViewCrossOut tvco;

    public interface ImageRequest {
        Bitmap getImage(Price item, ImageView iv);
    }

    ImageRequest request;

    public PriceController(Basket basket, View parentView, boolean rowMode, boolean showActionIcon, ImageRequest request) {
        super(parentView);
        this.basket = basket;
        this.rowMode = rowMode;
        this.showActionIcon = showActionIcon;

        this.request = request;
        View v = parentView.findViewById(R.id.btnCost);

    }

    String formatCost(float cost) {
        return String.format("%.02f &#x20bd", cost);
    }
    String formatFullCost(float cost) { return String.format("%.01f", cost); }

    public void updateView(Price item) {
        this.item = item;
        TextView tv = itemView.findViewById(R.id.tvName);
        tv.setText(item.name);

        String text = "";
        if(item.volume > 0) {
            text = String.format("%s ë", item.volume);
        }
        tv = itemView.findViewById(R.id.tvVolume);
        tv.setOnClickListener(view -> openAction(item.action));
        tv.setText(text);

        packMode = false;
        qty = 0;
        BasketItem bi = basket.find(item);
        if(bi != null) {
            packMode = bi.packMode;
            qty = bi.qty;
            if(packMode) {
                qty /= item.inPack;
            }
        }

        boolean  haveDiscount = item.discount != 0;
        cost = item.cost;
        if(haveDiscount) {
            cost -= item.discount;
        }

        tvCost = itemView.findViewById(R.id.btnCost);
        tvCost.setText(Html.fromHtml(formatCost(cost)));
        tvCost.setTextColor(ContextCompat.getColor(tvCost.getContext(), haveDiscount ? R.color.action : R.color.black));
        tvCost.setOnClickListener(view -> openAction(item.action));

        if(!rowMode) {
            tvCost.setGravity(haveDiscount ? Gravity.RIGHT : Gravity.CENTER_HORIZONTAL);
        }
        setMode(packMode);

        itemView.findViewById(R.id.ivMinus).setOnClickListener(mv -> { changeQty(false); });
        itemView.findViewById(R.id.ivPlus).setOnClickListener(mv -> { changeQty(true); });

        itemView.findViewById(R.id.btnItems).setOnClickListener(mv -> changePack(false));
        Button pack = itemView.findViewById(R.id.btnPack);
        pack.setOnClickListener(mv -> changePack(true));
        pack.setText(itemView.getContext().getString(R.string.in_pack_param, item.inPack));

        updateQty();

        if(request != null) {
            ImageView iv = itemView.findViewById(R.id.imageView);
            Bitmap bmp = request.getImage(item, iv);
            if(bmp != null) {
                iv.setImageBitmap(bmp);
            }
        }

        tvco = itemView.findViewById(R.id.tvRealCost);
        if(tvco != null) {
            tvco.setVisibility(haveDiscount ? View.VISIBLE : View.GONE);
            tvco.setText(Html.fromHtml(formatFullCost(item.cost)));
        }

        View v = itemView.findViewById(R.id.ivAction);
        if(v != null) {
            v.setVisibility(showActionIcon && item.action != null ? View.VISIBLE : View.INVISIBLE);
            v.setOnClickListener(view -> openAction(item.action));
        }

    }

    void openAction(ActionDef action) {
        if(showActionIcon && action != null)
            ((MainActivity)itemView.getContext()).openAction(action);
    }

    void updateOrder() {
        basket.changeQty(item, qty, packMode);
    }

    void changePack(boolean pack) {
        if(packMode != pack) {
            packMode = pack;
            qty = 0;
            setMode(packMode);
            updateQty();
            updateOrder();
        }
    }

    void changeQty(boolean add) {
        if(add) {
            if(packMode) qty++;
            else qty += item.quant;
        } else {
            if(qty > 0) {
                if(packMode)
                    qty--;
                else {
                    qty -= item.quant;
                }
            }
            if(qty < 0 ) {
                qty = 0;
            }
        }
        updateQty();
        updateOrder();
    }

    void updateQty() {
        TextView tv = itemView.findViewById(R.id.tvQty);
        tv.setText(Integer.toString(qty));

        ImageView iv = itemView.findViewById(R.id.ivMinus);
        int disableDraw = R.drawable.ic_minus;
        int enableDraw = R.drawable.ic_minus_enable;

        iv.setImageResource(qty == 0 ? disableDraw : enableDraw);

        TextView sum = itemView.findViewById(R.id.tvSum);
        if( sum != null ) {
            float ts = qty * cost;
            if(packMode) ts *= item.inPack;
            sum.setText(Html.fromHtml(formatCost(ts)));
        }
    }

    void setMode(boolean packMode) {
        this.packMode = packMode;

        Context context = itemView.getContext();
        ColorStateList marked = ContextCompat.getColorStateList(context, R.color.btn_selected);
        ColorStateList normal = ContextCompat.getColorStateList(context, R.color.white);

        int markColor = ContextCompat.getColor(context, R.color.white);
        int normalColor = ContextCompat.getColor(context, R.color.btn_color_text);

        Button b = itemView.findViewById(R.id.btnItems);
        b.setBackgroundTintList(packMode ? normal : marked);
        b.setTextColor(packMode ? normalColor : markColor);

        Button b1 = itemView.findViewById(R.id.btnPack);
        b1.setBackgroundTintList(packMode ? marked : normal);
        b1.setTextColor(packMode ? markColor : normalColor);

        float ccost = cost;
        if(packMode)
            ccost *= item.inPack;
        tvCost.setText(Html.fromHtml(formatCost(ccost)));

        if(tvco != null) {
            ccost = item.cost;
            if(packMode) ccost *= item.inPack;
            tvco.setText(Html.fromHtml(formatFullCost(ccost)));
        }
    }
}
