package com.serviko.sales.main_views.price_filter;

import com.serviko.dataobjects.Price;
import com.serviko.dataobjects.priceTree.TreeElement;
import com.serviko.sales.main_views.Filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PriceFilter extends Filter {

    public boolean actionGoods = false;

    public boolean v01 = false;
    public boolean v25 = false;
    public boolean v33 = false;
    public boolean v5 = false;
    public boolean v7 = false;
    public boolean v1 = false;
    public boolean v15 = false;
    public boolean v2 = false;
    public boolean v29 = false;

    public String searchText = "";

    public Set<String> manufacturer = new HashSet<>();

    public boolean inSet(TreeElement te) {
        Price p = te.item;

        if(searchText.length() > 0 && !p.name.toUpperCase().contains(searchText)) return false;

        if(manufacturer.size() > 0 && !manufacturer.contains(p.suplyer))
            return false;

        if(actionGoods && p.action == null)
            return false;

        boolean checkVolume = (v01 | v25 | v33 | v5 | v7 | v1 | v15 | v2 | v29);
        if(!checkVolume)
            return true;

        /*
 <= 0.1  : 100 мл
>0.1 и <0.3 : 250мл
>=0.3 и < 0.4 : 330мл
>=0.4 и <= 0.5 : 0,5л
>0.5 и < 0.8 : 0,7л
>=0.8 и <= 1 : 1л
>1 и <= 1.8 : 1,5л
>1.8 и <= 2 : 2л
 >2 : более 2л
         */
        if(v01 && p.volume <= 0.1001) return true;
        if(v25 && p.volume > 0.1001 && p.volume < 0.3) return true;
        if(v33 && p.volume >= 0.3 && p.volume < 0.39999) return true;
        if(v5 && p.volume >= 0.3999 && p.volume <= 0.5001) return true;
        if(v7 && p.volume > 0.5001 && p.volume < 0.79999) return true;
        if(v1 && p.volume >= 0.7999 && p.volume <= 1.0001) return true;
        if(v15 && p.volume > 1.0001 && p.volume <= 1.8001) return true;
        if(v2 && p.volume > 1.8001 && p.volume <= 2.0001) return true;
        if(v29 && p.volume > 2.0001) return true;

        return false;
    }
}
