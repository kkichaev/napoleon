package com.serviko.sales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.priceTree.Folder;
import com.serviko.dataobjects.ws.ReqCodeParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivityOld extends BaseActivityOld {

//    boolean loggedIn = false;
    Adapter adapter;
    boolean askLogin = false;

    static String appId;
    static String devId;
    static boolean starting = true;

    String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private final int REQUEST_CODE_PERMISSIONS = 10;

    public static ReqCodeParam getProgParams() {
        ReqCodeParam ret = new ReqCodeParam();
        ret.appId = appId;
        ret.deviceId = devId;
        return ret;
    }

    public static void open(Context context) {
        if(starting) {
            starting = false;
            return;
        }
        Intent i = new Intent(context, MainActivityOld.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
    }

    @Override protected int getLayoutID() { return R.layout.main_activity_old; }
    @Override protected int getBottomMenuID() { return 0; } //R.id.itMain; }
    @Override protected boolean canFinish() { return false; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        ListView lv = findViewById(R.id.lvItems);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Folder f = (Folder) adapter.getItem(position);
                PriceCatalog.open(MainActivityOld.this, f);
            }
        });
    }

    private Boolean allPermissionsGranted() {
        for(String p : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Login.REQ_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                selectCurrentPartner();
            } else if(resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //loggedIn = true;

        initAppData();
        if(!askLogin && allPermissionsGranted()) {
            askLogin = true;
            Login.open(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initAppData() {
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//
//                        appId = task.getResult().getToken();
//                    }
//                });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            devId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            devId = tm.getDeviceId();
        }
        if(devId == null) {
            devId = "0123456789012345";
        }
    }

    @Override
    protected void onPartnerSelect(Partner newPartner) {
        super.onPartnerSelect(newPartner);

        List<Folder> topFolders = new ArrayList<>();
        if(newPartner != null) {
            Folder root = newPartner.getPrice().root();
            for(Folder f : root.childs)
                topFolders.add(f);
        }

        adapter = new Adapter(topFolders);
        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(adapter);

        if(newPartner != null) {
            List<ActionDef> actions = newPartner.getActions();
            LinearLayout ll = findViewById(R.id.llActions);
            ll.removeAllViews();
            for(final ActionDef ad : actions) {
                View v = View.inflate(this, R.layout.action_tile, null);
                TextView tv = v.findViewById(R.id.tvName);
                tv.setText(ad.getName());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActionsActivity.open(MainActivityOld.this, ad);
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.addView(v, lp);
            }

            List<Order> orders = new ArrayList<>();
            ll = findViewById(R.id.llOrders);
            ll.removeAllViews();
            for(Order o : newPartner.orders) {
                if (!o.inState(Order.ORDER_STATE_ACTIVE))
                    continue;
                orders.add(o);
            }
            Collections.sort(orders, new Comparator<Order>() {
                @Override public int compare(Order o1, Order o2) { return o2.orderDate.compareTo(o1.orderDate); }
            });

            for(final Order o : orders) {
                View v = View.inflate(this, R.layout.order_tile, null);
                TextView tv = v.findViewById(R.id.tvName);

                SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy");
                String text = String.format("Заказ от %s<br/>Сумма %.02f &#x20bd<br/>Статус <b>%s</b>", sd.format(o.orderDate), o.sum(), o.status);
                tv.setText(Html.fromHtml(text));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderDetailActivity.open(MainActivityOld.this, o);
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.addView(v, lp);
            }
        }
    }

    class Adapter extends BaseAdapter {
        List<Folder> folders;

        public Adapter(List<Folder> folders) {
            this.folders = folders;
        }

        @Override public int getCount() { return folders.size(); }
        @Override public Object getItem(int position) { return folders.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null)
                view = View.inflate(MainActivityOld.this, R.layout.folder_row, null);

            Folder f = (Folder) getItem(position);
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(f.item.name);

            return view;
        }
    }
}
