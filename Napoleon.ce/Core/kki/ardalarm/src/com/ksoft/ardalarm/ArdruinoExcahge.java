package com.ksoft.ardalarm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class ArdruinoExcahge extends Service {
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private ConnectedThread connectedThread;
	final int RECIEVE_MESSAGE = 1;
	Handler h;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				StringBuilder sb = new StringBuilder();
				switch (msg.what) {
				case RECIEVE_MESSAGE: // если приняли сообщение в Handler
					byte[] readBuf = (byte[]) msg.obj;
					String strIncom = new String(readBuf, 0, msg.arg1);
					
					if (strIncom.trim().equals("1")) {
						Intent intent = new Intent(ArdruinoExcahge.this,
								SoundPlay.class);
						stopService(intent);
					}
						
					
//					Intent i = new Intent("status");
//					i.putExtra("status", "Recieved: " + strIncom);
//					sendBroadcast(i);
//					
//					sb.append(strIncom); // формируем строку
//					int endOfLineIndex = sb.indexOf("\r\n"); // определяем
//																// символы конца
//																// строки
//					if (endOfLineIndex > 0) { // если встречаем конец строки,
//						String sbprint = sb.substring(0, endOfLineIndex); // то
//																			// извлекаем
//																			// строку
//						if (sbprint.equals("1")) {
//							Intent intent = new Intent(ArdruinoExcahge.this,
//									SoundPlay.class);
//							stopService(intent);
//						}
//
//					}

					stopSelf();
					break;
				}
			};
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				btAdapter = BluetoothAdapter.getDefaultAdapter();

				try {
					BluetoothDevice device = btAdapter
							.getRemoteDevice(getSharedPreferences(
									Setting.SHARED_PREFERENCES_NAME,
									Context.MODE_PRIVATE).getString(
									Setting.ARD_ADDR, ""));

					btSocket = device
							.createRfcommSocketToServiceRecord(MY_UUID);
					btAdapter.cancelDiscovery();
					btSocket.connect();
					connectedThread = new ConnectedThread(btSocket);
					connectedThread.start();
					connectedThread.write("1");
//					Intent i = new Intent("status");
//					i.putExtra("status", "Connection success!");
//					sendBroadcast(i);
					// Toast.makeText(ArdruinoExcahge.this,
					// "Connection success!", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Intent i = new Intent("status");
					i.putExtra("status", e.getMessage());
					sendBroadcast(i);
					// Toast.makeText(ArdruinoExcahge.this, e.getMessage(),
					// Toast.LENGTH_LONG).show();
					e.printStackTrace();
					stopSelf();
				}
			}
		}).run();

	}

	@Override
	public void onDestroy() {
		try {
			if (btSocket != null)
				btSocket.close();
		} catch (IOException e) {}
		super.onDestroy();
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[256];
			int bytes;

			while (true) {
				try {
					bytes = mmInStream.read(buffer);
					h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
							.sendToTarget();
				} catch (IOException e) {
					break;
				}
			}

		}

		public void write(String message) {
			byte[] msgBuffer = message.getBytes();
			try {
				mmOutStream.write(msgBuffer);
			} catch (IOException e) {
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
