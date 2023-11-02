package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DanaAction;
import com.grsoft.dataobjects.DanaActionItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DanaActionImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
    int priceCost = 0;
    OrgEx org;
    Map<Object, DanaAction> macts;
    Map<CheckBox, DanaAction> actions = new HashMap<>();

    @Override protected int getContentViewId() { return R.layout.pricecountex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(document instanceof OrderImpl)
            ((OrderImpl)document).setUpdateQtyHandler(this);
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(document instanceof OrderImpl) {
            if( org == null) {
                OrgImpl oi = new OrgImpl();
                oi.read("id", document.getId());
                org = (OrgEx) oi.getData();
            }
            OrderEx oe = (OrderEx) document.getData();
            Price p = price.getData();

            priceCost = CostStrategy.defaultInstance.getItemCost(p, document);
            OrderItem oi = (OrderItem) ((OrderImpl)document).findItem(p.id);
            if(oi != null && oi.cost != priceVal) {
                onChangeCost(oi.cost);
            }

            LinearLayout ll = findViewById(R.id.llActions);
            ll.removeAllViews();
            actions.clear();

            macts = DbReader.fetchDic(DanaAction.class, "id");
            List<String> used = new ArrayList<>();
            for(OrderAction oa : oe.actions) {
                if(oa.items.contains(p.id)) {
                    DanaAction action = macts.get(oa.id);
                    if(action != null) {
                        used.add(oa.id);
                        addAction(ll, action, true);
                    }
                }
            }

            for(DanaAction da : DanaAction.active(true, org)) {
                if(used.contains(da.id)) continue;

                for(DanaActionItem dai : da.items) {
                    if(dai.id.equals(p.id)) {
                        addAction(ll, da, false);
                        break;
                    }
                }
            }
        }
    }

    private void addAction(LinearLayout parent, final DanaAction da, boolean checked) {
        View v = View.inflate(this, R.layout.action_card, null);

        CheckBox cb = v.findViewById(R.id.cbAction);
        cb.setChecked(checked);
        cb.setEnabled(document.isEditable());

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int qty = countWithPack();
                if(b) {
                    if(da.canApply((OrderEx) document.getData(), price.getData(), qty)) {
                        if(da.type == DanaAction.DISCOUNT_TYPE) {
                            int newCost = CostStrategy.costWithDiscount(priceCost, da.discount, Consts.SUM_SCALE);
                            onChangeCost(newCost);
                        }
                    } else {
                        compoundButton.setChecked(false);
                        Toast.makeText(compoundButton.getContext(), R.string.action_not_match_conditions, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(da.type == DanaAction.DISCOUNT_TYPE) {
                        onChangeCost(priceCost);
                    }
                }
            }
        });

        TextView tv = v.findViewById(R.id.tvDescr);
        tv.setText(da.descr);

        parent.addView(v);
        actions.put(cb, da);
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        ((OrderItemEx)item).costWOD = priceCost;
    }

    @Override
    protected boolean updateOrder() {
        boolean ret = super.updateOrder();

        if(document instanceof OrderImpl) {
            OrderEx o = (OrderEx) document.getData();
            List<String> used = new ArrayList<>();
            List<DanaAction> undoing = new ArrayList<>();
            for(Map.Entry<CheckBox, DanaAction> kv : actions.entrySet()) {
                boolean added = false;
                DanaAction da = kv.getValue();
                if(kv.getKey().isChecked()) {
                    used.add(da.id);
                    added = da.applyAction(o);
                }
                if(!added) {
                    //da.undo(o);
                    undoing.add(da);
                }
            }

            undoing.addAll(o.checkActions(used, macts));
            for(DanaAction da : undoing) {
                da.undo(o);
            }
            document.write();
        }
        return ret;
    }
}
