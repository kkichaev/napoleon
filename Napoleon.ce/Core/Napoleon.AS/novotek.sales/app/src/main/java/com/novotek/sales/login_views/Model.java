package com.novotek.sales.login_views;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.priceTree.FolderBase;
import com.novotek.dataobjects.ws.AcceptCodeResult;
import com.novotek.dataobjects.ws.ErrResult;
import com.novotek.dataobjects.ws.ReqCodeParam;
import com.novotek.dataobjects.ws.ReqCodeResult;
import com.novotek.dataobjects.ws.WSExchange;
import com.novotek.sales.BuildConfig;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.utils.Updater;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Model extends ViewModel {
    private static final String DEMO_TOKEN = "demo_token";
    public static long WAIT_NEXT_SEND_INTERVAL = BuildConfig.DEBUG ? 0 :  60 * 1000;

    public static final String PHONE_NUMBER = "phone_number";

    public static final String LAST_CONNECT = "last_connect";

    public static String DEMO_PHONE = "+71234567890";
    public static boolean DEMO = false;

    public static boolean TESTING = false;
//    public static boolean TESTING = BuildConfig.DEBUG;

    MutableLiveData<String> phone = new MutableLiveData<>();
    MutableLiveData<Boolean> requestInProgress = new MutableLiveData<>(false);
    MutableLiveData<Boolean> dataLoaded = new MutableLiveData<>(false);

    MutableLiveData<Updater.Progress> loadProgress = new MutableLiveData<>();

    MutableLiveData<ErrResult> requestError = new MutableLiveData<>();
    MutableLiveData<ReqCodeResult> requestResult = new MutableLiveData<>();
    private String appToken = "";
    long lastConnect = 0;

    public void load(Context context) {
        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        phone.setValue(pref.getString(PHONE_NUMBER, "+7"));
        lastConnect = pref.getLong(LAST_CONNECT, 0);

        if(phone.getValue() != null && phone.getValue().equals(DEMO_PHONE)) {
            TESTING = true;
            DEMO = true;
        }
    }

    public static String phoneNumber(Context context) {
        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PHONE_NUMBER, "+7");
    }

    public void save(Context context) {
        SharedPreferences.Editor ed = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit();
        ed.putString(PHONE_NUMBER, phone.getValue());
        ed.commit();
    }

    public String getAppToken(){
        return appToken;
    }

    public void updateLastConnect(Context context, long time) {
        lastConnect = time;

        SharedPreferences.Editor ed = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit();
        ed.putLong(LAST_CONNECT, lastConnect);
        ed.commit();
    }

    public MutableLiveData<String> getPhone() { return phone; }
    public LiveData<Boolean> getRequestInProgress() { return requestInProgress; }
    public LiveData<ReqCodeResult> getRequestResult() { return requestResult; }
    public LiveData<ErrResult> getRequestError() { return requestError; }
    public LiveData<Boolean> getDataLoaded() { return dataLoaded; }
    public LiveData<Updater.Progress> getLoadProgress() { return loadProgress; }

    public long getWaitInterval() {
        long ct = new Date().getTime();
        long w = lastConnect + WAIT_NEXT_SEND_INTERVAL;
        return (ct < w) ? w - ct : 0;
    }

    // for update apk
    public void setRequest(boolean inProgress) {
        requestInProgress.postValue(inProgress);
    }

    public void setLoadProgress(Updater.Progress progress) {
        loadProgress.postValue(progress);
    }

    public static String toPhoneNumber(String num) {
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

        updateLastConnect(context, new Date().getTime());
        String sphone = toPhoneNumber(phone.getValue());
        if(sphone.equals(DEMO_PHONE)) {
            TESTING = true;
            DEMO = true;
            requestInProgress.postValue(false);

            ReqCodeResult res = new ReqCodeResult();
            res.error = 0;
            res.message = "";
            res.token = DEMO_TOKEN;
            requestResult.postValue(res);
        } else {
            ReqCodeParam prm = MainActivity.getProgParams();
            prm.phone = sphone;
            exc.reqCode(prm);
        }
    }

    void handleError(Exception e) {
        requestInProgress.postValue(false);
        ErrResult err = new ErrResult();
        err.message = e.getLocalizedMessage();
        err.error = -1;

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
                if (err != null) {
                    requestInProgress.postValue(false);
                    requestError.postValue(err);
                    return;
                }
                requestInProgress.postValue(false);
                dataLoaded.postValue(true);
            }
        });

        if(TESTING) {
            exc.getTestData();
        } else {
            exc.getData();
        }
    }
}
