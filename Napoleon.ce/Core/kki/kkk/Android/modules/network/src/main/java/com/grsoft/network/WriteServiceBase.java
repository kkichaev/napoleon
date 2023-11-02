/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Класс обеспечивающий запись объектов по сети
 *
 * kki   30/01/2011   creating
 */
package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.CommandArgs;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.ForcePutCommandArgs;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.DataThread;
import com.grsoft.util.Debug;
import com.grsoft.util.RunnableArgs;
import com.grsoft.util.ThreadPool;

public class WriteServiceBase {
	private ServerAnswerHitching serverAnswerHitching = new ServerAnswerHitching();
	protected List<ObjectListener> objectsToSend;
	//protected List<Hitching> recieveHitch;
	protected List<Hitching> requestHitchs;
	protected UpdateProcessListener updateProcessListener;
	protected StreamReader reader;
	private String message = "";
	protected SocketConnection winConnect = null;
//	private boolean rcvRemnants = false;

	boolean closeConnection = true;

	private int sendedBytes = 0;

	public void setCloseConnection(boolean closeCon) { this.closeConnection = closeCon; }

	public void closeConnection() {
		if(winConnect != null) {
			winConnect.close();
		}
	}

	public void setUpdateProcessListenet(UpdateProcessListener listener)
	{
		updateProcessListener = listener;
	}
	
	protected void fireUpdate(UpdateStatus status, int progress)
	{
		if (updateProcessListener != null)
			updateProcessListener.onUpdate(status, progress);
	}
	
	public WriteServiceBase(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants)	{
		this.objectsToSend = new ArrayList<ObjectListener>();
		
		if(objectsToSend != null)
			this.objectsToSend.addAll(objectsToSend);
		
		List<Hitching> recieveHitch = new ArrayList<Hitching>();
		recieveHitch.add(serverAnswerHitching);
		
		reader = new StreamReader(recieveHitch);
		
//		this.rcvRemnants = rcvRemnants;
		this.requestHitchs = new ArrayList<Hitching>();
	}

	class ConnectProcess implements RunnableArgs{
		public UserInfo userInfo;
		public Context context;
		
		@Override
		public Object run() {
			boolean result = false;
			
			try{
				DataThread t = (DataThread) Thread.currentThread();
				SocketConnection conn = t.getConenction();
				
				if(conn.connect()){
					DataObjectPool dataObjectPool = new DataObjectPool();
					ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
					serverCommand.setCommandParams(new PutCommandArgs(userInfo.impersonate));
					dataObjectPool.add(serverCommand);
					
					byte[] streamData = dataObjectPool.toStreamData();
					ByteStream byteStream = new ByteStream(streamData, context);
					sendedBytes += byteStream.send(conn.getOutputStream(), ByteStream.GZIP_TAG);
					
					ByteStream bs = ByteStream.receive(conn.getInputStream(), context);
					if(bs != null) {
						t.setByteStream(bs);
						result = true;
					}
				}else
					result= false;
			}catch(Exception e){
				result = false;
			}
			
			return result;
		}
		
	}
	private ConnectProcess process = new ConnectProcess();
	
