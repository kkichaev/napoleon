package com.grsoft.napoleon.rfid;

import com.senter.support.openapi.StUhf.UII;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class RfidHelper {
	
	static boolean scanning = false;
	
	public interface Handler {
		void recievedRFID(String rfid);
	}
	
	public static boolean isScanning() { return scanning; }

	public static Dialog createWaitDialog(Context context) {
		ProgressDialog result = new ProgressDialog(context);
		result.setMessage("Сканирование RFID-меток");
		result.setButton(Dialog.BUTTON_POSITIVE, "Стоп", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
		});
		
		result.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				scanning = false;
				Rfid.close();
			}
		});
		return result;
	}
	
	public static void startScanning(final Handler handler, Context context) {
		scanning = true;
		Rfid.startInventory(context, new com.senter.support.openapi.StUhf.OnNewUiiInventoried() {
			@Override public void onNewUiiReceived(UII arg0) { 
				StringBuilder sb = new StringBuilder();
				for(byte b : arg0.getBytes()) {
					sb.append(String.format("%02X", b));
				}
				handler.recievedRFID(sb.toString()); 
		}});
	}
	
	public static void stopScanning() { Rfid.close(); }
}