package com.serviko.dataobjects.actionTree;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.Price;
import com.serviko.sales.PictureHolder;
import com.serviko.sales.R;
import com.serviko.view.PriceQtyPickerOld;
import com.serviko.view.TextViewCrossOut;
import com.serviko.view.treeview.InMemoryTreeNode;

public class ActionPrice extends InMemoryTreeNode {
    Price item;

    protected ActionPrice(Price item) {
        super(false);
        this.item = item;
    }

    public Price getPrice() { return item; }

    @Override public int getLayoutID() { return R.layout.action_price; }

    @Override
    public void updateView(View view, boolean expanded) {
        final Partner p = PartnerList.getCurrent();

        TextView tv = view.findViewById(R.id.tvName);
        tv.setText(item.name);

        final PriceQtyPickerOld pq = view.findViewById(R.id.pqQty);
        pq.setData(item, p.basket);

        TextViewCrossOut tco = view.findViewById(R.id.tvCost);
        tco.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.cost)));

        PictureHolder.setImage((ImageView)view.findViewById(R.id.imageView), item);
    }
}
