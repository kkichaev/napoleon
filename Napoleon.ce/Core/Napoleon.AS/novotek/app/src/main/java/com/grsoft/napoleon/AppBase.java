package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.List;

public class AppBase extends NapoleonAppBase {
    @SuppressWarnings("unused")
    private static final String TAG = "NapoleonApp";

    class OrderEditor implements OrderImpl.PropertiesEditor {
        @Override
        public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
            CreateOrder.open(ctx, order, isOldOrder);
        }
    }

    @Override
    protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
        return OrderImplEx.class;
    }

    @Override
    protected void initChildDocTypes() {
        DocType.addType(ReturnDoc.instance());

        if (newClient())
            DocType.addType(NewClientDoc.instance());
    }

    @Override
    protected void defineNewType() {
        DebtDocEx.initialize();

        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Order.class, OrderEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);
        DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
        DataObjectInfo doi = DataObjectInfo.getInstance();
        doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
        OrderImpl.OrderEditor = new OrderEditor();

        UpdateDB.addHitchingCtor(new HitchingCtor() {
            @Override
            public Hitching create() {
                return new RcvNewHitching(Sklad.class);
            }
        }, UpdateDB.GEN_DATA_HITCHING);
    }

    @Override
    protected void initChildActivity() {
        Warehouse.activity = WarehouseEx.class;
        UpdateDB.activity = UpdateDBEx.class;
        PriceCount.activity = PriceCountEx.class;
        Documents.activity = DocumentsEx.class;
        CreateReturn.activity = CreateReturnEx.class;
        OrderDetail.activity = OrderDetailEx.class;
    }

    @Override
    protected void initChildFeature() {
        Features.POTENZIAL_ORG = false;
        Features.CAN_EXPAND_PRICE = true;
        Features.USE_COST_IN_RETURNS = true;
        Features.WH_QTY = true;
        Features.INTEGER_INPUTS_QTY = true;
    }

//	private void initDocTypes() {
//		DebtDocEx.initialize();
//		DocType.addType(ReturnDoc.instance());
//		DocType.addType(OrderDoc.instance(OrderImplEx.class));
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//		DocType.addType(RemnantsDoc.instance());
//		
//		DocType.setCurDoc(OrderDoc.instance());		
//	}

    public boolean newClient(){
        return BuildConfig.FLAVOR.equals("trade") || BuildConfig.FLAVOR.equals("novotek");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        OrderImpl.OrderEditor = new OrderEditor();
        setProgrammVersion();

        if (newClient()) {
            Main.mainMenuPrepared.add(new MenuPrepareHitching() {
                                          @Override
                                          public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
                                              menu.add(3, new MenuHandler(getString(R.string.newclient), new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      NewClientList.open(activity);
                                                  }
                                              }));
                                          }
                                      }
            );

            DocFilterOnClickListener.HiddenTypes.add(NewClientDoc.instance());
        }
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
