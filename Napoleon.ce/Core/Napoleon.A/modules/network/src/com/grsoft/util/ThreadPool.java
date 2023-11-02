package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.network.ConnectionManager;
import com.grsoft.network.SocketConnection;

import android.util.Log;

public class ThreadPool implements Runnable{
	private static final String TAG = "ThreadPool"; 
	private List<Thread> pool = new ArrayList<Thread>();
	private Object monitor;
	private RunnableArgs process;
	public Thread winner;
	private boolean running;
	int leftThread;
	
	public ThreadPool(RunnableArgs process, Object monitor, ConnectionManager cman, SocketConnection activeConnect) {
		this.monitor = monitor;
		this.process = process;
		this.leftThread = cman.getCount();
		
		makePool(cman, activeConnect);
	}

	public ThreadPool(RunnableArgs process, ConnectionManager cman, Object monitor) {
		this(process, monitor, cman, null);
	}

	private void makePool(ConnectionManager cman, SocketConnection activeConnect) {
		if(activeConnect != null) {
			pool.add(new DataThread(activeConnect, this));
		} else {
			for(int i = 0; i < leftThread; i++){
				pool.add(new DataThread(cman.get(i), this));
			}
		}
	}
	
	public void start(){
		running = true;
		
		for(Thread t : pool)
			t.start();
		
		synchronized (monitor) {
			while(running)
				try{
					monitor.wait();
				}catch(Exception e){
					e.printStackTrace();
				}
		}
	}

	@Override
	public void run() {
		try{
			Log.d(TAG, "Thread: " + Thread.currentThread().getName() + " start begin");
			
			if ((Boolean) process.run())
				notifyMonitor();
			
			Log.d(TAG, "Thread: " + Thread.currentThread().getName() + " start end");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			leftThreadNotify();
		}
	}

	private void leftThreadNotify() {
		synchronized (monitor) {
			leftThread--;
			
			if (leftThread == 0){
				monitor.notify();
				running = false;
			}
		}
	}

	private void notifyMonitor() {
		synchronized (monitor) {
			if (winner == null){
				winner = Thread.currentThread();
			}
			
			monitor.notify();
			running = false;
		}
	}
	
	public Thread getWinner(){
		return winner;
	}
}
