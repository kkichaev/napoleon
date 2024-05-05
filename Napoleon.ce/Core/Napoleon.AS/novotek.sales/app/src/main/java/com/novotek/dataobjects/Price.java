package com.novotek.dataobjects;


import java.util.ArrayList;
import java.util.List;

public class Price implements Comparable<Price> {
    public String name = "";
    public String description = "";
    public String artikul = "";

    public float weight = 0;

    public NameObj filterType = new NameObj();
    public NameObj category = new NameObj();
    public NameObj subCategory = new NameObj();
    public NameObj brand = new NameObj();

    public String composition = "";
    public String id = "";

    public List<String> url = new ArrayList<>();
    public float qty = 0;
    public float price = 0;

    public int expirationDate = 0;

    public List<String> barcode = new ArrayList<>();

    public boolean haveBC(String bc) {
        for(String b : barcode) {
            if(b.equals(bc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Price price) {
        return name.compareTo(price.name);
    }
}
