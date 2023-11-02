package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import java.util.HashSet;

public class WarehouseEx extends Warehouse  {
    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter.resetCache();
        if (DocType.getCurDoc() == ReturnDoc.instance()) {
            return new ReturnAdapter(this);
        }
        return new FoldersAdapter(this);
    }

    class ReturnAdapter extends FoldersAdapter {

        HashSet<String> ids = new HashSet<String>();

        public ReturnAdapter(WarehouseManager warehouse) {
            super(warehouse);

            String orgId = document.getId();
            com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
            for(Document<?> d : dl) {
                for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
                    ids.add(di.id);
            }

            dl.close();
        }

        @Override public boolean inset(long rowid, String id) { return ids.contains(id); }
    }
}
