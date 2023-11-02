package com.novotek.dataobjects.priceTree;

import com.novotek.dataobjects.Price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FolderOld extends TreeElement {
    public List<FolderOld> childs = new ArrayList<>();
    public List<TreeElement> items = new ArrayList<>();

    public FolderOld(Price src) {
        super(src);
    }

    public FolderOld() {
        super(new Price());
    }

    public int size() { return childs.size() + items.size(); }

    public void setSrc(Price src) { this.item = src; }

    public FolderOld add(FolderOld f) {
//        for(Folder cf : childs) {
//            if(cf.item.code.equals(f.item.code)) {
//                return cf;
//            }
//        }
//        childs.add(f);
        return f;
    }
    public void add(Price i) {
        TreeElement te = new TreeElement(i);
        items.add(te);
    }

    public TreeElement get(int position) {
        if(position < childs.size()) return childs.get(position);
        return items.get(position - childs.size());
    }

    public void sort() {
        Collections.sort(childs);
        Collections.sort(items);
    }

    public void findItems(List<TreeElement> dest, Pattern pattern) {
        for(TreeElement el : items) {
            Matcher matcher = pattern.matcher(el.item.name);
            if(matcher.find())
                dest.add(el);
        }
        for(FolderOld f : childs)
            f.findItems(dest, pattern);
    }

    public FolderOld findFolder(String srchRoot) {
        if(item.id.equals(srchRoot))
            return this;
        for(FolderOld f : childs) {
            FolderOld fnd = f.findFolder(srchRoot);
            if(fnd != null)
                return fnd;
        }

        return null;
    }

    public Price findItem(String id) {
        for(TreeElement i : items) {
            if(i.item.id.equals(id))
                return i.item;
        }
        for(FolderOld f : childs) {
            Price p = f.findItem(id);
            if(p != null)
                return p;
        }

        return null;
    }
}
