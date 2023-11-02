package com.ashberrysoft.leadertask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.provider.Contacts.SettingsColumns.KEY;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY10;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY2;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY3;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY4;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY5;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY6;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY7;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY8;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY9;
import static com.ashberrysoft.leadertask.Utils.COUNT_KEY_LOGIN;
import static com.ashberrysoft.leadertask.Utils.KEYKEY;
import static com.ashberrysoft.leadertask.Utils.getLogIn;
import static com.ashberrysoft.leadertask.Utils.saveLogIn;
import static com.ashberrysoft.leadertask.Utils.saveSyncInfoAllUsers;
import static com.ashberrysoft.leadertask.Utils.saveSyncInfoForMe;
import static com.ashberrysoft.leadertask.Utils.saveSyncInfoToday;
import static com.ashberrysoft.leadertask.Utils.saveUserName;

public class StartActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {


    private static GoogleApiClient mGoogleApiClient;
    private static String nodeId;
    private LinearLayout forme2;
    private LinearLayout today2;
    private LinearLayout circ;
    private ProgressBar mProgressBar;
    private TextView mProgressBarText;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.start_wear);
        forme2 = (LinearLayout) findViewById(R.id.qwer1);
        today2 = (LinearLayout) findViewById(R.id.qwer2);
        circ = (LinearLayout) findViewById(R.id.qwer3);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBarText = (TextView) findViewById(R.id.progressBarText);

        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF9900"), PorterDuff.Mode.MULTIPLY);

        ImageView today = (ImageView) findViewById(R.id.totoday);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                startActivity(ListActivity.newInstance(StartActivity.this, true));
            }
        });

        ImageView forme = (ImageView) findViewById(R.id.toforme);
        forme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                startActivity(ListActivity.newInstance(StartActivity.this, false));
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.v("Tedorius", "onConnectionFailed");
                    }
                })
                .addApi(Wearable.API)
                .build();



        if (!getLogIn(StartActivity.this)) {
            LTPowerManager.getInstance(getApplicationContext()).sleepLock();
            mGoogleApiClient.connect();
            retrieveDeviceNode();

            sendCheckLogin();
            forme2.setVisibility(View.GONE);
            today2.setVisibility(View.GONE);
            circ.setVisibility(View.VISIBLE);
        }
    }

    private void resetVisibilityAfterLoad() {
        if (getLogIn(StartActivity.this)) {
            forme2.setVisibility(View.VISIBLE);
            today2.setVisibility(View.VISIBLE);
            circ.setVisibility(View.GONE);
        } else {
            /*forme2.setVisibility(View.GONE);
            today2.setVisibility(View.GONE);
            circ.setVisibility(View.VISIBLE);*/
            mProgressBar.setVisibility(View.GONE);
            mProgressBarText.setText(getResources().getText(R.string.t_error_no_auth));
            circ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StartActivity.this.finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LTPowerManager.getInstance(getApplicationContext()).sleepUnlock();
    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
            }
        }).start();
    }

    public static void sendCheckLogin() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "check_login", null);

                }
            }).start();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.v("Tedorius", "onDataChangedWear");
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(KEYKEY) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    if (!getLogIn(StartActivity.this)) {
                        saveLogIn(this, dataMap.getBoolean(COUNT_KEY_LOGIN));
                    }
                    saveUserName(this,dataMap.getString(COUNT_KEY7));
                    saveSyncInfoToday(this, dataMap.getStringArrayList(COUNT_KEY), dataMap.getStringArrayList(COUNT_KEY2), dataMap.getStringArrayList(COUNT_KEY8), dataMap.getStringArrayList(COUNT_KEY9));
                    saveSyncInfoAllUsers (this, dataMap.getStringArrayList(COUNT_KEY3), dataMap.getStringArrayList(COUNT_KEY10));
                    // TODO: 25.05.2017 добавить провеку на открытый экран
                    saveSyncInfoForMe(this, dataMap.getStringArrayList(COUNT_KEY4), dataMap.getStringArrayList(COUNT_KEY5), dataMap.getStringArrayList(COUNT_KEY6));

                    for (String email: Utils.getEmails(this)) {
                        readByteArray(dataMap.getByteArray(email), email);
                    }
                }
            }
            resetVisibilityAfterLoad();
        }
    }

    private void readByteArray(byte[] bytes, String fileName) {
        try {
            final File dst = new File(this.getExternalFilesDir(null), fileName);
            FileOutputStream fos = new FileOutputStream(dst.getPath());
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }
}
