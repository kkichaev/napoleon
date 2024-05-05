package com.novotek.dataobjects;

import com.novotek.dataobjects.priceTree.PriceTree;
import com.novotek.dataobjects.xml.Alias;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Partner {
    public String id = "";
    public String address = "";
    public String name = "";
    public String position = "";
    public String phone = "";
    public String payment = "";

    public List<Action> actions = new ArrayList<>();
    public List<Order> orders = new ArrayList<>();

    @Alias(name="IsAgreement") public boolean haveAgreement = false;

    @Alias(name="products_info_guid") public String product_info = "";

//    PriceTree price = new PriceTree();

    public Partner() {}

    ProductInfo prodInfo = null;
    ProductInfo getProductInfo() {
        if(prodInfo == null) {
            prodInfo = ProjectData.getProductInfo(product_info);
            if(prodInfo == null) {
                prodInfo = new ProductInfo();
            }
        }
        return prodInfo;
    }

    public List<Brand> brands(int maxBrands) {
        List<Brand> brands = new ArrayList<>();

        ProductInfo pi = getProductInfo();
        for (NameObj no : getPrice().brands.keySet()) {
            Brand b = pi.getBrand(no);
            if (b == null)
                continue;

            brands.add(b);
            if (maxBrands > 0 && brands.size() >= maxBrands)
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

//    public void setPrice(PriceTree newPrice)  {
//        price = newPrice;
//    }

    PriceTree priceTree = null;
    public PriceTree getPrice() {
        if(priceTree == null)
            priceTree = getProductInfo().getPrice(filterActions());
        return priceTree;
    }

    List<Action> filterActions() {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.add(Calendar.DAY_OF_YEAR, -1);

        List<Action> res = new ArrayList<>();

        for(Action a : actions) {
            if(a.isActive(c.getTime()))
                res.add(a);
        }
        return res;
    }

    public Order getOrder(String uid) {
//        for(Order o : orders)
//            if(o.uid.equals(uid))
//                return o;
//
        return new Order();
    }
}
