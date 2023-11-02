/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashberrysoft.leadertask.instance_sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.OkHttpConnection;
import com.ashberrysoft.leadertask.utils.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ashberrysoft.leadertask.application.Config.LT_PUSH_TO_SERV;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // тут регим на нашем серваке юзера
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

//       android.util.Log.v("Tedorius2","onTokenRefresh: "+refreshedToken);
        // post запрос к серверу нашему с регой
        if (Utils.isNetworkAvailable(getApplicationContext()) && LTSettings.getInstance().getUserProfile().isValid()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();;
                        nameValuePairs.add(new BasicNameValuePair("token ", refreshedToken));
                        nameValuePairs.add(new BasicNameValuePair("email ", LTSettings.getInstance().getUserName()));

                        String message = OkHttpConnection.postWithParams(nameValuePairs, LT_PUSH_TO_SERV);

                        //android.util.Log.v("Tedorius2","onTokenRefresh сообщение "+message);
                    } catch (Exception e) {
//                        android.util.Log.v("Tedorius2","onTokenRefresh ошибка");
                    }
                }
            }).start();
        }
    }

    public static void delToken(final Context context) {
        // тут регим на нашем серваке юзера
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    android.util.Log.v("Tedorius2", "delToken: " + FirebaseInstanceId.getInstance().getToken());
                    FirebaseInstanceId.getInstance().deleteInstanceId();

                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static void regToken(final Context context) {
        // тут регим на нашем серваке юзера
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                    if (refreshedToken != null) {

//                        android.util.Log.v("Tedorius2", "regToken: " + refreshedToken);
                        // post запрос к серверу нашему с регой

                        if (Utils.isNetworkAvailable(context)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // Add your data
                                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                                        nameValuePairs.add(new BasicNameValuePair("token", refreshedToken));
                                        nameValuePairs.add(new BasicNameValuePair("email", LTSettings.getInstance().getUserName()));

                                        String message = OkHttpConnection.postWithParams(nameValuePairs, LT_PUSH_TO_SERV);

//                                        android.util.Log.v("Tedorius2", "regToken сообщение " + message);
                                    } catch (Exception e) {
//                                        android.util.Log.v("Tedorius2", "regToken ошибка");
                                    }
                                }
                            }).start();
                        }
                    }
                } catch (Exception e) {

                }
            }
        }).start();



    }
}
