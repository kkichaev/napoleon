package com.novotek.dataobjects.priceTree;

import com.novotek.dataobjects.Action;
import com.novotek.dataobjects.Brand;
import com.novotek.dataobjects.Catalog;
import com.novotek.dataobjects.NameObj;
import com.novotek.dataobjects.Price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceTree {
    public List<FolderSrc> folders = new ArrayList<>();

    public Map<NameObj, List<Price>> brands = new HashMap<>();
    public Map<String, Price> price = new HashMap<>();
    public Map<Action, List<Price>> actions = new HashMap<>();

    public static PriceTree load(List<Price> price, List<Catalog> catalog, List<Action> actions) {
        Map<NameObj, FolderSrc> folders = new HashMap<>();
        Map<String, Price> priceMap = new HashMap<>();
        Map<NameObj, List<Price>> brands = new HashMap<>();
        Map<Action, List<Price>> actionsMap = new HashMap<>();

        for(Catalog c : catalog) {
            FolderSrc f = new FolderSrc(c);
            for(Catalog ch : c.children) {
                SubFolder sf = new SubFolder(ch);
                f.folders.add(sf);
            }
            folders.put(f.name, f);
        }

        for(Price p : price) {
            priceMap.put(p.id, p);

            List<Price> ba = brands.get(p.brand);
            if(ba == null) {
                ba = new ArrayList<>();
                brands.put(p.brand, ba);
            }
            ba.add(p);

            NameObj f1 = p.subCategory;
            NameObj f2 = p.category;
            FolderSrc f = folders.get(f1);
            if(f == null) continue;
            for(SubFolder sf : f.folders) {
                if(sf.name.equals(f2)) {
                    sf.add(p);
                    break;
                }
            }
        }

        for(Action a : actions) {
            List<Price> ap = new ArrayList<>();
            for(String id : a.items) {
                Price p = priceMap.get(id);
                if( p != null ) {
                    ap.add(p);
                }
            }
            if(ap.size() > 0) {
                actionsMap.put(a, ap);
            }
        }

        List<FolderSrc> srcF = new ArrayList<>(folders.values());
        List<FolderSrc> rmv = new ArrayList<>();
        for(FolderSrc f : srcF) {
            List<SubFolder> rsf = new ArrayList<>();
            for(SubFolder sf : f.folders) {
                if(sf.items.size() == 0) {
                    rsf.add(sf);
                } else {
                    Collections.sort(sf.items);
                }
            }
            f.folders.removeAll(rsf);
            if(f.folders.size() == 0){
                rmv.add(f);
            } else {
                Collections.sort(f.folders);
            }
        }
        srcF.removeAll(rmv);
        Collections.sort(srcF);

        PriceTree ret = new PriceTree();
        ret.folders.addAll(srcF);
        ret.price = priceMap;
        ret.brands = brands;
        ret.actions = actionsMap;

        return ret;
    }

    public Price findBarcode(String bc) {
        for(Price c : price.values()) {
            if(c.haveBC(bc)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<String> products(Brand b) {
        ArrayList<String> dest = new ArrayList<>();
        List<Price> src = brands.get(b.name);
        for(Price p : src) {
            dest.add(p.id);
        }
        return dest;
    }

    public Price get(String id) {
        return price.get(id);
    }

    public FolderSrc find(String en_name) {
        for(FolderSrc f : folders) {
            if(f.name.name_en.equals(en_name)) {
                return f;
            }
        }
        return null;
    }

    public SubFolder find(String parent, String folder) {
        FolderSrc f = find(parent);
        if(f == null)
            return  null;

        for(SubFolder sf : f.folders) {
            if(sf.name.name_en.equals(folder))
                return sf;
        }

        return null;
    }

    public ArrayList<String> allProducts() {
        ArrayList<String> ret = new ArrayList<>(price.keySet());
        return ret;
    }
}
