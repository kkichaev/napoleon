package com.grsoft.napoleon;

import android.graphics.Color;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.documents.Document;

public class DocListEx extends DocList {
    @Override
    protected int getDocColor(Document<?> doc) {
        Object order = doc.getData();
        if(order instanceof OrderEx) {
            return
                    ((OrderEx) order).fromKIS > 0 ? getResources().getColor(R.color.item_highlight) : Color.BLACK;
        }
        return super.getDocColor(doc);
    }
}
