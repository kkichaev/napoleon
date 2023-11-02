package com.grsoft.napoleon;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grsoft.dataobjects.Brand;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Supplier;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.HashMap;
import java.util.Map;

public class RemnantsDetailEx extends RemnantsDetail {

    Map<Object, Brand> brands = new HashMap<>();
    Map<Object, Supplier> suppl = new HashMap<>();

    @Override
    protected RemnantItemsAdapter createAdapter() {
        brands = DbReader.fetchDic(Brand.class, "id");
        suppl = DbReader.fetchDic(Supplier.class, "id");

        return new Adapter();
    }

    String getItemName(String id) {
        String[] prt = id.split("\t");
        if(prt.length < 2) {
            return id;
        }
        Brand b = brands.get(prt[0]);
        Supplier s = suppl.get(prt[1]);
        return String.format("<b>%s</b><br/>%s"
                , b != null ? b.name : prt[0]
                , s != null ? s.name : prt[1]);
    }

    class Adapter extends RemnantItemsAdapter {
        @Override
        public View getView(int arg0, View view, ViewGroup arg2) {
            if(view == null) {
                view = View.inflate(RemnantsDetailEx.this, R.layout.remnantsdetail_list_row, null);
            }

            RemnantItem item = (RemnantItem) getItem(arg0);

            TextView tv = (TextView)view.findViewById(R.id.tvName);
            linesController.prepareTextView(tv);
            tv.setText(Html.fromHtml(getItemName(item.id)));

            tv = (TextView)view.findViewById(R.id.tvQty);
            tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));

            if( Features.SHOW_NUMBER_IN_ORDER ) {
                tv = (TextView)view.findViewById(R.id.tvOrder);
                if( tv != null ) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(Integer.toString(arg0+1));
                }
            }

            view.setTag(item);
            return view;
        }
    }
}
