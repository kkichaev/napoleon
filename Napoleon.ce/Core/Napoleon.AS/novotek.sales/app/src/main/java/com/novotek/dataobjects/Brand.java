package com.novotek.dataobjects;

public class Brand implements Comparable<Brand> {
    public NameObj name = new NameObj();
    public String url = "";

    @Override
    public String toString() {
        return name.name_ru;
    }

    @Override
    public int compareTo(Brand contract) {
        return toString().compareTo(contract.toString());
    }
}
