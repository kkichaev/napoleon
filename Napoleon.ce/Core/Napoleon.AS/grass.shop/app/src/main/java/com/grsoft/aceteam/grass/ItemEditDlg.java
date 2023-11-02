package com.grsoft.aceteam.grass;

import static java.lang.String.*;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.grsoft.dataobjects.GrassDiscount;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ItemEditDlg extends DialogFragment {
    Model model;

    OrderItemEx orderItem;

    PriceUnit selUnit;
    PriceEx priceItem;
    Adapter adapter;

    View view;

    int qty = 0;

    public String sumText(long sum) { return Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false); }
    public String article() { return "Àðò.:" + priceItem.id; }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.order_item_add, null, false);

        model = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(Model.class);

        model.currentItem().observe(this, item -> {
            if(item == null) {
                return;
            }
            priceItem = item;
            orderItem = model.getItem(item);

            ((TextView)view.findViewById(R.id.tvArticle)).setText(article());
            ((TextView)view.findViewById(R.id.tvName)).setText(priceItem.name);

            Spinner sp = view.findViewById(R.id.spUnits);
            ArrayAdapter<PriceUnit> aa = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, priceItem.units);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(aa);

            adapter = new Adapter(getContext(), priceItem, orderItem.costItem);
            ListView lv = view.findViewById(R.id.list);
            lv.setAdapter(adapter);

            PriceUnit u = item.getUnit(orderItem.unit);
            int sel = item.units.indexOf(u);
            if(sel >= 0 ) {
                sp.setSelection(sel);
            }
            qty = (int)((long)orderItem.qty * Consts.QTY_SCALE)/u.inpack;
            setQty();
        });

//        binding.list.setDividerHeight(0);
        view.findViewById(R.id.btnMinus).setOnClickListener(this::minus);
        view.findViewById(R.id.btnPlus).setOnClickListener(this::plus);
        view.findViewById(R.id.btnOK).setOnClickListener(this::doOK);
        view.findViewById(R.id.btnCancel).setOnClickListener((v)->dismiss());

        ((Spinner)view.findViewById(R.id.spUnits)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selUnit = (PriceUnit)priceItem.units.get(i);
                adapter.refresh(selUnit);
                updateSum();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int w = dm.widthPixels * 4 / 5;
        getDialog().getWindow().setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setQty() {
        ((TextView)view.findViewById(R.id.tvQty)).setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
    }

    private void doOK(View view) {
        orderItem.qtyPack = qty;
        if(selUnit != null) {
            orderItem.unit = selUnit.id;
            orderItem.qty = (int)((long)qty * selUnit.inpack / Consts.QTY_SCALE);
        } else {
            orderItem.qty =orderItem.qtyPack;
        }
        model.updateItem(orderItem);
        dismiss();
    }

    private void plus(View view) {
        qty += Consts.QTY_SCALE;
        setQty();
        updateSum();
    }

    private void minus(View view) {
        if (qty > Consts.QTY_SCALE) {
            qty -= Consts.QTY_SCALE;
        } else {
            qty = 0;
        }
        setQty();
        updateSum();
    }

    private void updateSum() {
        long q = qty;
        long c = orderItem.costItem;
        int discount = adapter.getDiscount(q / Consts.QTY_SCALE);

        if(selUnit != null && selUnit.inpack != Consts.QTY_SCALE) {
            q = q * selUnit.inpack / Consts.QTY_SCALE;
        }

        if(discount != 0)
            c = CostStrategy.costWithDiscount(c, discount, Consts.SUM_SCALE);
        orderItem.discount = discount;
        orderItem.cost = c;

        long sum = c * q / Consts.QTY_SCALE;
        ((TextView)(view.findViewById(R.id.sum))).setText(sumText(sum));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private static class Adapter extends BaseAdapter {
        private final long cost;
        Context context;
        List<GrassDiscount> discounts;
        List<GrassDiscount> src = new ArrayList<>();
        PriceUnit unit;
        public Adapter(Context context, PriceEx item, long cost){
            this.context = context;
            this.cost = cost;
            this.discounts = item.discounts;
        }

        public int getDiscount(long qty) {
            int dsc = 0;
            int tq = 0;
            for(GrassDiscount g : src) {
                if(g.qty <= qty && g.qty > tq) {
                    dsc = g.discount;
                    tq = g.qty;
                }
            }

            return dsc;
        }

        public void refresh(PriceUnit unit) {
            this.unit = unit;
            src.clear();
            for(GrassDiscount g : discounts) {
                if(g.unit.equals(unit.code)) {
                    src.add(g);
                }
            }

            GrassDiscount g = new GrassDiscount();
            g.discount = 0;
            g.qty = 1;
            g.unit = unit.code;
            src.add(0, g);

            Collections.sort(src);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return src.size();
        }

        @Override
        public Object getItem(int i) {
            return src.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                view = View.inflate(context, R.layout.order_item_row, null);

            GrassDiscount d = (GrassDiscount) getItem(i);
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(unit.name);

            tv = view.findViewById(R.id.tvPackQty);
            tv.setText(Integer.toString(d.qty));


            tv = view.findViewById(R.id.tvSum);
            long cc = cost * unit.inpack / Consts.QTY_SCALE;
            long sum =  CostStrategy.costWithDiscount(cc, d.discount, Consts.SUM_SCALE);
            tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));

            return view;
        }
    }
}
