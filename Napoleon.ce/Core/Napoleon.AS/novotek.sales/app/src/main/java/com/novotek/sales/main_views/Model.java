package com.novotek.sales.main_views;

import android.content.Context;
import android.os.FileUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.novotek.dataobjects.Basket;
import com.novotek.dataobjects.CommonData;
import com.novotek.dataobjects.DataRcv;
import com.novotek.dataobjects.DataState;
import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.OrderCancelResult;
import com.novotek.dataobjects.OrderSend;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.ProjectData;
import com.novotek.dataobjects.UpdateScheduleData;
import com.novotek.dataobjects.ws.ErrResult;
import com.novotek.dataobjects.ws.WSExchange;
import com.novotek.dataobjects.xml.Reader;
import com.novotek.sales.BuildConfig;
import com.novotek.sales.PictureHolder;
import com.novotek.sales.UpdateDataService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class Model extends ViewModel {

    MutableLiveData<Boolean> requestInProgress = new MutableLiveData<>(false);
    MutableLiveData<ErrResult> requestError = new MutableLiveData<>();
    MutableLiveData<Boolean> requestResult = new MutableLiveData<>();

    MutableLiveData<Partner> partner = new MutableLiveData<>();
    MutableLiveData<Integer> picEvent = new MutableLiveData<>();
    int picEventCtr = 0;

    long dataStamp = 0;

    MutableLiveData<Order> currentOrder = new MutableLiveData<>();

    MutableLiveData<Integer> basketQty = new MutableLiveData<>();

    MutableLiveData<Date> deliveryDate = new MutableLiveData<>(new Date());

    MutableLiveData<OrderCancelResult> orderCancel = new MutableLiveData<>();
    MutableLiveData<List<Order>> orders = new MutableLiveData<>();

    public Basket getBasket() { return partner.getValue().basket; }
    public LiveData<Integer> getBasketQty() { return basketQty; }

    public void checkLogin(Context context) {
        String session = CommonData.getSession(context);
        if(session.length() == 0) {
            needLogin.postValue(true);
            return;
        }
        checkData(context);
    }

    public void checkData(Context context) {
//        if(BuildConfig.DEBUG) {
//            readData(context);
//        }
        if(CommonData.haveFreshData(context)) {
//            readData(context);
            dataState.postValue(new DataState(DataState.State.Read, ""));
            return;
        }
        dataState.postValue(new DataState(DataState.State.Receiving, ""));
        UpdateDataService.schedule(context, true);
    }

    void readDataThread(Context context) {
        File data = CommonData.getDataFile(context);

        try {
            String res = new String(Files.readAllBytes(Paths.get(data.getAbsolutePath())));
            Reader rdr = new Reader();
            DataRcv rcv = (DataRcv) rdr.read(res, DataRcv.class);

            if(rcv != null) {
                UpdateScheduleData upd = new UpdateScheduleData();
                upd.interval = rcv.common_info.poll_interval;
                upd.start = rcv.common_info.start_hour;
                upd.finish = rcv.common_info.end_hour;
                CommonData.putScheduleData(context, upd);
                if(!upd.empty()) {
                    UpdateDataService.schedule(context, false);
                }

                ProjectData.updateFrom(rcv);
                dataState.postValue(new DataState(DataState.State.Parsed, ""));
                dataStamp = CommonData.getDataReceived(context);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            dataState.postValue(new DataState(DataState.State.Error, e.getLocalizedMessage()));
        }
    }

    public boolean haveNewestData(Context context) {
        return dataStamp != CommonData.getDataReceived(context);
    }

    public void readData(Context context) {
        Thread t = new Thread(() -> readDataThread(context));
        t.start();
    }

    MutableLiveData<DataState> dataState = new MutableLiveData<>(new DataState());
    public LiveData<DataState> getDataState() { return dataState;}

    MutableLiveData<Boolean> needLogin = new MutableLiveData<>();
    public LiveData<Boolean> getNeedLogin() { return needLogin; }
    public void clearNeedLogin() {needLogin.postValue(false);}

    public void logout(Context context) {
        String session = CommonData.getSession(context);
        CommonData.putSession(context, "");

        if(session.length() > 0) {
            WSExchange exch = new WSExchange(context);
            exch.logout(session, (result, error) -> {
                needLogin.postValue(true);
                ProjectData.partners().clear();
                partner.postValue(new Partner());
            });
        }
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
        if(newPartner == null)
            return;
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
