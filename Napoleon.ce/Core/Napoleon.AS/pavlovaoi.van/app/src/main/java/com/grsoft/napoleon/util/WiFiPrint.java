package com.grsoft.napoleon.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

public class WiFiPrint {

	static PrnThread thread = null;
	static Semaphore semaphore = new Semaphore(1);
	
	public static void print(WiFiPrinterConfig cfg, Activity context, String fileName) {
		// во время закрытия сокета взводим семафор
		accureSemaphor();
		
		if( thread == null ) {
			thread = new PrnThread(cfg, context, fileName);
			thread.start();
		} else {
			thread.addFile(fileName);		}
		
		releaseSemaphore();
	}
	
	public static void accureSemaphor() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseSemaphore() { semaphore.release(); }
	public static void printerClosed() { thread = null;	}
}

class PrnThread extends Thread {
	Socket socket;
    ConcurrentLinkedQueue<String> files = new ConcurrentLinkedQueue<String>();
    Context context;
	
	WiFiPrinterConfig cfg;
	public PrnThread(WiFiPrinterConfig cfg, Activity context, String fileName) {
		this.cfg = cfg;
		this.context = context.getApplicationContext();
		addFile(fileName);
	}
	
	public void addFile(String fileName) {
		files.add(fileName);
	}
	
	static final int CPY_BUF_SIZE = 250; //symbols in second according to specification of EPSON LX-300+II printer
	static final int SLEEP_TIME_TEXT = 1000; //send 250 symbols to print, wait while printing is in progress
	static final int SLEEP_TIME_COPIES = 5000; // 5 seconds for changing the sheet of paper
	
	@Override
	public void run() {
		boolean accured = false;
        try {
			SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(cfg.ip), cfg.port);
        	socket = new Socket();
            socket.connect(socketAddress);

            OutputStream out = socket.getOutputStream();
            String fileName = files.poll();
            while( fileName != null ) {
            	sendFile(out, fileName);
            	fileName = files.poll();
            	
            	if( fileName == null ) {
            		// начинаем закрывать соединение
                    WiFiPrint.accureSemaphor();
            		accured = true;
                    // если дали еще один файл - печатаем его
                    fileName = files.poll();
                    if( fileName != null )
                    	WiFiPrint.releaseSemaphore();
                    else
                		break;
            	}
    			SystemClock.sleep(SLEEP_TIME_COPIES);
            }
            
            
    	    try {
				out.close();
				SystemClock.sleep(SLEEP_TIME_COPIES);

				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

        } catch (final Exception ex) {
        	Handler h = new Handler(context.getMainLooper());
        	h.post(new Runnable() {
				@Override public void run() { 
					Toast.makeText(context, "Ошибка печати: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
				}
			});
//        	context.runOnUiThread(new Runnable() {
//				@Override public void run() { MessageBox.showError(context, null, ex); }
//			});
        	ex.printStackTrace();
        	
        	try{
        		if(socket != null )
        			socket.close();
        	}catch(Exception e){}
		} finally {
			WiFiPrint.printerClosed();
			if( accured )
				WiFiPrint.releaseSemaphore();
        }
    }

	private void sendFile(OutputStream out, String file) throws FileNotFoundException, IOException {
		byte[] buf = new byte[CPY_BUF_SIZE];
		int len = 0;
		int copies = cfg.copies;
		
		do {
		    FileInputStream in = new FileInputStream(file);
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		        SystemClock.sleep(SLEEP_TIME_TEXT);
		    }
		    in.close();
		    out.flush();
		    --copies;
		    
			SystemClock.sleep(SLEEP_TIME_COPIES);
		} while(copies > 0);
	}
	
	public void cancel() {
        try {
        	if (socket != null)
        		socket.close();
        } catch (Exception e) { }
	}
}