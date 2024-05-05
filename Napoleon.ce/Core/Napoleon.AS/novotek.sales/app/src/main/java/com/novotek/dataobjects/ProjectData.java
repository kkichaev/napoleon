package com.novotek.dataobjects;


import com.novotek.dataobjects.priceTree.PriceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectData {
    static List<Partner> partners = new ArrayList<>();
    static Partner current = null;

    static List<ProductInfo> products = new ArrayList<>();
    public static ProductInfo getProductInfo(String guid) {
        for(ProductInfo pi : products) {
            if(pi.guid.equals(guid)) {
                return pi;
            }
        }
        return null;
    }

//    public static Map<NameObj, Brand> brands = new HashMap<>();
    public static CommonInfo commonInfo = new CommonInfo();

    public static List<Partner> partners() { return partners;}

//    public static void setPartners(List<Partner> newPartners) {
//        current = null;
//        partners = newPartners;
//    }

    public static void updateFrom(DataRcv data) {
        for(Partner p : data.orgs) {
            boolean finded = false;
            for(Partner pi : partners) {
                if(p.id.equals(pi.id)) {
                    finded = true;
                    break;
                }
            }

            if(!finded) {
                partners.add(p);
            }
        }
//        partners = data.orgs;
        products = data.products_info;
        commonInfo = data.common_info;

//        current = data.orgs.size() > 0 ? data.orgs.get(0) : null;
    }

    public static Price findBarcode(String bc, Partner current) {
        if(current != null) {
            PriceTree pt = current.getPrice();
            return pt.findBarcode(bc);
        }
        return null;
    }

    public static Partner getCurrent() { return current; }
    public static void setCurrent(Partner p) {current = p;}
}
