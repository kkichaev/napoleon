package com.ashberrysoft.leadertask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.R.attr.src;
import static android.provider.Contacts.SettingsColumns.KEY;
import static com.ashberrysoft.leadertask.R.layout.header;
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
import static com.ashberrysoft.leadertask.Utils.KEYKEY;
import static com.ashberrysoft.leadertask.Utils.getForMeTasks;
import static com.ashberrysoft.leadertask.Utils.getNames;
import static com.ashberrysoft.leadertask.Utils.getTasksToday;
import static com.ashberrysoft.leadertask.Utils.saveSyncInfoAllUsers;
import static com.ashberrysoft.leadertask.Utils.saveSyncInfoForMe;
import static com.ashberrysoft.leadertask.Utils.saveSyncInfoToday;
import static com.ashberrysoft.leadertask.Utils.saveUserName;

public class ListActivity extends WearableActivity implements ListItem.OnClickTaskItemListener, GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {

    private static final int SPEECH_REQUEST_CODE = 0;
    public static final String EXTRA_LIST_TYPE = "EXTRA_LIST_TYPE";


    private static ListAdapter mAdapter;
    private WearableListView listView;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static boolean canSwipeToRefresh = false;
    private static GoogleApiClient mGoogleApiClient;
    private static String nodeId;
    private static boolean mIsTodayList;
    private RelativeLayout mHeader;
    private int isWaitingForSync = 2;


    public static Intent newInstance(Context context, boolean isTodayTasks) {
        final Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra(EXTRA_LIST_TYPE, isTodayTasks);

        return intent;
    }


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        //
        if (b != null) {
            mIsTodayList = b.getBoolean(EXTRA_LIST_TYPE);
        } else {
            final Intent intent = getIntent();
            mIsTodayList = intent.getBooleanExtra(EXTRA_LIST_TYPE, true);
        }
        //
        setContentView(R.layout.activity_wear);

        final com.software.shell.fab.ActionButton addTaskContainer = (com.software.shell.fab.ActionButton) findViewById(R.id.add_task);

