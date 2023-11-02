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
import com.grsoft.dataobjects.CommandArgs;
import com.grsoft.dataobjects.DataObjectPool;
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
	private List<? extends ObjectListener> objectsToSend;
	protected List<Hitching> recieveHitch;
	protected UpdateProcessListener updateProcessListener;
	StreamReader reader;
	private String message = "";
	private boolean rcvRemnants = false;
	
	private int sendedBytes = 0;

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
		this.objectsToSend = objectsToSend;
		
		recieveHitch = new ArrayList<Hitching>();
		recieveHitch.add(serverAnswerHitching);
		
		reader = new StreamReader(recieveHitch);
		
		this.rcvRemnants = rcvRemnants;
	}

	class ConnectProcess implements RunnableArgs{
		public UserInfo userInfo;
		
		@Override
		public Object run() {
			boolean result = false;
			
			try{
				DataThread t = (DataThread) Thread.currentThread();
				SocketConnection conn = ConnectionManager.getInstance().get(t.getIndex());
				
				if(conn.connect()){
					DataObjectPool dataObjectPool = new DataObjectPool();
					ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
					serverCommand.setCommandParams(new PutCommandArgs());
					dataObjectPool.add(serverCommand);
					
					byte[] streamData = dataObjectPool.toStreamData();
					ByteStream byteStream = new ByteStream(streamData);
					sendedBytes += byteStream.send(conn.getOutputStream(), ByteStream.GZIP_TAG);
					
					ByteStream bs = ByteStream.receive(conn.getInputStream());
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
	
	public boolean write(Context context, UserInfo userInfo) 
	{
		fireUpdate(UpdateStatus.BEGIN_SEND, 0);
		final int THREAD_COUNT = 2;
		process.userInfo = userInfo;
		
		ThreadPool threadPool = new ThreadPool(process, this, THREAD_COUNT);
		threadPool.start();
		DataThread winner = (DataThread) threadPool.getWinner();
			
		ConnectionManager cman = ConnectionManager.getInstance(); 
		
		if (winner == null){
			message = context.getString(R.string.cant_connect_server);
			cman.endSession();
			return false;
		}
			
		ByteStream stream = winner.getByteStream();
			
		try
		{
			reader.read(stream);
			
			if (!serverAnswerHitching.IsOK()){
				message = serverAnswerHitching.getServerAnsver().message;
				return false;
			}
			
			fireUpdate(UpdateStatus.ENDREQUEST_SEND, 1);
			
			DataObjectPool dataObjectPool = createDataObjectPool(userInfo);
			
			if (dataObjectPool.isEmpty())
				return true;
			
			SocketConnection conn = ConnectionManager.getInstance().get(winner.getIndex());
			byte[] streamData = dataObjectPool.toStreamData();
			ByteStream byteStream = new ByteStream(streamData);
			sendedBytes += byteStream.send(conn.getOutputStream(), ByteStream.GZIP_TAG);
			ByteStream outStream = ByteStream.receive(conn.getInputStream());
			
			if(outStream == null)
			{
				message = context.getString(R.string.server_not_approved);
				return false;
			}
			
			sendByeCommanToCloseSession(userInfo, conn);
			serverAnswerHitching.setObjects(objectsToSend);
			reader.read(outStream);		
			
			fireUpdate(UpdateStatus.STEP_SEND, 2);
			
			if (!serverAnswerHitching.IsOK()){
				message = serverAnswerHitching.getMessage();
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
			
			cman.get(winner.getIndex()).close();
			cman.endSession();
		}
	}
	
	public int getSendedBytes() { return sendedBytes; }

	private void sendByeCommanToCloseSession(UserInfo userInfo,
			SocketConnection conn) throws RuntimeException {
		// send bye command (для закрытия сессии на сервере)
		ByteStream bye = new ByteStream(ByeCommand.dbPool((LoginData) userInfo).toStreamData());
		bye.send(conn.getOutputStream(), "");
	}
		
	class PutCommandArgs implements CommandArgs {
		@Override public String getCommand() { return "PUT"; }
		@Override public String getParams() throws RuntimeException { return new String(); }	
	}
	
	class GetCommand implements CommandArgs {
		String objectName;
		public GetCommand(String objectName) { this.objectName = objectName; }
		@Override public String getCommand() { return "GET"; }
		@Override public String getParams() throws RuntimeException { return objectName; }	
	}

	private DataObjectPool createDataObjectPool(UserInfo userInfo) throws RuntimeException {
		DataObjectPool dataObjectPool = new DataObjectPool();
		
		for(ObjectListener ol : objectsToSend)
		{
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
		
		if(rcvRemnants) {
			ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
			serverCommand.setCommandParams(new GetCommand("PriceRemnants"));
			dataObjectPool.add(serverCommand);
		}				
		
		return dataObjectPool;
	}
	
	public String getMessage(){	return message; }
	
	public void addRecieveHitch(Hitching hitching){
		recieveHitch.add(hitching);
	}
}
