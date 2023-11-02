package com.grsoft.napoleon.documents;

import android.content.Context;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.SenegInputDoc;
import com.grsoft.dataobjects.SenegOutputDoc;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.SenegInputDocImpl;
import com.grsoft.dataobjects.impl.SenegOutputDocImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.util.Util;

import java.util.Date;

public class DocWType extends Document<DocDataObject> implements  Comparable<DocWType> {
    Document<?> src;
    DocTypeBase docType;

    public DocWType(Document<?> src, DocTypeBase type) {
        this.src = src;
        this.docType = type;
    }

    public boolean canAccept() { return src instanceof SenegInputDocImpl; }
    public boolean isEditable() {return src instanceof SenegInputDocImpl && ((SenegInputDocImpl)src).isEditable(); }

    public String docType() {
        if(src instanceof SenegOutputDocImpl)
            return SenegInputDoc.getType(((SenegOutputDoc)src.getData()).docType);
        return docType.name;
    }

    public int weight() {
        if(src instanceof OrderImpl)
            return ((OrderImpl)src).weight();
        if(src instanceof SenegInputDocImpl)
            return ((SenegInputDocImpl)src).weight();
        return 0;
    }

    public String docDescription() {
        if(src instanceof SenegInputDocImpl) {
            return ((SenegInputDocImpl)src).description();
        }
        return docType.getName();
    }

    public boolean accepted() {
        if(src instanceof SenegInputDocImpl)
            return ((SenegInputDocImpl)src).accepted();
        return  false;
    }

    public CreatableDocument<?> getSource() {
        if(src instanceof OrderImplBase)
            return (OrderImplBase) src;
        if(src instanceof SenegInputDocImpl)
            return ((SenegInputDocImpl)src).getLinkedDoc();
        if(src instanceof SenegOutputDocImpl)
            return (SenegOutputDocImpl)src;
        if(src instanceof VisitImpl)
            return (VisitImpl)src;
        return null;
    }

    public boolean accept(Context context) {
        if(src instanceof SenegInputDocImpl)
            return ((SenegInputDocImpl)src).accept(context);
        return false;
    }

    @Override public Date getDate() { return src.getDate(); }
    @Override public String getDescription(Context context) { return src.getDescription(context); }
    @Override public String getId() { return src.getId(); }
    @Override public String getNumber() { return src.getNumber(); }
    @Override public int getSumType() { return src.getSumType(); }
    @Override public void open(Context context) { src.open(context); }
    @Override public int qty() { return src.qty(); }
    @Override public long sum() { return src.sum(); }

    @Override public DocDataObject getData() { return src.getData(); }
    @Override public long getRowid() { return src.getRowid(); }
    @Override public void close() { src.close(); }

    public Date created() { return src instanceof CreatableDocument ? ((CreateDocDataObject)src.getData()).created : src.getDate();}

    @Override
    public int compareTo(DocWType o) {
        Date d1 = Util.getDayStart(getDate());
        Date d2 = Util.getDayStart(o.getDate());
        boolean sameDate = d1.compareTo(d2) == 0;
        if(sameDate) {
            // move custom docs up
            if(canAccept()) {
                if(!o.canAccept())
                    return 1;
            } else {
                if(o.canAccept())
                    return -1;
            }
        }
        int cmp = created().compareTo(o.created());
        return cmp;
    }
}
