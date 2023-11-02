/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Класс обеспечивающий запись объектов по сети
 *
 * kki   30/01/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.CommandArgs;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.ForcePutCommandArgs;
import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UploadException;
import com.grsoft.util.DataThread;
import com.grsoft.util.Debug;
import com.grsoft.util.RunnableArgs;
import com.grsoft.util.ThreadPool;

public class WriteServiceBase {
	protected ServerAnswerHitching serverAnswerHitching = new ServerAnswerHitching();
	protected List<ObjectListener> objectsToSend;
	//protected List<Hitching> recieveHitch;
	protected List<Hitching> requestHitchs;
	protected UpdateProcessListener updateProcessListener;
	protected StreamReader reader;
	protected String message = "";
	protected SocketConnection winConnect = null;
//	private boolean rcvRemnants = false;

	boolean closeConnection = true;

	protected int sendedBytes = 0;

	public void closeConnection() {
		if(winConnect != null) {
			winConnect.close();
		}
	}

	public void setUpdateProcessListenet(UpdateProcessListener listener) {
		updateProcessListener = listener;
	}
	
	protected void fireUpdate(UpdateStatus status, int progress) {
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

//	public boolean sendPhotos(Context context, UserInfo userInfo, List<ObjectExportListener> objects, SocketConnection conn, boolean needPackObjects) {
//		if(conn == null) {
//			return false;
//		}
//
//		boolean result = false;
//		try{
//			DataObjectPool dataObjectPool = new DataObjectPool();
//			ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
//			serverCommand.setCommandParams(new ForcePutCommandArgs(userInfo.impersonate));
//			dataObjectPool.add(serverCommand);
//			for(ObjectExportListener ol : objects) {
//				for( int i=0; i < ol.size(); i++)
//					dataObjectPool.add(ol.get(i), ol.getObjectName());
//			}
//
//			byte[] streamData = dataObjectPool.toStreamData();
//			ByteStream byteStream = new ByteStream(streamData, context);
//			String tag = needPackObjects ? ByteStream.GZIP_TAG : ByteStream.CRC_TAG;
//			sendedBytes += byteStream.send(conn.getOutputStream(), tag);
//			ByteStream outStream = ByteStream.receive(conn.getInputStream(), context);
//			if (outStream == null) {
//				conn.close();
//				message = context.getString(R.string.server_not_approved);
//			} else {
//				serverAnswerHitching.setObjects(objectsToSend);
//				reader.read(outStream);
//				outStream.close();
//
//				if (!serverAnswerHitching.IsOK()) {
//					message = context.getString(R.string.cant_write_object) + " " + serverAnswerHitching.getMessage();
//				} else {
//					result = true;
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		return result;
//	}

	public boolean write(Context context, UserInfo userInfo) {
		fireUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_SEND, 0);
		ConnectionHelper.Result cres = ConnectionHelper.getConnection(userInfo);
		if(cres.error != null || cres.connection == null) {
			message = cres.error;
			return false;
		}

		boolean res = false;
		SocketConnection conn = null;
		try {
			conn = cres.connection;
			conn.setWin();

			DataObjectPool dataObjectPool = new DataObjectPool();
			ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
			serverCommand.setCommandParams(new ForcePutCommandArgs(userInfo.impersonate));
			dataObjectPool.add(serverCommand);

			addObjectsToSend(dataObjectPool, userInfo);
			dataObjectPool.closeUpload();

			reader.addHitching(requestHitchs);

			byte[] streamData = dataObjectPool.toStreamData();
			ByteStream byteStream = new ByteStream(streamData, context);
			sendedBytes += byteStream.send(conn.getOutputStream(), ByteStream.GZIP_TAG);

			fireUpdate(UpdateProcessInfo.UpdateStatus.ENDREQUEST_SEND, 1);

			ByteStream bs = ByteStream.receive(conn.getInputStream(), context);
			if(bs == null) {
				message = context.getString(R.string.server_not_approved);
			} else {
				serverAnswerHitching.setObjects(objectsToSend);
				reader.read(bs);
				sendByeCommanToCloseSession(userInfo, conn, context);
				bs.close();

				if (!serverAnswerHitching.IsOK()){
					message = context.getString(R.string.cant_write_object) + " " + serverAnswerHitching.getMessage();
				} else {
					res = true;

					for(ObjectListener ol : objectsToSend) {
						if (ol instanceof DocExportListener){
							DocList list = ((DocExportListener)ol).getDocuments();
							list.close();
						}
					}
				}
				fireUpdate(UpdateProcessInfo.UpdateStatus.END, 3);
			}
		} catch (RuntimeException e) {
			Debug.dbgPrint(e.getMessage());
			e.printStackTrace();
			message = e.getInnerException().getMessage();
		} catch (UploadException e) {
			e.printStackTrace();
			message = e.getMessage();
		}finally{
			if(conn != null)
				conn.close();
		}

		return res;
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
	
	protected void addObjectsToSend(DataObjectPool dataObjectPool, UserInfo userInfo) throws RuntimeException, UploadException {
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

//	private DataObjectPool createDataObjectPool(UserInfo userInfo) throws RuntimeException, UploadException {
//		DataObjectPool dataObjectPool = new DataObjectPool();
//
//		addObjectsToSend(dataObjectPool, userInfo);
//
//		reader.addHitching(requestHitchs);
//		return dataObjectPool;
//	}
//
	public String getMessage(){	return message; }
	
	public void addRecieveHitch(Hitching hitching){
		reader.addHitching(hitching);
	}
}
