package com.novotek.dataobjects.priceTree;


import com.novotek.dataobjects.Price;

public class TreeElement implements Comparable<TreeElement> {
    public Price item;

    public TreeElement(Price src) { item = src;}

    @Override
    public int compareTo(TreeElement o) {
        return item.name.compareTo(o.item.name);
    }
}
