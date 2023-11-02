package com.grsoft.napoleon.modules.print.util;
import com.grsoft.aceteam.R;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.modules.print.TextPrinter;

public class BTPrinterHelper {
    public static final UUID RFCOMM_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	static final String PREF_NAME = "BTPrinterSettings";
	private static final String COPIES = "Copies";
	private static final String ADDRESS = "Address";
	private static final String NAME = "Name";
	private static final String ROW_COUNT = "RowCount";
	
	public static void printing(String address, int copies, String fileName, Context context ) {
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

		if( ba == null )
			return;
		
		ba.cancelDiscovery();
		boolean paired = false; 
		for (BluetoothDevice device : ba.getBondedDevices()) {
			if(device.getAddress().equals(address)) {
				paired = true;
				Thread t = new PrintingThread(device, copies, fileName);
				t.start();
				break;
			}
		}
		
		if( !paired ) {
			Toast.makeText(context, R.string.device_no_paired, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static BTPrinterSettings getSettings(Context context) {
		BTPrinterSettings ret = new BTPrinterSettings();
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		ret.copies = sp.getInt(COPIES, 1);
		ret.address = sp.getString(ADDRESS, "");
		ret.name = sp.getString(NAME, "");
		ret.row_count = sp.getInt(ROW_COUNT, TextPrinter.PAGE_ROW_COUNT);
		return ret;
	}
	
	public static void saveSettings(BTPrinterSettings settings, Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(COPIES, settings.copies);
		editor.putString(ADDRESS, settings.address);
		editor.putString(NAME, settings.name);
		editor.putInt(ROW_COUNT, settings.row_count);
		editor.commit();
	}
}

class PrintingThread extends Thread {
    private static final String TAG = "ConnectThread";

    private String fileName;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	
	int copies;
 
    public PrintingThread(BluetoothDevice device, int copies, String fileName) {
        this.fileName = fileName;
        this.device = device;
        this.copies = copies;
    }
 
    public void run() {
        
        try {
        	socket = device.createRfcommSocketToServiceRecord(BTPrinterHelper.RFCOMM_UUID);
            socket.connect();

            final int CPY_BUF_SIZE = 250; //symbols in second according to specification of EPSON LX-300+II printer
            final int SLEEP_TIME_TEXT = 1000; //send 250 symbols to print, wait while printing is in progress
            final int SLEEP_TIME_COPIES = 5000; // 5 seconds for changing the sheet of paper
            
            OutputStream out = socket.getOutputStream();
    	    byte[] buf = new byte[CPY_BUF_SIZE];
    	    int len = 0;
    	    
    	    do {
	            FileInputStream in = new FileInputStream(fileName);
	    	    while ((len = in.read(buf)) > 0) {
	    	        out.write(buf, 0, len);
	    	        SystemClock.sleep(SLEEP_TIME_TEXT);
	    	    }
	    	    in.close();
	    	    out.flush();
	    	    --copies;
	    	    
				SystemClock.sleep(SLEEP_TIME_COPIES);
    	    } while(copies > 0);

    	    out.close();
			SystemClock.sleep(SLEEP_TIME_COPIES);

			socket.close();
            Log.d(TAG, "Printed SUCCESS");
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
			if (socket != null)
				socket.close();
		} catch (Exception e) { }
	}
}