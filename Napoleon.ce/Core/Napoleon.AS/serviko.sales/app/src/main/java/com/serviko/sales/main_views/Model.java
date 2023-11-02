package com.serviko.sales.main_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.Contract;
import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.OrderItem;
import com.serviko.dataobjects.OrderSend;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.priceTree.Folder;
import com.serviko.dataobjects.priceTree.PriceTree;
import com.serviko.dataobjects.ws.ErrResult;
import com.serviko.dataobjects.ws.ReqCodeParam;
import com.serviko.dataobjects.ws.ReqCodeResult;
import com.serviko.dataobjects.ws.ReqOrdersParam;
import com.serviko.dataobjects.ws.ReqOrdersResult;
import com.serviko.dataobjects.ws.SendBasketParam;
import com.serviko.dataobjects.ws.WSExchange;
import com.serviko.sales.MainActivity;
import com.serviko.sales.PictureHolder;
import com.serviko.sales.main_views.order_filter.OrderFilter;
import com.serviko.sales.main_views.price_filter.PriceFilter;
import com.serviko.sales.main_views.price_filter.PriceOrdering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model extends ViewModel {
    static String BASE_URL = "http://1c.serviko.ru:8081/images/";
    static String BASE_URL2 = "http://1c.serviko.ru:8081/images/B2BImages/";

    public ActionDef currentAction;

    MutableLiveData<Boolean> requestInProgress = new MutableLiveData<>(false);
    MutableLiveData<ErrResult> requestError = new MutableLiveData<>();
    MutableLiveData<Boolean> requestResult = new MutableLiveData<>();

    MutableLiveData<Partner> partner = new MutableLiveData<>();
    MutableLiveData<Integer> picEvent = new MutableLiveData<>();
    int picEventCtr = 0;

    public Folder currentFolder;

    public OrderFilter orderFilter = new OrderFilter();
    public PriceOrdering priceSort = new PriceOrdering();
    public PriceFilter priceFilter = new PriceFilter();

    MutableLiveData<Order> currentOrder = new MutableLiveData<>();

    MutableLiveData<Integer> basketQty = new MutableLiveData<>();

    public Basket getBasket() { return partner.getValue().basket; }
    public LiveData<Integer> getBasketQty() { return basketQty; }

    public void logout() {
        PartnerList.partners().clear();
        partner.postValue(new Partner());
    }

    public LiveData<Boolean> getRequestInProgress() { return requestInProgress; }
    public LiveData<Boolean> getRequestResult() { return requestResult; }
    public LiveData<ErrResult> getRequestError() { return requestError; }
    public void clearRequestError() {
        requestError.postValue(null);
    }

    Basket.Handler basketHandler = basket -> {
        basketQty.postValue(basket.size());
    };

    public void setFrom(Model activeModel) {
        currentAction = activeModel.currentAction;
        currentFolder = activeModel.currentFolder;

        orderFilter = activeModel.orderFilter;
        priceSort = activeModel.priceSort;
        priceFilter = activeModel.priceFilter;

        partner.setValue(activeModel.partner.getValue());
        currentOrder.setValue(activeModel.currentOrder.getValue());

        basketQty.setValue(activeModel.basketQty.getValue());
    }

    public LiveData<Partner> getPartner() { return partner; }
    public void setPartner(Partner newPartner) {
        Partner p = partner.getValue();
        if(p != null) {
            p.basket.setHandler(null);
        }
        newPartner.basket.setHandler(basketHandler);
        basketQty.postValue(newPartner.basket.size());
        partner.postValue(newPartner);
    }

    void loadOrders(Context context) {
        ReqOrdersParam prm = new ReqOrdersParam();
        ReqCodeParam rp =  MainActivity.getProgParams();
        prm.appId = rp.appId;
        prm.orgId = partner.getValue().id;

        WSExchange exch = new WSExchange(context);
        exch.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) {
                requestInProgress.postValue(false);
                requestResult.postValue(true);
            }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                ReqOrdersResult res = (ReqOrdersResult) response;
                partner.getValue().setOrders(res.orders);
                requestInProgress.postValue(false);
                requestResult.postValue(true);
            }
        });
        exch.reqOrders(prm);
    }

    public void sendBasket(Context context) {
        OrderSend order = new OrderSend(getBasket());
        SendBasketParam prm = new SendBasketParam();
        ReqCodeParam rp =  MainActivity.getProgParams();
        prm.appId = rp.appId;
        prm.deviceId = rp.deviceId;
        prm.orgId = partner.getValue().id;
        prm.orders.add(order);

        requestResult.postValue(false);
        requestInProgress.postValue(true);

        WSExchange exch = new WSExchange(context);
        exch.setHandler(new WSExchange.Events() {
            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {

                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestInProgress.postValue(false);
                    requestError.postValue(err);
                    return;
                }

                loadOrders(context);
            }

            @Override
            public void error(Exception e) {
                requestInProgress.postValue(false);
                ErrResult err = new ErrResult();
                err.error = e.getLocalizedMessage();
                err.mode = ErrResult.EXCEPTION_MODE;
                requestError.postValue(err);
            }
        });
        if(com.serviko.sales.login_views.Model.TESTING) {
            requestResult.postValue(true);
            requestInProgress.postValue(false);
        } else
            exch.sendOrder(prm);
    }

    public void popFolder() {
        Folder root = partner.getValue().getPrice().root();
        Folder upf = findParent(root, currentFolder);
        if(upf == null) {
            upf = root;
        }
        currentFolder = upf;
    }

    public LiveData<Order> getOrder() { return currentOrder; }
    public void setOrder(Order o) { currentOrder.postValue(o); }


    PictureHolder.Handler handler = (id, img) -> picEvent.postValue(++picEventCtr);

    public void bindPicHandler() { PictureHolder.addHandler(handler); }
    public void unbindPicHandler() { PictureHolder.removeHandler(handler); }

    public LiveData<Integer> getPicEvent() { return picEvent; }

    public String makeUrl(String id, boolean isCategoryOrAction) {
        String url = (isCategoryOrAction ? BASE_URL2 : BASE_URL) + id;
        if(!id.contains(".png"))
            url += ".jpg";
        return url;
    }
    public Bitmap getPhoto(String url) {
        return PictureHolder.get(url);
    }

    public List<Contract> getOrdersContracts() {
        List<Contract> ret = new ArrayList<>();
        Partner p = partner.getValue();

        PriceTree pt = p.getPrice();
        for(Order o : p.orders) {
            for(OrderItem oi : o.items) {
                com.serviko.dataobjects.Price prc = pt.find(oi.id);
                if(prc != null) {
                    Contract c = PartnerList.contracts.get(prc.contract);
                    if(!ret.contains(c)) {
                        ret.add(c);
                    }
                }
            }
        }

        Collections.sort(ret);
        return ret;
    }

    Folder findParent(Folder curParent, Folder cf) {
        for(Folder f : curParent.childs) {
            if(f == cf) return curParent;
            Folder tf = findParent(f, cf);
            if(tf != null) return tf;
        }
        return null;
    }

    public void clearRequsetResult() {
        requestResult.postValue(false);
    }
}
