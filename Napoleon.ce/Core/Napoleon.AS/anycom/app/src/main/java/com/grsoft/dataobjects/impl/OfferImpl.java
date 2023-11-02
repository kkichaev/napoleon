package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Offer;
import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.OfferEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.FolderTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OfferImpl extends CreatableDocument<Offer> {
    @Override
    public void open(Context context) {
        OfferEdit.open(context, this);
    }

    @Override
    public void postInit() {
        super.postInit();

        OrgImpl oi = new OrgImpl();
        oi.read("id", data.id);
        data.email = ((OrgEx)oi.getData()).email;
    }

    public boolean contains(String fid) {
        for(OfferItem oi : data.items) {
            if(oi.id.equals(fid))
                return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return data.items.size() == 0  || data.email.length() == 0 || super.isEmpty();
    }

    public void toggle(Folder src) {
        if(!isEditable())
            return;

        boolean finded = false;
        Map<String, OfferItem> used = new HashMap<>();
        for (OfferItem oi : data.items) {
            used.put(oi.id, oi);
            if(oi.id.equals(src.fid))
                finded = true;
        }

        FolderTree ft = CostStrategy.getFolders();
        List<Folder> fsrc = ft.getWithDescendats(src.fid);

        if(!finded) {
            for(Folder f : fsrc) {
                if(!used.containsKey(f.fid)) {
                    OfferItem ofi = new OfferItem();
                    ofi.id = f.fid;
                    data.items.add(ofi);
                }
            }
        } else {
            for(Folder f : fsrc) {
                OfferItem ofi = used.get(f.fid);
                if(ofi != null) {
                    data.items.remove(ofi);
                }
            }
        }
        write();
    }
}
