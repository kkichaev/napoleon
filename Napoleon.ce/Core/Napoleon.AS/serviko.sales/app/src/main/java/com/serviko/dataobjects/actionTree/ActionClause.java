package com.serviko.dataobjects.actionTree;

import android.view.View;
import android.widget.TextView;

import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.BasketItem;
import com.serviko.dataobjects.Price;
import com.serviko.sales.R;
import com.serviko.view.treeview.InMemoryTreeNode;

import java.util.Map;

public class ActionClause extends InMemoryTreeNode {
    ActionCondition action;

    protected ActionClause(ActionCondition c, Map<String, Price> price) {
        super(true);

        this.action = c;

        for(String id : c.items) {
            Price p = price.get(id);
            if(p != null) {
                ActionPrice item = new ActionPrice(p);
                add(item);
            }
        }
    }

    public boolean isGood() {
        return getChildren().size() != 0;
    }

    @Override public int getLayoutID() { return R.layout.action_clause; }

    @Override
    public void updateView(View view, boolean expanded) {
        TextView tv = view.findViewById(R.id.tvName);
        tv.setText(action.name);
    }

    public void addToBasket(Basket basket) {
//        for(InMemoryTreeNode i : getChildren()) {
//            Price p = ((ActionPrice)i).getPrice();
//            if(p != null) {
//                BasketItem bi = basket.find(p);
//                if(bi == null)
//                    basket.add(p);
//            }
//        }
    }
}
