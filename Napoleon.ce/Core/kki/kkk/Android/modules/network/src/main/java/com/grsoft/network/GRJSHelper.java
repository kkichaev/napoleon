package com.grsoft.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Arrays;

import android.util.JsonReader;

public class GRJSHelper {
	static String error = "";
	
    static String JS_PACKET_TAG = "GRJS";
    static String CLIENT_CONNECT_CMD = "CLTC";
    static String OK_CMD = "OKCM";
    static String REJECT_CMD = "CLTC";

	static String serverIP = "51.68.175.104";
	static int serverPort = 9595;

	static SocketAddress getServerAddress(String serverID) {
    	SocketAddress retAddr = null;
    	try {
			String url = "https://grsoft.ru/grjs/grjs.php?server=" + serverID.substring(5);
			URL addr = new URL(url);
			HttpURLConnection  conn = (HttpURLConnection)addr.openConnection();
			InputStream in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String retSrc = "";
			String line;
			while((line = reader.readLine()) != null) {
				retSrc += line;
			}
			reader.close();
			
			int port = 0;
			String ip = "";
			InputStream stream = new ByteArrayInputStream(retSrc.getBytes());
			JsonReader r = new JsonReader(new InputStreamReader(stream));
			r.beginObject();
			while(r.hasNext()) {
				String name = r.nextName();
				if(name.equals("port")) {
					port = r.nextInt(); 
				} else if(name.equals("addr")) {
					ip = r.nextString();
				}
			}
			r.endObject();
			r.close();
			
			if(ip.length() > 0 && port != 0) {
				serverIP = ip;
				serverPort = port;
		        InetAddress address = InetAddress.getByName(ip);
				retAddr = new InetSocketAddress(address, port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return retAddr;
    }
	
	public static boolean IsJSAddress(String addr) {
		return addr != null && addr.toUpperCase().startsWith("GRJS.");
	}

    static byte[] ToBigEndianBytes(int x){
       return new byte[] {
       (byte)((x >> 24) & 0xff),
       (byte)((x >> 16) & 0xff),
       (byte)((x >> 8) & 0xff),
       (byte)(x & 0xff)
       };
    }
	
    static int FromBytes(byte[] buf, int offset) {
       return (buf[offset] << 24) | (buf[offset + 1] << 16) | (buf[offset + 2] << 8) | (buf[offset + 3]);
    }
    
	static boolean SendCommand(Socket socket, String cmd, int id) {
       boolean ret = true;
       byte[] data = new byte[4 * 4];

       error = "";

       int cp = 0;
       System.arraycopy(JS_PACKET_TAG.getBytes(), 0, data, cp, 4);
       cp += 4;
       System.arraycopy(cmd.getBytes(), 0, data, cp, 4);
       cp += 4;
       System.arraycopy(ToBigEndianBytes(id), 0, data, cp, 4);
       cp += 4;
       System.arraycopy(ToBigEndianBytes(0), 0, data, cp, 4);

       try {
    	   socket.getOutputStream().write(data);
		   socket.getInputStream().read(data);
		   
		   String answ = new String(Arrays.copyOfRange(data, 4, 4));
		   if(answ == REJECT_CMD) {
		      ret = false;
		      int len = FromBytes(data, 12);
		      if(len > 0)
		      {
		         data = new byte[len];
		         socket.getInputStream().read(data);
		         error = new String(data);
		      }
		   }
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}

       return ret;
    }

	public static boolean connect(Socket socket, String strAddress) {
		boolean ret = true;
		error = "";

		String[] parts = strAddress.split("\\.");
		if (parts.length != 5)
			return false;

		int id = 0;
		for (int i = 1; i < 5; i++) {
			id <<= 8;
			try {
				int val = Integer.parseInt(parts[i]);
				id |= val;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		try {
			SocketAddress socketAddress;
			if(serverIP.isEmpty()) {
				socketAddress = getServerAddress(strAddress);
			} else {
				InetAddress address = InetAddress.getByName(serverIP);
				socketAddress = new InetSocketAddress(address, serverPort);
			}

			if(socketAddress != null) {
				socket.connect(socketAddress);
		        ret = SendCommand(socket, CLIENT_CONNECT_CMD, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
			serverIP = "";
		}
		
		return ret;
	}
}
