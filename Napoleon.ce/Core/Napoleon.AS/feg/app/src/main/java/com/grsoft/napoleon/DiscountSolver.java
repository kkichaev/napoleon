package com.grsoft.napoleon;

import com.grsoft.dataobjects.DiscountItem;
import com.grsoft.dataobjects.Discounts;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscountSolver {
    int discount = 0;

    boolean include = true;
    Set<String> items = new HashSet<>();
    Set<Integer> folders = new HashSet<>();

    public int getCost(int cost, Price item) {
        boolean inset = (items.contains(item.id) || folders.contains(item.folderID));
        if(inset && include || (!inset && !include)) {
            return CostStrategy.costWithDiscount(cost, discount, Consts.SUM_SCALE);
        }
        return cost;
    }

    DiscountSolver(Discounts src, FolderTree tree) {
        discount = src.discount;
        include = src.type == Discounts.DSC_TYPE_IN_HLIST;

        for(DiscountItem item : src.items) {
            if(item.isFolder == 0) {
                items.add(item.id);
            } else {
                addWithDescendants(item.id, tree);
            }
        }
    }

    private void addWithDescendants(String id, FolderTree tree) {
        List<Folder> fl = tree.getWithDescendats(id);
        for(Folder f : fl) {
            folders.add(f.id);
        }
    }

    public static DiscountSolver create(String id, FolderTree tree) {
        DiscountSolver ret = null;
        DiscountImpl di = new DiscountImpl();
        if(di.read("id", id)) {
            ret = new DiscountSolver(di.getData(), tree);
        }

        return ret;
    }
}