	public SocketConnection getActiveConnection() { return winConnect; }
	public void setActiveConnection(SocketConnection conn) { winConnect = conn; }

	
	public boolean forcePut(Context context, UserInfo userInfo, List<ObjectExportListener> objects, SocketConnection conn, boolean needPackObjects) {
		boolean result = false;
		
		if(conn == null) {
			if(objectsToSend == null)
				objectsToSend = new ArrayList<ObjectListener>();
			
			objectsToSend.clear();
			objectsToSend.addAll(objects);
			return write(context, userInfo);
		}

		try{
			DataObjectPool dataObjectPool = new DataObjectPool();
			ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
			serverCommand.setCommandParams(new ForcePutCommandArgs(userInfo.impersonate));
			dataObjectPool.add(serverCommand);
			for(ObjectExportListener ol : objects) {
				for( int i=0; i < ol.size(); i++)
					dataObjectPool.add(ol.get(i), ol.getObjectName());
			}

			if(closeConnection) {
				if(!conn.connect()) {
					return false;
				}
			}

			byte[] streamData = dataObjectPool.toStreamData();
			ByteStream byteStream = new ByteStream(streamData, context);
			String tag = needPackObjects ? ByteStream.GZIP_TAG : ByteStream.CRC_TAG;  
			sendedBytes += byteStream.send(conn.getOutputStream(), tag);
			ByteStream outStream = ByteStream.receive(conn.getInputStream(), context);
			
			if(outStream == null) {
				conn.close();
				message = context.getString(R.string.server_not_approved);
				return false;
			}

			if(closeConnection)
				sendByeCommanToCloseSession(userInfo, conn, context);
			serverAnswerHitching.setObjects(objectsToSend);
			reader.read(outStream);
			outStream.close();
			if(closeConnection) {
				conn.close();
			}

			if (!serverAnswerHitching.IsOK()){
				message = context.getString(R.string.cant_write_object) + " " + serverAnswerHitching.getMessage();
				return false;
			}
			
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean write(Context context, UserInfo userInfo) 
	{
		fireUpdate(UpdateStatus.BEGIN_SEND, 0);
		ConnectionManager cman = ConnectionManager.getInstance(); 
		cman.createPool(userInfo);
		
		process.userInfo = userInfo;
		process.context = context; 
		
		if(userInfo.impersonate.trim().length() > 0)
			ServerCommand.Category = "managerPDA";
		
		ThreadPool threadPool = new ThreadPool(process, this, cman, winConnect);
		threadPool.start();
		DataThread winner = (DataThread) threadPool.getWinner();
		
		if (winner == null){
			message = context.getString(R.string.cant_connect_server);
			cman.endSession();
			return false;
		}
			
		winConnect = winner.getConenction();
		winConnect.setWin();

		ByteStream stream = winner.getByteStream();
			
		try
		{
			reader.read(stream);
			stream.close();
			
			if (!serverAnswerHitching.IsOK()){
				message = serverAnswerHitching.getMessage();
				return false;
			}
			
			fireUpdate(UpdateStatus.ENDREQUEST_SEND, 1);
			
			DataObjectPool dataObjectPool = createDataObjectPool(userInfo);
			
			if (dataObjectPool.isEmpty())
				return true;
			
			byte[] streamData = dataObjectPool.toStreamData();
			ByteStream byteStream = new ByteStream(streamData, context);
			String tag = Features.ZIP_PACKET ? ByteStream.GZIP_TAG : ByteStream.CRC_TAG;  
			sendedBytes += byteStream.send(winConnect.getOutputStream(), tag);
			ByteStream outStream = ByteStream.receive(winConnect.getInputStream(), context);
			
			if(outStream == null)
			{
				message = context.getString(R.string.server_not_approved);
				return false;
			}
			
			serverAnswerHitching.setObjects(objectsToSend);
			reader.read(outStream);	
			outStream.close();
			if(reader.haveServerData()) {
				sendServerInfo(reader, winConnect, userInfo, context);
			}
			if(closeConnection)
				sendByeCommanToCloseSession(userInfo, winConnect, context);
			
			fireUpdate(UpdateStatus.STEP_SEND, 2);
			
			if (!serverAnswerHitching.IsOK()){
				message = context.getString(R.string.cant_write_object) + " " + serverAnswerHitching.getMessage();
				return false;
			}
			
			fireUpdate(UpdateStatus.END, 3);
			
			for(ObjectListener ol : objectsToSend) {
				if (ol instanceof DocExportListener){
					DocList list = ((DocExportListener)ol).getDocuments();
					list.close();
				}
			}
			
			return true;
		} catch (RuntimeException e)
		{
			Debug.dbgPrint(e.getMessage());
			e.printStackTrace();
			
			message = e.getInnerException().getMessage();
			return false;
		}finally{
			if(closeConnection)
				winConnect.close();
			cman.endSession();
		}
	}
	
	private void sendServerInfo(StreamReader reader, SocketConnection winConnect, UserInfo userInfo, Context context) {
		try {
			DataObjectPool dataObjectPool = new DataObjectPool();
			ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
			serverCommand.setCommandParams(new ForcePutCommandArgs(userInfo.impersonate));
			dataObjectPool.add(serverCommand);
			
			List<ObjectExportListener> objects = reader.getServerObjects();
			for(ObjectExportListener ol : objects) {
				for( int i=0; i < ol.size(); i++)
					dataObjectPool.add(ol.get(i), ol.getObjectName());
			}
			
			byte[] streamData = dataObjectPool.toStreamData();
			ByteStream byteStream = new ByteStream(streamData, context);
			String tag = ByteStream.GZIP_TAG;  
			byteStream.send(winConnect.getOutputStream(), tag);
			ByteStream outStream = ByteStream.receive(winConnect.getInputStream(), context);
			
			if(outStream != null) {
				reader.read(outStream);
			}
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getSendedBytes() { return sendedBytes; }

	public void sendByeCommanToCloseSession(UserInfo userInfo, SocketConnection conn, Context context) throws RuntimeException {
		if(conn != null) {
			// send bye command (для закрытия сессии на сервере)
			ByteStream bye = new ByteStream(ByeCommand.dbPool((LoginData) userInfo).toStreamData(), context);
			bye.send(conn.getOutputStream(), "");
			bye.close();
		}
	}
		
	class PutCommandArgs implements CommandArgs {
		String impersonate;
		
		public PutCommandArgs(String impersonate){
			this.impersonate = impersonate;
		}
		
		@Override public String getCommand() { return impersonate.length() == 0 ? "PUT" : "PUT AS '" + impersonate + "'"; }
		@Override public String getParams() throws RuntimeException { return new String(); }	
	}

	class GetCommand implements CommandArgs {
		String objectName;
		public GetCommand(String objectName) { this.objectName = objectName; }
		@Override public String getCommand() { return "GET"; }
		@Override public String getParams() throws RuntimeException { return objectName; }	
	}
	
	private void addObjectsToSend(DataObjectPool dataObjectPool, UserInfo userInfo) throws RuntimeException {
		for(ObjectListener ol : objectsToSend) {
			if (ol instanceof DocExportListener){
				DocList list = ((DocExportListener)ol).getDocuments();
				for( int i=0; i<list.getCount(); i++) {
					Document<?> d = list.get(i);
					if( d != null )
						dataObjectPool.add(d.getData(), ol.getObjectName()); 
				}
			} else if (ol instanceof ObjectExportListener) {
				ObjectExportListener oel = (ObjectExportListener)ol;
				for( int i=0; i < oel.size(); i++){
					dataObjectPool.add(oel.get(i), ol.getObjectName());
				}
			}
		}

		for(Hitching h : requestHitchs) {
			ServerCommand sc = new ServerCommand((LoginData)userInfo);
			sc.setCommandParams(h);
			dataObjectPool.add(sc);
		
			if( h instanceof ReportHitching )
				((ReportHitching)h).addReport(dataObjectPool, reader.getHitchings());
		}
	}

	private DataObjectPool createDataObjectPool(UserInfo userInfo) throws RuntimeException {
		DataObjectPool dataObjectPool = new DataObjectPool();
		
		addObjectsToSend(dataObjectPool, userInfo);
		
		reader.addHitching(requestHitchs);
		return dataObjectPool;
	}
	
	public String getMessage(){	return message; }
	
	public void addRecieveHitch(Hitching hitching){
		reader.addHitching(hitching);
	}
}
