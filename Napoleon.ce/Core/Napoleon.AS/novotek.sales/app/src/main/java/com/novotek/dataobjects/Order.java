package com.novotek.dataobjects;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.novotek.dataobjects.xml.WSDLElement;
import com.novotek.sales.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order implements Comparable<Order>
{
    static  final int STATUS_CREATED = 0; // создан
    static  final int STATUS_PROCEEDED = 10; // в обработка
    static  final int STATUS_ASSEMBLING = 20; // Отдан на сборку
    static  final int STATUS_ASSEMBLED = 30; // Собран
    static  final int STATUS_DELIVERED = 40; // отгружен
    static  final int STATUS_PAYED = 50; // Оплачен
    static  final int STATUS_CANCELED = 60; // Отменен

    public List<OrderItem> items = new ArrayList<>();

    public String id = "";
    public String dateDelivery = "";

    public int statusValue = 0;

    public float sum() {
        float ret = 0;
        for (OrderItem oi : items)
            ret += oi.sum;
        return ret;
    }

    public int count() {
        return items.size();
    }


    @Override
    public int compareTo(Order order) {
        return order.dateDelivery.compareTo(dateDelivery);
    }

    public boolean isActivew() {
        return statusValue == STATUS_CREATED || statusValue == STATUS_PROCEEDED || statusValue == STATUS_ASSEMBLING || statusValue == STATUS_ASSEMBLED;
    }

    public boolean canCancel() {
        return statusValue == STATUS_CREATED || statusValue == STATUS_PROCEEDED;
    }

    public boolean canDelete() {
        return statusValue == STATUS_PAYED || statusValue == STATUS_CANCELED;
    }

    public boolean canCopy() {
        return true;
    }

    public void updateStatusText(TextView tv) {
        int color = getStatusColor(tv.getContext());
        int text = getStatusText();

        Drawable[] dw = tv.getCompoundDrawables();
        DrawableCompat.setTint(dw[0], color);
        tv.setCompoundDrawables(dw[0], dw[1], dw[2], dw[3]);
        tv.setTextColor(color);
        tv.setText(text);
    }

    public int getStatusColor(Context context) {
        int color = R.color.gray_cicle;
        switch (statusValue) {
            case STATUS_DELIVERED:
                color = R.color.order_orange;
                break;
            case STATUS_CREATED:
            case STATUS_PROCEEDED:
                color = R.color.order_gray;
                break;
            case STATUS_ASSEMBLING:
                color = R.color.order_blue;
                break;
            case STATUS_ASSEMBLED:
                color = R.color.order_brown;
                break;
            case STATUS_PAYED:
                color = R.color.order_green;
                break;
            case STATUS_CANCELED:
                color = R.color.order_red;
                break;
        }

        Resources res = context.getResources();
        return res.getColor(color, null);
    }

    public int getStatusText() {
        switch (statusValue) {
            case STATUS_DELIVERED:
                return R.string.order_status_delivered;
            case STATUS_CREATED:
            case STATUS_PROCEEDED:
                return R.string.order_status_proceeded;
            case STATUS_ASSEMBLING:
                return R.string.order_status_assembling;
            case STATUS_ASSEMBLED:
                return R.string.order_status_assebmled;
            case STATUS_CANCELED:
                return R.string.order_status_canceld;
            case STATUS_PAYED:
                return R.string.order_status_payed;
        }
        return R.string.order_status_delivered;
    }
}
