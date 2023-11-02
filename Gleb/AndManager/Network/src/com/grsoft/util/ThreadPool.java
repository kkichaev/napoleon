package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ThreadPool implements Runnable{
	private static final String TAG = "ThreadPool"; 
	private List<Thread> pool = new ArrayList<Thread>();
	private Object monitor;
	private RunnableArgs process;
	public Thread winner;
	private boolean running;
	int leftTrhread;
	
	public ThreadPool(RunnableArgs process, Object monitor, int count) {
		this.monitor = monitor;
		this.process = process;
		this.leftTrhread = count;
		
		makePool(count);
	}

	private void makePool(int count) {
		for(int i = 0; i < count; i++){
			pool.add(new DataThread(i, this));
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
			leftTrhread--;
			
			if (leftTrhread == 0){
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
