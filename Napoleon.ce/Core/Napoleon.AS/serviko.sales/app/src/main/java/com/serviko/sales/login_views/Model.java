package com.serviko.sales.login_views;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.ws.AcceptCodeParam;
import com.serviko.dataobjects.ws.AcceptCodeResult;
import com.serviko.dataobjects.ws.ErrResult;
import com.serviko.dataobjects.ws.GetKupecResponse;
import com.serviko.dataobjects.ws.ReqCodeParam;
import com.serviko.dataobjects.ws.ReqCodeResult;
import com.serviko.dataobjects.ws.ReqOrdersParam;
import com.serviko.dataobjects.ws.ReqOrdersResult;
import com.serviko.dataobjects.ws.ReqPriceParam;
import com.serviko.dataobjects.ws.ReqPriceResult;
import com.serviko.dataobjects.ws.WSExchange;
import com.serviko.sales.BuildConfig;
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;
import com.serviko.utils.Updater;

import java.util.Iterator;
import java.util.List;

public class Model extends ViewModel {
    static final String TAG = Model.class.toString();

    public static final String PHONE_NUMBER = "phone_number";
    public static final String SMS_MODE = "sms_mode";

    public static String DEMO_PHONE = "+71234567890";
    public static boolean DEMO = false;

    public static boolean TESTING = false;
//    public static boolean TESTING = BuildConfig.DEBUG;

    MutableLiveData<String> phone = new MutableLiveData<>();
    MutableLiveData<Boolean> smsMode = new MutableLiveData<Boolean>();
    MutableLiveData<Boolean> requestInProgress = new MutableLiveData<>(false);
    MutableLiveData<Boolean> dataLoaded = new MutableLiveData<>(false);

    MutableLiveData<Updater.Progress> loadProgress = new MutableLiveData<>();

    MutableLiveData<ErrResult> requestError = new MutableLiveData<>();
    MutableLiveData<ReqCodeResult> requestResult = new MutableLiveData<>();

    List<Partner> partners;

    public void load(Context context) {
        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        phone.setValue(pref.getString(PHONE_NUMBER, "+7"));
        smsMode.setValue(pref.getBoolean(SMS_MODE, true));
    }

    public void save(Context context) {
        SharedPreferences.Editor ed = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit();
        ed.putString(PHONE_NUMBER, phone.getValue());
        ed.putBoolean(SMS_MODE, smsMode.getValue());
        ed.commit();
    }

    public MutableLiveData<String> getPhone() { return phone; }
    public MutableLiveData<Boolean> getSmsMode() { return smsMode; }
    public LiveData<Boolean> getRequestInProgress() { return requestInProgress; }
    public LiveData<ReqCodeResult> getRequestResult() { return requestResult; }
    public LiveData<ErrResult> getRequestError() { return requestError; }
    public LiveData<Boolean> getDataLoaded() { return dataLoaded; }
    public LiveData<Updater.Progress> getLoadProgress() { return loadProgress; }

    // for update apk
    public void setRequest(boolean inProgress) {
        requestInProgress.postValue(inProgress);
    }

    public void setLoadProgress(Updater.Progress progress) {
        loadProgress.postValue(progress);
    }

    String toPhoneNumber(String num) {
        String res = "";
        for( char sym : num.toCharArray()) {
            if(sym == '+' && res.length() == 0) {
                res += sym;
                continue;
            }
            if(Character.isDigit(sym)) {
                res += sym;
            }
        }
        return res;
    }

