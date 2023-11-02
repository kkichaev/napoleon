package com.grsoft.util;

import com.grsoft.network.ByteStream;

public class DataThread extends Thread {
	private int index;
	private ByteStream byteStream;
	
	public DataThread(int index, Runnable run){
		super(run);
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}

	public void setByteStream(ByteStream byteStream) {
		this.byteStream = byteStream;
	}

	public ByteStream getByteStream() {
		return byteStream;
	}
}
