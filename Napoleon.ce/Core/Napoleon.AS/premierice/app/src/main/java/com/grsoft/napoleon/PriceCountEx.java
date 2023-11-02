package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.ExtrasConst;

public class PriceCountEx extends PriceCount{

    static String BONUS = "bonusTag";

    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc, boolean isBonus) {
        Intent i = new Intent(context, activity);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        i.putExtra(BONUS, isBonus);

        context.startActivity(i);
    }

    @Override protected int getContentViewId() { return R.layout.pricecount_ex; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((RadioButton)findViewById(R.id.rbPack)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cbPackets.setChecked(isChecked);
                updateSumTextView();
            }
        });

        ((RadioButton)findViewById(R.id.rbItem)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cbPackets.setChecked(!isChecked);
                updateSumTextView();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(document instanceof  OrderImplEx) {
            outState.putBoolean(BONUS, ((OrderImplEx)document).isBonusMode());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(document instanceof OrderImplEx) {
            OrderImplEx doc = (OrderImplEx)document;

            OrderItemEx oie = (OrderItemEx) doc.findItem(price.getData().id);
            if(oie != null) {
                doc.setBonusMode(oie.bonus != 0);
            } else {
                doc.setBonusMode(getIntent().getBooleanExtra(BONUS, false));
            }

            ((CheckBox)findViewById(R.id.cbBonus)).setChecked(doc.isBonusMode());
        } else {
            findViewById(R.id.trBonus).setVisibility(View.GONE);
        }

        if(cbPackets.isChecked()) {
            ((RadioButton)findViewById(R.id.rbPack)).setChecked(true);
        } else {
            ((RadioButton)findViewById(R.id.rbItem)).setChecked(true);
        }
    }

//    @Override
//    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
////        ((OrderItemEx)item).bonus = ((CheckBox)findViewById(R.id.cbBonus)).isChecked() ? 1 : 0;
//    }
}
