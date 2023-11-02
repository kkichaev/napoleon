package com.serviko.sales;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.Price;
import com.serviko.view.PriceQtyPickerOld;
import com.serviko.view.TextViewCrossOut;

public class GoodsView extends BaseActivityOld implements PictureHolder.Handler {

    Price item = new Price();

    private static final String ID_TAG = "ID_TAG";

    public static void open(Context context, Price price) {
        Intent i = new Intent(context, GoodsView.class);
        i.putExtra(ID_TAG, price.id);
        context.startActivity(i);
    }

    @Override protected int getLayoutID() { return R.layout.goods_view; }
    @Override protected int getBottomMenuID() { return 0; } //R.id.itOrder; }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;

        final Partner p = PartnerList.getCurrent();

        String id = b.getString(ID_TAG, "");
        if(id.length() > 0) {
            item = p.getPrice().find(id);
        }
        if(item == null)
            item = new Price();

        final MaterialToolbar mb = findViewById(R.id.topAppBar);
        mb.setTitle(item.name);
        mb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        TextView tv = findViewById(R.id.tvName);
        tv.setText(item.name);

        final PriceQtyPickerOld pq = findViewById(R.id.pqQty);
        pq.setData(item, p.basket);

        TextViewCrossOut tco = findViewById(R.id.tvCost);
        tco.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.cost)));

        tv = findViewById(R.id.tvActCost);
        if(item.discount > 0) {
            tv.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.cost - item.discount)));
            tco.setCrossOut(true);
        } else {
            tv.setText("");
            tco.setCrossOut(false);
        }

        PictureHolder.setImage((ImageView) findViewById(R.id.imageView), item);

        findViewById(R.id.tvPartner).setEnabled(false);

        tv = findViewById(R.id.tvAvail);
        tv.setText("Остаток: " + Integer.toString((int)(item.qty + 0.005)) + " шт.");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ID_TAG, item.id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PictureHolder.addHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PictureHolder.removeHandler(this);
    }

    @Override
    public void onReceive(String id, final Bitmap img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView iv = findViewById(R.id.imageView);
                iv.setImageBitmap(img);
            }
        });
    }
}
