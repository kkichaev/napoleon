package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Dogovors;
import com.grsoft.dataobjects.Invent;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.SenegDocItem;
import com.grsoft.dataobjects.SenegInputDoc;
import com.grsoft.dataobjects.SenegOutputDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.InventDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SenegDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.List;

public class SenegInputDocImpl extends Document<SenegInputDoc> {

    CreatableDocument<?> linkedDoc = null;
    boolean ldocLoaded = false;

    @Override
    public void open(Context context) {
        if(!ldocLoaded)
            loadLinkedDoc();

        if(linkedDoc != null)
            linkedDoc.open(context);
    }

    CreatableDocument<?> linkedDocType() {
        CreatableDocument<?> cd = null;
        if(data.docType == SenegInputDoc.TYPE_ORDER) {
            cd = (CreatableDocument<?>) OrderDoc.instance().create();
        } else if(data.docType == SenegInputDoc.TYPE_INVENT) {
            cd = (CreatableDocument<?>) InventDoc.instance().create();
        } else if(data.docType == SenegInputDoc.TYPE_RETURN) {
            cd = (CreatableDocument<?>) ReturnDoc.instance().create();
        } else {
            cd = (CreatableDocument<?>) SenegDoc.instance().create();
        }
        return cd;
    }

    void loadLinkedDoc() {
        linkedDoc = null;

        CreatableDocument<?> cd = linkedDocType();

        if(cd != null) {
            String where = "link='" + data.link + "'";
            List<Long> ids = DbReader.readIds(cd.getTableName(), where, "");
            if(ids.size() > 0) {
                linkedDoc = cd;
                linkedDoc.read(ids.get(0));
            }
        }
        ldocLoaded = true;
    }

    public CreatableDocument<?> getLinkedDoc() {
        ldocLoaded = false;
        loadLinkedDoc();
        return linkedDoc;
    }

    public String description() {
        return data.typeToStr();
    }

    @Override
    public String getDescription(Context context) {
        if(!ldocLoaded)
            loadLinkedDoc();
        return linkedDoc != null ? linkedDoc.getDescription(context) : super.getDescription(context);
    }

    public int weight() {
        if(data.docType == SenegInputDoc.TYPE_RETURN || data.docType == SenegInputDoc.TYPE_ORDER || data.docType == SenegInputDoc.TYPE_INVENT) {
            if(!ldocLoaded)
                loadLinkedDoc();

            if(linkedDoc != null)
                return ((OrderImplBase<?>)linkedDoc).weight();
        }
        return data.weight();
    }

    @Override
    public long sum() {
        if(data.docType == SenegInputDoc.TYPE_RETURN || data.docType == SenegInputDoc.TYPE_ORDER || data.docType == SenegInputDoc.TYPE_INVENT) {
            if(!ldocLoaded)
                loadLinkedDoc();

            if(linkedDoc != null)
                return ((OrderImplBase<?>)linkedDoc).sum();
        }
        return data.sum();
    }

    public boolean accepted() {
        if(ldocLoaded == false) {
            loadLinkedDoc();
        }
        return linkedDoc != null && linkedDoc.getRowid() != ExtrasConst.INVALID_ROWID;
    }

    public boolean isEditable() {
        return data.canEdit() && (linkedDoc == null || linkedDoc.isEditable());
    }

    void addItems(Order doc) {
        for(SenegDocItem sdi : data.items) {
            OrderItem oi = new OrderItem();
            oi.id = sdi.id;
            oi.qty = sdi.qty;
            oi.cost = (int)((long)sdi.sum * Consts.QTY_SCALE / sdi.qty);

            doc.items.add(oi);
        }
    }

    void initOrder(OrderImpl dest) {
        DogovorsImpl di = new DogovorsImpl();
        Dogovors dog = di.getData();

        di.read("id", data.dogovor);
        OrderEx doc = (OrderEx) dest.getData();
        doc.link = data.link;
        doc.dogovor = data.dogovor;
        doc.prcType = dog.priceID;

        addItems(doc);

        dest.write();
    }

    public boolean accept(Context context) {
        if(isEditable()) {
            if(accepted()) {
                linkedDoc.delete();
                linkedDoc = null;
            } else {
                CreatableDocument<?> cd = linkedDocType();
                cd.initSilent(context, data.id, GPSUtilNew.getLastKnownLocation(context));
                if(data.docType == SenegInputDoc.TYPE_ORDER) {
                    initOrder((OrderImpl) cd);
                } else if(data.docType == SenegInputDoc.TYPE_RETURN) {
                    initReturn((ReturnImpl)cd);
                } else if(data.docType == SenegInputDoc.TYPE_INVENT) {
                    initInvent((InventImpl)cd);
                } else {
                    initOutDoc((SenegOutputDocImpl)cd);
                }
            }
            ldocLoaded = false;
        }
        return accepted();
    }

    private void initOutDoc(SenegOutputDocImpl dest) {
        SenegOutputDoc doc = dest.getData();
        doc.link = data.link;
        doc.docType = data.docType;
        dest.write();
    }

    private void initInvent(InventImpl dest) {
        Invent doc = dest.getData();
        doc.link = data.link;
        addItems(doc);
        dest.write();
    }

    private void initReturn(ReturnImpl dest) {
        ReturnEx doc = (ReturnEx) dest.getData();
        doc.link = data.link;
        addItems(doc);
        dest.write();
    }
}
