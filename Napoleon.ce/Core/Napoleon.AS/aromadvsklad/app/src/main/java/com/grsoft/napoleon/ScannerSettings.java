package com.grsoft.napoleon;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.SettingActivity;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerSettings extends SettingActivity {
    static final String TAG = "ScannerSetting";

    protected static final int WAIT_DLG = 0;

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_BLUETOOTH = 101;
    private static final int REQUEST_BLUETOOTH_ADMIN = 102;

    private ArrayAdapter<KeyValue> devices;
    ListView listView;
    MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_settings);

        if((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},REQUEST_BLUETOOTH);
            return;
        }

        if(!haveBTAdminGranted()) {
            return;
        }

        findViewById(R.id.btnPlayFail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingToneData rt = (RingToneData)((Spinner)findViewById(R.id.spFailTone)).getSelectedItem();
                if(rt != null)
                    playSound(rt.uri);
            }
        });

        findViewById(R.id.btnPlayGood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingToneData rt = (RingToneData)((Spinner)findViewById(R.id.spGoodTone)).getSelectedItem();
                if(rt != null)
                    playSound(rt.uri);
            }
        });
        init();
    }

    private void playSound(String uri) {
        if(uri.length() > 0) {
            Uri u = Uri.parse(uri);
            if(u != null) {
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                }

                mediaPlayer = MediaPlayer.create(this, u);
                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer = null;
                        mp.reset();
                        mp.stop();
                    }
                });
                mediaPlayer.start();
            }
        }
    }

    public Map<String, RingToneData> getNotifications() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, RingToneData> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            list.put(notificationUri, new RingToneData(notificationUri,notificationTitle));
        }

        return list;
    }

    boolean haveBTAdminGranted() {
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN},REQUEST_BLUETOOTH_ADMIN);
            return false;
        }
        return true;
    }

    void init() {
        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View arg0) { refreshDevices(); }
        });

        devices = new ArrayAdapter<KeyValue>(this, R.layout.devices_row, R.id.tvName){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = View.inflate(getContext(), R.layout.devices_row, null);

                TextView textView = (TextView)convertView.findViewById(R.id.tvName);
                KeyValue item = getItem(position);
                if( item != null && item.value != null ){
                    String text = item.value.toString();
                    if(listView.isItemChecked(position)) {
                        convertView.setBackgroundResource(R.drawable.device_selected);
                    } else
                        convertView.setBackgroundResource(R.drawable.device_back);
                    //					text = "<b>" + text + "</b>";
                    textView.setText(Html.fromHtml(text));
                }
                return convertView;
            }
        };

        listView = ((ListView)findViewById(R.id.lvDevices));
        listView.setAdapter(devices);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> list, View view, int arg2, long arg3) {
                listView.setItemChecked(arg2, true);
                devices.notifyDataSetChanged();
            }
        });

        update();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_BLUETOOTH) {
            if(grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                if(!haveBTAdminGranted())
                    return;
                init();
            }
        } else if(requestCode == REQUEST_BLUETOOTH_ADMIN) {
            if(grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                init();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");

            String action = intent.getAction();
            Log.d(TAG, action);
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                showDialog(WAIT_DLG);
                devices.clear();
                int pos = listView.getCheckedItemPosition();
                if( pos >= 0 )
                    listView.setItemChecked(pos, false);
            }else  if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                try {
                    dismissDialog(WAIT_DLG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                KeyValue kv = new KeyValue(device.getAddress(), device.getName());

                boolean found = false;
                for( int i=0; i<devices.getCount(); i++ ) {
                    if( devices.getItem(i).key.equals(kv.key)) {
                        found = true;
                        break;
                    }
                }

                if( !found ) {
                    devices.add(kv);
                    devices.notifyDataSetChanged();
                }
            }
        }
    };

    protected Dialog createWaitDlg() {
        ProgressDialog result = new ProgressDialog(this);
        result.setMessage("Подождите...");

        return result;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id){
            case WAIT_DLG:
                return createWaitDlg();
            default:
                return super.onCreateDialog(id);
        }
    }

    protected void refreshDevices() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if( adapter != null ) {
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            boolean r = adapter.startDiscovery();
            Log.d(TAG, "bluetoothAdapter.startDiscovery() = " + r);
        } else {
            Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            boolean r = BluetoothAdapter.getDefaultAdapter().startDiscovery();
            Log.d(TAG, "bluetoothAdapter.startDiscovery() = " + r);
        }
    }
    @Override
    public void save() {
        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();

        int pos = listView.getCheckedItemPosition();
        if( pos >= 0 ) {
            KeyValue kv = devices.getItem(pos);

            if (kv != null)
            {
                cfg.scannerAddress = kv.key.toString();
                cfg.scannerName = kv.value.toString();

                BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                if( ba != null && cfg.scannerAddress.length() > 0 ) {
                    BluetoothDevice dev = ba.getRemoteDevice(cfg.scannerAddress);
                    if( dev.getBondState() == BluetoothDevice.BOND_NONE ) {
                        Toast.makeText(this, R.string.device_no_paired, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        RingToneData sel;
        Spinner sp;
        sp = findViewById(R.id.spFailTone);
        sel = (RingToneData) sp.getSelectedItem();
        if(sel != null)
            cfg.uriFail = sel.uri;

        sp = findViewById(R.id.spGoodTone);
        sel = (RingToneData) sp.getSelectedItem();
        if(sel != null)
            cfg.uriGood = sel.uri;

        ConfigManager.save();
    }

    @Override
    public void update() {
        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();

        if( cfg.address.length() > 0 ) {
            KeyValue kv = new KeyValue(cfg.scannerAddress, cfg.scannerName);
            devices.clear();
            devices.add(kv);
            devices.notifyDataSetChanged();
            listView.setItemChecked(0, true);
            devices.notifyDataSetChanged();
        }

        Map<String, RingToneData> rt = getNotifications();
        List<RingToneData> values = new ArrayList<>(rt.values());

        ArrayAdapter<RingToneData> aa;
        RingToneData sel;
        Spinner sp;

        aa = new ArrayAdapter<RingToneData>(this, R.layout.simple_spinner_layout, values);
        sp = findViewById(R.id.spFailTone);
        sp.setAdapter(aa);
        sel = rt.get(cfg.uriFail);
        if(sel != null)
            sp.setSelection(values.indexOf(sel));

        aa = new ArrayAdapter<RingToneData>(this, R.layout.simple_spinner_layout, values);
        sp = findViewById(R.id.spGoodTone);
        sp.setAdapter(aa);
        sel = rt.get(cfg.uriGood);
        if(sel != null)
            sp.setSelection(values.indexOf(sel));
    }


    @Override public int getName() {return R.string.scanner_settings; }
    @Override public int getIcon() { return R.drawable.scanner_settings; }
}
