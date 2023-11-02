package com.grsoft.napoleon.documents;

import com.grsoft.util.DatePeriod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllDocList extends DocList {
    List<DocWType> docs = new ArrayList<DocWType>();

    public AllDocList(String orgId, DatePeriod dp, boolean onlyCreatable) {
        Set<String> links = new HashSet<>();
        if(!onlyCreatable) {
            DocTypeBase sdt = SenegInputDoc.instance();
            DocList dl  = sdt.docList(orgId, "", dp);
            for(Document<?> d : dl) {
                com.grsoft.dataobjects.SenegInputDoc sid = (com.grsoft.dataobjects.SenegInputDoc) d.getData();
                links.add(sid.link);
                Document<?> src = sdt.create();
                src.read(d.getRowid());
                DocWType el = new DocWType(src, sdt);
                docs.add(el);
            }
            dl.close();
        }

        for(DocTypeBase dt : DocType.docTypes) {
            if(dt.isCreatable()) {
                DocList dl = dt.docList(orgId, "", dp);
                Field linkField = dt.create().getData().getField("link");
                for(Document<?> d : dl) {
                    Document<?> src = dt.create();
                    src.read(d.getRowid());
                    if(linkField != null) {
                        try {
                            String link = (String)linkField.get(src.getData());
                            if(links.contains(link))
                                continue;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    DocWType el = new DocWType((CreatableDocument<?>) src, dt);
                    docs.add(el);
                }
                dl.close();
            }
        }

        Collections.sort(docs);
    }

    @Override public Document<?> get(int index) { return docs.get(index); }
    @Override public int getCount() {  return docs.size(); }
}
