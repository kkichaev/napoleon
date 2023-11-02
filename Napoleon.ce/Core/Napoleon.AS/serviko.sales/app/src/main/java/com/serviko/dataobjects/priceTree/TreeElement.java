package com.serviko.dataobjects.priceTree;


import com.serviko.dataobjects.Price;

public class TreeElement implements Comparable<TreeElement> {
    public Price item;

    public TreeElement(Price src) { item = src;}

    @Override
    public int compareTo(TreeElement o) {
        return item.name.compareTo(o.item.name);
    }
}
