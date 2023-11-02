package com.novotek.dataobjects;

import com.novotek.dataobjects.priceTree.PriceTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Partner extends PartnerSrc {
    PriceTree price = new PriceTree();

    public Partner() {}

    public Partner(PartnerSrc src) {
        id = src.id;
        address = src.address;
        name = src.name;
        position = src.position;
        phone = src.phone;
        payment = src.payment;
    }

    List<Order> orders = new ArrayList<>();

    public List<Brand> brands(int maxBrands) {
        List<Brand> brands = new ArrayList<>();
        for(NameObj no : price.brands.keySet()) {
            Brand b = ProjectData.brands.get(no);
            if(b == null)
                continue;

            brands.add(b);
            if(maxBrands > 0 && brands.size() >= maxBrands)
                break;
        }

        Collections.sort(brands);
        return brands;
    }

    public Basket basket = new Basket();

    public String toText() {
        return toText(false);
    }

    public String toText(boolean oneLine) {
        if(name.length() == 0)
            return address;
        String text = "<b>" + name + "</b> ";
        if(!oneLine)
            text += "<br/>";
        text += address;
        return  text;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() { return orders; }

    public void setPrice(PriceTree newPrice)  {
        price = newPrice;
    }

    public PriceTree getPrice() { return price; }

    public Order getOrder(String uid) {
//        for(Order o : orders)
//            if(o.uid.equals(uid))
//                return o;
//
        return new Order();
    }
}
