package com.grsoft.napoleon;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.views.TextViewCrossOut;

public class WarehouseEx extends Warehouse {

    static String lastId = "";

    public static void clearCache() {
        lastId = "";
    }

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
        if(document instanceof OrderImpl) {
            if(!lastId.equals(document.getId())) {
                FoldersAdapter.resetCache();
            }
            ret.putFilter(new CostFilter((OrderImpl)document));
        }
        return ret;
    }

    @Override
    protected void setTextColumnValue(TextView textView, int type, Price price) {
        textView.setTextColor(Color.BLACK);

        ViewGroup vg = (ViewGroup) textView.getParent();
//        TextViewCrossOut src = vg.findViewById(textView.getId() == R.id.tvClmn1 ? R.id.tvoClmn1 : R.id.tvoClmn2);
//        if(src != null) {
//            src.setVisibility(View.GONE);
//        }

        if((type == COLUMN_COST || type == COLUMN_COST_SUM) && document instanceof OrderImpl) {
            long sum = ((OrderImpl)document).getItemSum(price);
            if(type == COLUMN_COST || sum == 0) {
                int cost = Features.COST_MANAGER.getCost(price.id, document.getSumType());
                int regCost = ((CostManagerImplEx)Features.COST_MANAGER).getCurCost().regularCost;
                if( cost != 0 && regCost != 0) {
                    String text = Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
                    text += "\u2026" + Util.IntToScaleStr(regCost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
                    textView.setText(text);
                    textView.setTextColor(getResources().getColor(R.color.green_cost));
//                    textView.setTextColor(0x04CB12);
                    return;
                }
            }
        }
        super.setTextColumnValue(textView, type, price);
    }

    @Override
    protected void layoutColumns(TextView tvClmn1, TextView tvClmn2, int cellWidth, LinearLayout llQuant) {
//        tvClmn1.setGravity(Gravity.RIGHT);
//        LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) tvClmn1.getLayoutParams();
//        lp.gravity = Gravity.RIGHT;

//        tvClmn1.setWidth(cellWidth / 2);
//        tvClmn2.setWidth(cellWidth / 2);

//        View llClmn1 = llQuant.findViewById(R.id.llClmn1);
//        LinearLayout llClmn2 = llQuant.findViewById(R.id.llClmn2);

//        llClmn2.setMinimumWidth(cellWidth / 2);
        if (linesController.isMinLines()) {
            llQuant.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            llQuant.setOrientation(LinearLayout.VERTICAL);
        }
        llQuant.requestLayout();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.priceitemrowex;
    }

    class CostFilter extends Filter {
        public static final String NAME = "CostFilter";
        int sumType;

        public CostFilter(OrderImpl doc) {
            super(NAME);

            sumType = doc.getSumType();
        }

        @Override
        public boolean inset(long priceRowID, String id) {

            if(Features.COST_MANAGER != null) {
                return Features.COST_MANAGER.getCost(id, sumType) != 0;
            }
            return super.inset(priceRowID, id);
        }
    }
}
