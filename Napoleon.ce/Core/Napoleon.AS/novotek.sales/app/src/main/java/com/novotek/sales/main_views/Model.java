package com.novotek.sales.main_views;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.novotek.dataobjects.Basket;
import com.novotek.dataobjects.Brand;
import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.OrderCancelResult;
import com.novotek.dataobjects.OrderSend;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.ProjectData;
import com.novotek.dataobjects.priceTree.FolderBase;
import com.novotek.dataobjects.priceTree.FolderOld;
import com.novotek.dataobjects.priceTree.FolderSrc;
import com.novotek.dataobjects.ws.ErrResult;
import com.novotek.dataobjects.ws.ReqCodeParam;
import com.novotek.dataobjects.ws.ReqOrdersParam;
import com.novotek.dataobjects.ws.ReqOrdersResult;
import com.novotek.dataobjects.ws.WSExchange;
import com.novotek.sales.MainActivity;
import com.novotek.sales.PictureHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Model extends ViewModel {

    MutableLiveData<Boolean> requestInProgress = new MutableLiveData<>(false);
    MutableLiveData<ErrResult> requestError = new MutableLiveData<>();
    MutableLiveData<Boolean> requestResult = new MutableLiveData<>();

    MutableLiveData<Partner> partner = new MutableLiveData<>();
    MutableLiveData<Integer> picEvent = new MutableLiveData<>();
    int picEventCtr = 0;

    MutableLiveData<Order> currentOrder = new MutableLiveData<>();

    MutableLiveData<Integer> basketQty = new MutableLiveData<>();

    MutableLiveData<Date> deliveryDate = new MutableLiveData<>(new Date());

    MutableLiveData<OrderCancelResult> orderCancel = new MutableLiveData<>();
    MutableLiveData<List<Order>> orders = new MutableLiveData<>();

    public Basket getBasket() { return partner.getValue().basket; }
    public LiveData<Integer> getBasketQty() { return basketQty; }

    public void logout() {
        ProjectData.partners().clear();
        partner.postValue(new Partner());
    }

    public LiveData<Boolean> getRequestInProgress() { return requestInProgress; }
    public LiveData<Boolean> getRequestResult() { return requestResult; }
    public LiveData<ErrResult> getRequestError() { return requestError; }
    public LiveData<OrderCancelResult> getCancelResult() { return orderCancel; }
    public void clearRequestError() {
        requestError.postValue(null);
    }

    public LiveData<List<Order>> getOrders() { return orders; }

    public LiveData<Date> getDeliveryDate() {
        deliveryDate.postValue(getBasket().dlvDate);
        return deliveryDate;
    }

    public void setDeliveryDate(Date d) {
        getBasket().dlvDate = d;
        deliveryDate.postValue(d);
    }

    Basket.Handler basketHandler = basket -> {
        basketQty.postValue(basket.size());
    };

    public void setFrom(Model activeModel) {
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
        orders.postValue(newPartner.getOrders());
    }

    void loadOrders(Context context) {
        WSExchange exch = new WSExchange(context);
        exch.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) {
                requestInProgress.postValue(false);
                requestResult.postValue(true);
            }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                partner.getValue().setOrders((List<Order>) response);
                orders.postValue((List<Order>) response);

                requestInProgress.postValue(false);
                requestResult.postValue(true);
            }
        });
        exch.reqOrders(partner.getValue());
    }

    public void sendBasket(Context context) {
        OrderSend order = new OrderSend(partner.getValue(), getBasket());

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
                err.message = e.getLocalizedMessage();
                err.error = -1;
                requestError.postValue(err);
            }
        });
        if(com.novotek.sales.login_views.Model.TESTING) {
            requestResult.postValue(true);
            requestInProgress.postValue(false);
            exch.sendOrder(order);
        } else
            exch.sendOrder(order);
    }

    public LiveData<Order> getOrder() { return currentOrder; }
    public void setOrder(Order o) { currentOrder.postValue(o); }

    public void deleteOrder(Order o) {
    }

    public void cancelOrder(Order o, Context context) {
        requestInProgress.postValue(true);
        WSExchange exchange = new WSExchange(context);
        exchange.setHandler(new WSExchange.Events() {
            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                requestInProgress.postValue(false);
                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestError.postValue(err);
                    return;
                }
                OrderCancelResult res = (OrderCancelResult) response;
                o.statusValue = res.statusValue;
                res.doc = o;
                orderCancel.postValue(res);
            }

            @Override
            public void error(Exception e) {
                requestInProgress.postValue(false);
            }
        });
        exchange.cancelOrder(o, partner.getValue());
    }

    public void copyOrder(Order o) {
        getBasket().setFrom(o, getPartner().getValue().getPrice());
    }

    PictureHolder.Handler handler = (id, img) -> picEvent.postValue(++picEventCtr);

    public void bindPicHandler() { PictureHolder.addHandler(handler); }
    public void unbindPicHandler() { PictureHolder.removeHandler(handler); }

    public LiveData<Integer> getPicEvent() { return picEvent; }


    public void clearRequestResult() {
        requestResult.postValue(false);
    }

    public void sendFeedback(String text, Context context) {
        requestInProgress.postValue(true);

        WSExchange exchange = new WSExchange(context);
        exchange.setHandler(new WSExchange.Events() {
            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                requestInProgress.postValue(false);
            }

            @Override
            public void error(Exception e) {
                requestInProgress.postValue(false);
            }
        });
        exchange.sendMessage(text);
    }

    public void clearCancelResult() {
        orderCancel.postValue(null);
    }
}
