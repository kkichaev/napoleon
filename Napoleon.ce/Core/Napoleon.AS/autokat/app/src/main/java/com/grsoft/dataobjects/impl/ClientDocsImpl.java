package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.ClientDocs;
import com.grsoft.dataobjects.ClientDocsItem;
import com.grsoft.napoleon.documents.CreatableDocument;

import java.util.UUID;

public class ClientDocsImpl extends CreatableDocument<ClientDocs> {
    @Override
    public void open(Context context) {

    }

    public void addItem(String type, String file){
        ClientDocsItem item = findItem(type);

        if (item == null){
            item = new ClientDocsItem();
            item.type = type;
            data.items.add(item);
        }

        item.file = file.getBytes();
    }

    private ClientDocsItem findItem(String type) {
        ClientDocsItem res = null;
        for(ClientDocsItem i : data.items)
            if (i.type.equals(type)) {
                res = i;
                break;
            }
        return res;
    }

    public String getFile(String type){
        ClientDocsItem item = findItem(type);

        if (item == null)
            return String.format("%s.pdf", UUID.randomUUID().toString().replace("-",""));

        return new String(item.file);
    }
}
