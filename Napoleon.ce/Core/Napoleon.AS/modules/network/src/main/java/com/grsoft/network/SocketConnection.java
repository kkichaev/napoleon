/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Поддерживает соединение по сети
 *
 * kki   30/01/2011   creating
 */
package com.grsoft.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.util.Log;

import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.network.exception.RuntimeException;

public class SocketConnection implements IOStream
{
	public static int WinPort = 0;
	public static String WinAdr = "";
	
	private static final String TAG = "SocketConnection"; 
	protected Socket socket;
	
	private String strAddress;
	private int port;
	private int timeout = 6000;

	private String error = "";

	protected ByteStream received;
	
	public SocketConnection(String address, int port)
	{
		this.strAddress = address;
		this.port = port;
	}
	
	public void setWin() {
		WinPort = port;
		WinAdr = strAddress;
	}

	public ByteStream getReceived() { return received; }

	public ByteStream receive(Context context) throws RuntimeException {
		received = ByteStream.receive(getInputStream(), context);
		return  received;
	}

	public void send(DataObjectPool pool, String tag, Context context) throws RuntimeException {
		byte[] streamData = pool.toStreamData();
		ByteStream byteStream = new ByteStream(streamData, context);
		byteStream.send(getOutputStream(), tag);
		byteStream.close();
	}

	public boolean connect(){
		try {
			boolean ret = true;
			if(GRJSHelper.IsJSAddress(strAddress)) {
				socket = new Socket();
				ret = GRJSHelper.connect(socket, strAddress);
			} else {
				Log.d(TAG, "connect: " + strAddress);
				
				InetAddress address = InetAddress.getByName(strAddress);
				SocketAddress socketAddress = new InetSocketAddress(address, port);
				socket = new Socket();
				socket.connect(socketAddress, timeout);
			}
			return ret;
		}
		catch(Exception e) {
			error = e.getLocalizedMessage();
			e.printStackTrace();
			return false;
		}
	}

	public String getError() { return error; }
	
	@Override
	public InputStream getInputStream() throws RuntimeException
	{
		try
		{
			return socket.getInputStream();
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}

	@Override
	public OutputStream getOutputStream() throws RuntimeException
	{
		try
		{
			return socket.getOutputStream();
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public void close() 
	{
		try{
			if (socket != null)
				socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
