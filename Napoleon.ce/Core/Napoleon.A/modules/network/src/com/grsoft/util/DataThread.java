package com.grsoft.util;

import com.grsoft.network.ByteStream;
import com.grsoft.network.SocketConnection;

public class DataThread extends Thread {
	private ByteStream byteStream;
	private SocketConnection connection;
	
	public DataThread(SocketConnection connection, Runnable run){
		super(run);
		this.connection = connection;
	}
	
	public SocketConnection getConenction(){
		return connection;
	}

	public void setByteStream(ByteStream byteStream) {
		this.byteStream = byteStream;
	}

	public ByteStream getByteStream() {
		return byteStream;
	}
}
