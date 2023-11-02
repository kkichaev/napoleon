package com.grsoft.napoleon.dostavka.documents;

import com.grsoft.dataobjects.AutoWaybill;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.AutoWaybillImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DispatchReturnsInfoImpl;
import com.grsoft.napoleon.documents.DocType;

import java.util.ArrayList;
import java.util.List;

public class AutoWaybillDoc extends DocType {
    static AutoWaybillDoc instance;

    public static AutoWaybillDoc instance() {
        if(instance == null)
            instance = new AutoWaybillDoc();
        return instance;
    }

    AutoWaybillDoc() {
        super("AutoWaybill","AutoWaybill", AutoWaybillImpl.class);
    }

//    @Override
//    public List<CreateDocDataObject> getDirtyPhotos() {
//        final List<CreateDocDataObject> ret = new ArrayList<CreateDocDataObject>();
//
//        DataTraveler.travel(AutoWaybill.class, new DataTraveler.Travel<AutoWaybill>(true) {
//
//            @Override
//            public boolean travel(DataTraveler<AutoWaybill> item) {
//                ret.add(item.data);
//                return true;
//            }
//        }, "closed == 1");
//
//        return ret;
//    }
}
