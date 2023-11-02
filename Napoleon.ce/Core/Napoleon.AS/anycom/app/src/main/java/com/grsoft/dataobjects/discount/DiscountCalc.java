package com.grsoft.dataobjects.discount;

import android.util.Log;

import androidx.annotation.NonNull;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderCard;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiscountCalc {
    static final String TAG = "DiscountCalc";

    String orgId = "";
    String storeId = "";
    Set<String> cards =  new HashSet<>();
    Date docDate = new Date();

    Map<Object, DiscountTreeCalc> dscTree = null;
    public Map<String, Integer> orgPrice = new HashMap<>();
    public Map<String, List<DiscountCalcElement>> priceElements = new HashMap<>();
    public Map<Integer, List<DiscountCalcElement>> folderElements = new HashMap<>();

    public DiscountCalc() {
        loadDiscountTree();
    }

    public void loadDiscountTree() {
        dscTree = DbReader.fetchDic(DiscountTreeCalc.class, "id");
    }

    public void load(OrgEx org, OrderEx doc) {
        boolean needRefresh = false;
        if(!orgId.equals(org.id)) {
            orgId = org.id;
            storeId = "";
            needRefresh = true;
        }
        if(needRefresh || !storeId.equals(doc.whCode)) {
            storeId = doc.whCode;
            needRefresh = true;
        }

        Date dd = org.getDiscountDate(doc);
        if(needRefresh || !docDate.equals(dd)) {
            docDate = dd;
            needRefresh = true;
        }

        cards = new HashSet<>();
        for(OrderCard oc : doc.cards) {
            cards.add(oc.number);
        }

        if(needRefresh) {
            loadElements(org, doc);
            orgPrice = OrgPrice.load(org, doc);
        }
    }

    void loadElements(OrgEx org, OrderEx doc) {
        Map<Object, PriceEx> prc = DbReader.fetchDic(PriceEx.class, "id");
        Set<String> folders = new HashSet<>();
        FolderTree ft = CostStrategy.getFolders();
        for(PriceEx pe : prc.values()) {
            for(Folder f : ft.getWithDescendats(pe.fid)) {
                folders.add(f.fid);
            }
        }

        priceElements.clear();
        folderElements.clear();

        List<DiscountLoad> items = DiscountLoad.load(org, doc);

        for(DiscountLoad dli : items) {
            for (DiscountItem di : dli.items) {
                if (di.type == DiscountItem.TYPE_ITEM) {
                    if(prc.containsKey(di.id)) {
                        List<DiscountCalcElement> els = priceElements.get(di.id);
                        if (els == null) {
                            els = new ArrayList<>();
                            priceElements.put(di.id, els);
                        }
                        DiscountCalcElement dce = new DiscountCalcElement(dli);
                        if(!els.contains(dce))
                            els.add(dce);
                    }
                } else {
                    if(folders.contains(di.id)) {
                        for (Folder f : ft.getWithDescendats(di.id)) {
                            List<DiscountCalcElement> els = folderElements.get(f.id);
                            if (els == null) {
                                els = new ArrayList<>();
                                folderElements.put(f.id, els);
                            }
                            DiscountCalcElement dce = new DiscountCalcElement(dli);
                            if(!els.contains(dce))
                                els.add(dce);
                        }
                    }
                }
            }
        }
    }


    public int calc(Price p, int cost) {

        List<DiscountCalcElement> els = getDiscountCalcElements(p);

        if(els.size() > 0) {
            DiscountCalcElement el = calcDiscount(els);
            if(el != null) {
                if(el.orgCost == DiscountElement.TYPE_ORG_DISCOUNT) {
                    cost = (int)CostStrategy.costWithDiscount(cost, el.discount, DiscountCalcElement.DISCOUNT_SCALE);
                } else {
                    Integer pc = orgPrice.get(p.id);
                    if(pc != null)
                        cost = pc;
                }
            }
        }

        return cost;
    }

    @NonNull
    private List<DiscountCalcElement> getDiscountCalcElements(Price p) {
        List<DiscountCalcElement> els = new ArrayList<>();
        List<DiscountCalcElement> del = priceElements.get(p.id);
        if(del != null) {
            els.addAll(del);
        }

        del = folderElements.get(p.folderID);
        if(del != null) {
            for(DiscountCalcElement di : del) {
                if(!els.contains(di))
                    els.add(di);
            }
        }
        return els;
    }

    DiscountTreeCalc upToRoot(String parentId, Map<String, DiscountTreeCalc> tree) {
        DiscountTreeCalc cur = null;
        while (true) {
            DiscountTreeCalc el = tree.get(parentId);
            if(el == null) {
                el = dscTree.get(parentId);
                if(el == null) // find root
                    return cur;

                el.clear();
                if(cur != null)
                    el.add(cur);

                tree.put(parentId, el);
                cur = el;
                parentId = el.parent;
            } else {
                if(cur != null)
                    el.add(cur);
                return null;
            }
        }
    }

    private DiscountCalcElement calcDiscount(List<DiscountCalcElement> els) {
        Map<String, DiscountTreeCalc> tree = new HashMap<>();

        DiscountTreeCalc root = null;
        List<DiscountCalcElement> src = new ArrayList<>();

        for(DiscountCalcElement el : els) {
            if(el.cardNumber.length() > 0 && !cards.contains(el.cardNumber))
                continue;

            src.add(el);
            DiscountTreeCalc newRoot = upToRoot(el.parent, tree);
            if(newRoot != null) {
                if(root == null) {
                    root = newRoot;
                    root.parent = null;
                } else {
                    Log.d(TAG, "Second root ");
                    continue;
                }
            } else {
                if(root == null) {
                    Log.d(TAG, "Missing root ");
                    continue;
                }
            }
            tree.get(el.parent).add(el);
        }

        return root == null ? null : root.calc();
    }

    public List<KeyValue> clientCards() {
        Map<String, KeyValue> ret = new HashMap<>();
        for(List<DiscountCalcElement> list : priceElements.values()) {
            for(DiscountCalcElement dce : list) {
                if(dce.cardNumber.length() > 0 && !ret.containsKey(dce.cardNumber)) {
                    ret.put(dce.cardNumber, new KeyValue(dce.cardNumber, dce.cardName));
                }
            }
        }

        for(List<DiscountCalcElement> list : folderElements.values()) {
            for(DiscountCalcElement dce : list) {
                if(dce.cardNumber.length() > 0 && !ret.containsKey(dce.cardNumber)) {
                    ret.put(dce.cardNumber, new KeyValue(dce.cardNumber, dce.cardName));
                }
            }
        }
        return new ArrayList(ret.values());
    }
}
