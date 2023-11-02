package com.grsoft.napoleon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.grsoft.napoleon.util.debug.Path;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BarcodeScannerHelper {
    public static final UUID RFCOMM_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ScanThread thread = null;
    public interface Event {
        void onRead(String barcode);
    }

    public BarcodeScannerHelper() { }

    public void scanning(Context context, String address, Event handler) {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

        if( ba == null ) {
            return;
        }

        ba.cancelDiscovery();
        boolean paired = false;
        for (BluetoothDevice device : ba.getBondedDevices()) {
            if(device.getAddress().equals(address)) {
                paired = true;
                thread = new ScanThread(device, handler);
                thread.start();
                break;
            }
        }

        if( !paired ) {
            Toast.makeText(context, R.string.device_no_paired, Toast.LENGTH_SHORT).show();
        }
    }

    public void close() {
        if(thread != null) {
            thread.cancel();
        }
    }

    private class ScanThread extends Thread {
        private static final String TAG = "ScanThread";

        BluetoothDevice device;
        private BluetoothSocket socket;

        Event handler;
        boolean closing = false;

        public ScanThread(BluetoothDevice device, Event handler) {
            this.device = device;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                socket = device.createRfcommSocketToServiceRecord(RFCOMM_UUID);
                socket.connect();

                String readed = "";
                InputStream is = socket.getInputStream();
                while (!closing) {
                    byte[] b = new byte[1];

                    is.read(b);
//                    Log.d(TAG, "Read " + Integer.toHexString(b[0]));

                    if(b[0] == 0xd || b[0] == 0xa) {
                        handler.onRead(readed);
                        readed = "";
                    } else {
                        String s = new String(b);
                        readed += s;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                try{
                    if(socket != null )
                        socket.close();
                }catch(Exception e){}
            }
        }

        public void cancel() {
            try {
                thread = null;
                closing = true;
                if (socket != null)
                    socket.close();
            } catch (Exception e) { }
        }
    }
}