    public void ackCode(Context context) {
        requestInProgress.setValue(true);

        WSExchange exc = new WSExchange(context);
        exc.setHandler(new WSExchange.Events() {
            @Override
            public void complete(final boolean result, final Object response, WSExchange exchange) {
                requestInProgress.postValue(false);

                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestError.postValue(err);
                    return;
                }

                ReqCodeResult res = (ReqCodeResult) response;
                requestResult.postValue(res);
            }

            @Override
            public void error(Exception e) {
                handleError(e);
            }
        });

//        if(TESTING) {
//            ReqCodeResult res = new ReqCodeResult();
//            res.result = false;
//            res.error = ErrResult.OLD_VERSION;
//            res.code = 0;
//            requestResult.postValue(res);
//            requestInProgress.postValue(false);
//            return;
//        }
        String sphone = toPhoneNumber(phone.getValue());
        if(sphone.equals(DEMO_PHONE)) {
            TESTING = true;
            DEMO = true;
            requestInProgress.postValue(false);

            ReqCodeResult res = new ReqCodeResult();
            res.result = true;
            res.error = "";
            res.code = -1;
            requestResult.postValue(res);
        } else {
            ReqCodeParam prm = MainActivity.getProgParams();
            prm.phone = sphone;
            prm.byPhone = smsMode.getValue() ? 0 : 1;
            exc.reqCode(prm);
        }
    }

    void handleError(Exception e) {
        requestInProgress.postValue(false);
        ErrResult err = new ErrResult();
        err.error = e.getLocalizedMessage();
        err.mode = ErrResult.EXCEPTION_MODE;

        requestError.postValue(err);
    }


    public void loadData(final Context context) {
        requestInProgress.postValue(true);
        WSExchange exc = new WSExchange(context);
        exc.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) {
                handleError(e);
            }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestInProgress.postValue(false);
                    requestError.postValue(err);
                    return;
                }

                AcceptCodeResult res = (AcceptCodeResult) response;
                partners = res.partners;
                PartnerList.setPartners(partners);

                Log.d(TAG, String.format("Got %d partners", partners.size()));

                loadOrders(res.partners.iterator(), context);
            }
        });

        AcceptCodeParam prm = new AcceptCodeParam();

        ReqCodeParam src = MainActivity.getProgParams();
        prm.phone = phone.getValue();
        prm.deviceId = src.deviceId;
        prm.appId = src.appId;

        if(TESTING) {
            exc.setAnswerResource(R.raw.ack_code1);
        }

        exc.acceptCode(prm);
    }

    void getKupecAction(final Iterator<Partner> iPartners, final Partner cp, final ReqPriceResult res, Context context) {
        WSExchange exch = new WSExchange(context);
        exch.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) {
                handleError(e);
            }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestInProgress.postValue(false);
                    requestError.postValue(err);
                    return;
                }
                cp.setPrice(res.price, res.actionRules, res.actionConditions);
                cp.kupecAction = ((GetKupecResponse)response).items;
                PartnerList.addContracts(res.contracts);

                loadOrders(iPartners, context);
            }
        });
        if(TESTING) {
            if(cp.id.equals("008f9944-ddb6-11e8-80d7-1866dab567c7")) {
                exch.setAnswerResource(R.raw.get_kupec);
            }
        }
        exch.reqKupec(cp.id);

    }

    void loadPrice(final Iterator<Partner> iPartners, final Partner cp, Context context) {
        ReqPriceParam prm = new ReqPriceParam();
        ReqCodeParam rp =  MainActivity.getProgParams();
        prm.appId = rp.appId;
        prm.deviceId = rp.deviceId;
        prm.orgId = cp.id;

        WSExchange exch = new WSExchange(context);
        exch.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) {
                handleError(e);
            }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestInProgress.postValue(false);
                    requestError.postValue(err);
                    return;
                }
                getKupecAction(iPartners, cp, (ReqPriceResult) response, context);
            }
        });
        if(TESTING) {
            if(prm.orgId.equals("b19fa3ea-eafe-11e8-80db-1866dab567cd")) {
                exch.setAnswerResource(R.raw.get_price2);
            } else if(prm.orgId.equals("008f9944-ddb6-11e8-80d7-1866dab567c7")) {
                exch.setAnswerResource(R.raw.get_price3);
            } else {
                exch.setAnswerResource(R.raw.get_price);
            }
        }
        exch.reqPrice(prm);
    }

    void loadOrders(final Iterator<Partner> iPartners, Context context) {
        if(iPartners.hasNext() == false) {
//            // remove partner without price
            Iterator<Partner> pl = partners.iterator();
            while (pl.hasNext()) {
                Partner p = pl.next();
                if(p.getPrice().size() == 0)
                    pl.remove();
            }
//            PartnerList.setPartners(partners);
            requestInProgress.postValue(false);
            dataLoaded.postValue(true);
            return;
        }

        final Partner cp = iPartners.next();

        ReqOrdersParam prm = new ReqOrdersParam();
        ReqCodeParam rp =  MainActivity.getProgParams();
        prm.appId = rp.appId;
        prm.orgId = cp.id;

        WSExchange exch = new WSExchange(context);
        exch.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) {
                handleError(e);
            }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                ErrResult err = WSExchange.checkError(result, response);
                if(err != null) {
                    requestInProgress.postValue(false);
                    requestError.postValue(err);
                    return;
                }
                final ReqOrdersResult res = (ReqOrdersResult) response;
                for(Order o : res.orders) {
                    if(o.state != 2) {
                        Log.d("Order status != 2", o.number);
                    }
                }
                cp.setOrders(res.orders);
                loadPrice(iPartners, cp, context);
            }
        });
        if(TESTING) {
            exch.setAnswerResource(R.raw.get_order);
        }
        exch.reqOrders(prm);
    }
}