        if (mIsTodayList) {
            addTaskContainer.setVisibility(View.VISIBLE);
            addTaskContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displaySpeechRecognizer();
                }
            });
        } else {
            addTaskContainer.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                synchronize();
            }
        });

        listView = (WearableListView) findViewById(R.id.sample_list_view); // swipe_refresh_layout
        RelativeLayout l = (RelativeLayout)findViewById(R.id.llist);


        mHeader = (RelativeLayout) getLayoutInflater().inflate(header, null);

        final TextView header_title = (TextView) mHeader.findViewById(R.id.header_title);
        final TextView headerSubtitle = (TextView) mHeader.findViewById(R.id.header_subtitle);

        if (mIsTodayList) {
            header_title.setText(getResources().getString(R.string.today));
            headerSubtitle.setText(TimeHelper.getInstance(this).getCuteDateTitle(new Date(System.currentTimeMillis())));
        } else {
            header_title.setText(getResources().getString(R.string.sm_instruct_me));
            headerSubtitle.setVisibility(View.GONE);
        }

        // Add the header, or you have already defined it in the xml layout
        l.addView(mHeader, 0);

        // Now comes the scrolling part (makes the header disappear)
        listView.addOnScrollListener(new WearableListView.OnScrollListener() {
            @Override
            public void onScroll(int i) {

            }

            @Override
            public void onAbsoluteScrollChange(int i) {
                // Do only scroll the header up from the base position, not down...
                if (i > 0) {
                    mHeader.setY(-i);
                }
            }

            @Override
            public void onScrollStateChanged(int i) {}

            @Override
            public void onCentralPositionChanged(int i) {
                if (i==0) {
                    canSwipeToRefresh = true;
                } else {
                    canSwipeToRefresh = false;
                }
                mSwipeRefreshLayout.setEnabled(canSwipeToRefresh);
            }
        });

        loadAdapter();

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

        mGoogleApiClient.connect();
        retrieveDeviceNode();
        synchronize();
    }

    @Override
    public void onResume() {
        super.onResume();
        //synchronize();
    }

    private void reLoadAdapter(ArrayList data) {
        mAdapter.setData(data, mIsTodayList, this);
        mAdapter.notifyDataSetChanged();
    }

    private void loadAdapter() {
        ArrayList<String> arrayList = new ArrayList<>();
        mAdapter = new ListAdapter(ListActivity.this);

        if (mIsTodayList) {
            arrayList = getTasksToday(ListActivity.this);
        } else {
            arrayList = getForMeTasks(ListActivity.this);
        }

        mAdapter.setData(arrayList, mIsTodayList, this);

        listView.setAdapter(mAdapter);
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
        //
        //Toast.makeText(this, getResources().getText(R.string.toast_to_add_task), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, getResources().getText(R.string.toast_to_add_task2), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            recognizeAndAddTask(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("Tedorius", "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Wearable.DataApi.removeListener(mGoogleApiClient, this);
        //mGoogleApiClient.disconnect();
    }

    private void recognizeAndAddTask(String full_text) {
        ArrayList<String> namesCoworkers = getNames(ListActivity.this);
        boolean hasCustomer = false;
        String customer = "";
        String taskName = "";
        full_text = full_text.toLowerCase();
        String assign = getResources().getString(R.string.assign).toLowerCase();
        int indexAssign = full_text.indexOf(assign);
        //
        if (indexAssign != -1) {
            // если есть слово поручение и оно идет раньше ключевого слова "задача"
            customer = full_text.substring(0, indexAssign);
            taskName = full_text.substring(indexAssign + assign.length(), full_text.length());
            for (String email : namesCoworkers) {
                if (email.toLowerCase().equals(customer.trim().toLowerCase())) {
                    hasCustomer = true;
                    android.util.Log.v("Tedorius", "Поручить " + customer);
                    break;
                }
            }

            android.util.Log.v("Tedorius", "Задача" + taskName);
            if (!taskName.isEmpty()) {
                if (!hasCustomer) {
                    android.util.Log.v("Tedorius", "Ошибка, не нашли такого сотрудника");
                    speakToMe(getResources().getString(R.string.read_7));
                } else {
                    android.util.Log.v("Tedorius", "Выдаем голосом - Задача поручена: " + customer);
                    speakToMe(getResources().getString(R.string.read_9) + customer);
                    sendMessageAddTaskTo(taskName, customer);
                }
            } else {
                android.util.Log.v("Tedorius","Ошибка, нет названия задачи");
                speakToMe(getResources().getString(R.string.read_8)+customer);
            }
        } else {
            // если поручения нет или слово задача идет раньше ключевого слова "поручение"
            taskName = full_text;
            if (taskName.isEmpty()) {
                // если задача с пустым именем
                android.util.Log.v("Tedorius","Ошибка, нет названия задачи");
                speakToMe(getResources().getString(R.string.read_8)+customer);
            } else {
                // если есть название у задачи обычной
                android.util.Log.v("Tedorius", "Выдаем голосом - Задача добавлена");
                speakToMe(getResources().getString(R.string.read_11));
                sendMessageAddTask(taskName);
            }
        }
    }

    private void speakToMe(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void synchronize () {
        isWaitingForSync = 2;
        LTPowerManager.getInstance(getApplicationContext()).sleepLock();
        mGoogleApiClient.connect();
        retrieveDeviceNode();
        sendMessage(true, false, null, 0);
    }


    public void sendMessage(final boolean needSync, final boolean cancelTask, final String uid, final int id) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                    if (needSync) {
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "leadSync", null);
                    }
                    if (cancelTask) {
                        new Thread() {
                            public void run() {

                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.setDataAfterCancelTask(id);
                                                mAdapter.notifyDataSetChanged();
                                                if (id >= 0) {
                                                    mHeader.setY(-id);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {

                                    }
                            }
                        }.start();
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "cancelTask"+uid, null);
                    }

                }
            }).start();
        }
    }

    public static void sendMessageAddTask(final String taskName) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "addTask"+taskName, null);

                }
            }).start();
        }
    }

    public static void sendMessageAddTaskTo(final String taskName, final String toUser) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, taskName+"addNewAssign"+toUser, null);

                    android.util.Log.v("Tedorius","Поручена задача"+toUser+" с названием "+taskName);

                }
            }).start();
        }
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



    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Tedorius", "onConnectionSuspended");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v("Tedorius", "onDataChangedWear");
        if (isWaitingForSync == 1) {
            isWaitingForSync = 2;
            mSwipeRefreshLayout.setRefreshing(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LTPowerManager.getInstance(getApplicationContext()).sleepUnlock();
                }
            }, 2000);
        } else {
            if (isWaitingForSync == 2) {
                isWaitingForSync--;
            }
        }

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(KEYKEY) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    saveUserName(this,dataMap.getString(COUNT_KEY7));

                    saveSyncInfoToday(this, dataMap.getStringArrayList(COUNT_KEY), dataMap.getStringArrayList(COUNT_KEY2), dataMap.getStringArrayList(COUNT_KEY8), dataMap.getStringArrayList(COUNT_KEY9));
                    saveSyncInfoAllUsers (this, dataMap.getStringArrayList(COUNT_KEY3), dataMap.getStringArrayList(COUNT_KEY10));
                    saveSyncInfoForMe(this, dataMap.getStringArrayList(COUNT_KEY4), dataMap.getStringArrayList(COUNT_KEY5), dataMap.getStringArrayList(COUNT_KEY6));

                    for (String email: Utils.getEmails(this)) {
                        readByteArray(dataMap.getByteArray(email), email);
                    }

                    if (mIsTodayList) {
                        updateAdapter(dataMap.getStringArrayList(COUNT_KEY));
                    } else {
                        updateAdapter(dataMap.getStringArrayList(COUNT_KEY4));
                    }
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
                Log.v("Tedorius", "DataItem deleted");
            }
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


    // Our method to update the count
    private void updateAdapter(ArrayList<String> data) {
        reLoadAdapter(data);
    }

    @Override
    public void onTaskClick(String data, int id) {
        //android.util.Log.v("Tedorius",id+" Нажали "+data);
        startActivity(EditActivity.newInstance(ListActivity.this, data));
    }
}
