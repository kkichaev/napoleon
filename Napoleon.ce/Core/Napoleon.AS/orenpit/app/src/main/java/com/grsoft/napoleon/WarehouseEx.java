package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.EquQty;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.EquipmentDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class WarehouseEx extends Warehouse{
    public static class EquFilter extends Filter{
        private List<String> ids = new ArrayList<>();

        public EquFilter(String name, String id) {
            super(name);

            DataTraveler.travel(EquQty.class, new DataTraveler.Travel<EquQty>() {
                @Override
                public boolean travel(DataTraveler<EquQty> item) {
                    ids.add(item.data.idItem);
                    return true;
                }
            }, String.format("id='%s'", id));
        }

        @Override
        public boolean inset(long priceRowID, String id) {
            return ids.contains(id);
        }
    }

    @Override
    protected void postAdapterInit() {
        FoldersAdapter.resetCache();

        if (DocType.getCurDoc().equals(EquipmentDoc.instance()))
            adapter.putFilter(new EquFilter("EquFilter", document.getId()));

        super.postAdapterInit();
    }

    @Override
    protected void setTextColumnValue(TextView textView, int type, Price price) {
        super.setTextColumnValue(textView, type, price);

        if (DocType.getCurDoc().equals(EquipmentDoc.instance()) && textView.getId() == R.id.tvClmn2)
            textView.setVisibility(View.INVISIBLE);
        else if (DocType.getCurDoc().equals(OrderDoc.instance())){
            if (type == COLUMN_COST){
                int cost = getCost(price);
                int wcost = ((PriceEx)price).wcost;

                if (wcost > 0) {
                    String text = String.format("%s/%s", Util.IntToScaleStr(wcost, Consts.SUM_SCALE), Util.IntToScaleStr(cost, Consts.SUM_SCALE));
                    textView.setText(text);
                }
            }
        }
    }
}
