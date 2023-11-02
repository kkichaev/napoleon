package com.serviko.dataobjects.actionTree;

import android.view.View;
import android.widget.TextView;

import com.serviko.dataobjects.Price;
import com.serviko.sales.ActionRules;
import com.serviko.sales.R;
import com.serviko.view.treeview.InMemoryTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionDef extends InMemoryTreeNode implements Comparable<ActionDef> {
    public ActionCondition action;

    int range;

    List<ActionClause> clauses = new ArrayList<>();

    public boolean isGift = false;

    public String getName() {
        return action.name;
    }
    public String getId() { return action.id; }

    public void expand(boolean expand) {
        for(InMemoryTreeNode ch : getChildren()) {
            ch.setVisible(expand);
        }
    }

    public int title() {
        return isGift ? R.string.gift : R.string.action;
    }

    public String text() {
        return action.name;
    }

    public List<ActionClause> getClauses() { return clauses; }

    public boolean isAdditiveAction() { return action.combineType.compareToIgnoreCase("ÈËÈ") == 0; }
    public boolean isGood() { return getChildren().size() > 0; }

    @Override public int getLayoutID() { return R.layout.action_def; }

    @Override
    public void updateView(final View view, boolean expanded) {
        TextView tv = view.findViewById(R.id.tvName);
        tv.setText(action.name);

        View v = view.findViewById(R.id.btnCondition);
        v.setVisibility(expanded ? View.VISIBLE : View.GONE);

        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { ActionRules.open(view.getContext(), action.id); }
        });
    }

    private void addItem(Price p, ActionRule rule) {
        if(p != null && !contains(p)) {
            if(range > rule.range) {
                range = rule.range;
            }
            if(rule.isGift)
                isGift = true;

            add(new ActionGoods(p, rule.discount));
            if(p.discount < rule.discount || p.action == null) {
                p.discount = rule.discount;
                p.action = this;
            }
        }
    }

    boolean contains(Price p) {
        for(InMemoryTreeNode el : getChildren()) {
            if( ((ActionGoods)el).getItem().id.equals(p.id) )
                return true;
        }

        return false;
    }

    static Map<String, Price> convertPrice(List<Price> newPrice) {
        Map<String, Price> price = new HashMap<>();
        for(Price p : newPrice)
            price.put(p.id, p);

        return price;
    }

    public static List<ActionDef> create(List<Price> newPrice, List<ActionRule> actions, List<ActionCondition> actionConditions) {
        Map<String, Price> price = convertPrice(newPrice);
        Map<String, ActionDef> actionMap = new HashMap<>();
        Map<String, ActionData> conditionMap = convertConditions(actionConditions);

        for(ActionRule ar : actions) {
            Price p = price.get(ar.idPrice);
            if(p == null)
                continue;

            ActionData ad = conditionMap.get(ar.idCondition);
            if(ad == null)
                continue;

            ActionDef def = actionMap.get(ar.idCondition);
            if(def == null) {
                def = new ActionDef(ar, ad.root, ad.items, price);
                if(!def.isGood())
                    continue;
                actionMap.put(ar.idCondition, def);
            } else {
                def.addItem(p, ar);
            }
        }

        List<ActionDef> ret = new ArrayList<>(actionMap.values());
        Collections.sort(ret);
        return ret;
    }

    private static Map<String, ActionData> convertConditions(List<ActionCondition> actionContidions) {
        Map<String, ActionData> ret = new HashMap<>();
        for (ActionCondition i : actionContidions) {
            if(i.isFolder) {
                ActionData ad = ret.get(i.id);
                if(ad == null) {
                    ad = new ActionData();
                    ret.put(i.id, ad);
                }
                ad.root = i;
            } else {
                ActionData ad = ret.get(i.parent);
                if(ad == null) {
                    ad = new ActionData();
                    ret.put(i.parent, ad);
                }
                ad.items.add(i);
            }
        }

        return ret;
    }

    @Override
    public int compareTo(ActionDef actionDef) {
        return range - actionDef.range;
    }

    static class ActionData {
        public ActionCondition root;
        public List<ActionCondition> items = new ArrayList<>();
    }

    protected ActionDef(ActionRule rule, ActionCondition action, List<ActionCondition> items, Map<String, Price> price) {
        super(true);

        this.action = action;
        this.addItem(price.get(rule.idPrice), rule);

        for(ActionCondition ac : items) {
            ActionClause item = new ActionClause(ac, price);
            if(item.isGood())
                clauses.add(item);
            else if(!isAdditiveAction()) {
                clearChildren();
                break;
            }
        }
    }

    protected ActionDef() { super(true); }
}
