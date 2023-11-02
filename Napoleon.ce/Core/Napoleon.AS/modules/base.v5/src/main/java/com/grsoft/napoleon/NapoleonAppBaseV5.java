package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemV5;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceV5;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.RemnantsV5;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemV5;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplV5;
import com.grsoft.napoleon.documents.RemnantsDocV5;
import com.grsoft.network.RWServiceFactory;

public class NapoleonAppBaseV5 extends NapoleonAppBase {
    @Override
    protected void defineNewType() {
        RemnantsDocV5.init();

        super.defineNewType();

        RWServiceFactory.instance = new RWServiceFactoryV5();

        CostStrategy.defaultInstance = new CostStrategyV5();

        DbObject.regNewDataType(Price.class, PriceV5.class);
        DbObject.regNewDataType(Remnants.class, RemnantsV5.class);

        DataObjectInfo di = DataObjectInfo.getInstance();
        di.replaceListType(Order.class, "items", OrderItemV5.class);
        di.replaceListType(Visit.class, "items", VisitItemV5.class);
        di.replaceListType(Return.class, "items", ReturnItem.class);
    }

    @Override
    protected void initChildFeature() {
        super.initChildFeature();

        Features.WH_QTY = true;
        Features.HAVE_RETURN_DOC = true;
    }

    @Override
    protected Class<? extends ReturnImpl> returnsImplType() {
        return ReturnImplV5.class;
    }

    @Override
    protected void initChildActivity() {
        super.initChildActivity();

        UpdateDB.activity = UpdateDBV5.class;
        PriceCount.activity = PriceCountV5.class;
        Setting.NetworkSettingActivity = ConnectionSettings.class;
        Warehouse.activity = WarehouseV5.class;
    }
}
