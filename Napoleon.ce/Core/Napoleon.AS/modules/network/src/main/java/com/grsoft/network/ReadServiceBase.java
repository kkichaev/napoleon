/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Класс обеспечивающий чтения объектов из сети
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network;


import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.LoginHitching;
import com.grsoft.database.ManagerHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.ForcePutCommandArgs;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.encrypt.Encryptor;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.DataThread;
import com.grsoft.util.RunnableArgs;
import com.grsoft.util.ThreadPool;

public class ReadServiceBase{
	private static final String TAG = "ReadService"; 
	private Runnable postUpdateWorks = null;
	LoginHitching login = new LoginHitching();
	
	protected UpdateProcessListener updateProcessListener;
	protected List<Hitching> sendHitch;
	protected List<Hitching> recieveHitch;
	protected String message = "";
	protected SocketConnection winConnect = null;
	
	int receivedBytes = 0;
	
	public void setUpdateProcessListenet(UpdateProcessListener listener)
	{
		updateProcessListener = listener;
	}
	
	protected void fireUpdate(UpdateStatus status, int progress)
	{
		if (updateProcessListener != null)
			updateProcessListener.onUpdate(status, progress);
	}
	
	public ReadServiceBase(List<Hitching> hitchings)
	{
		this(hitchings, false, null);
	}
	
	public ReadServiceBase(List<Hitching> hitchings, boolean readAsManager, Context ctx) {
		hitchings.add(new Hitching(Config.class, "ServerConfig"));
		this.sendHitch = hitchings;
		recieveHitch = new ArrayList<Hitching>();
		recieveHitch.addAll(sendHitch);
		login = (readAsManager) ? new ManagerHitching(ctx) : new LoginHitching();
		recieveHitch.add(login);
	}
	
	public int getReceivedBytes () {  return receivedBytes; }
	
	class ConnectProcess implements RunnableArgs{
//		private static final String TAG = "ConnectProcess";
		public UserInfo userinfo;
		public Context context;
		
		@Override
		public Object run() {
			SocketConnection.WinAdr = "";
			
			DataThread t = (DataThread) Thread.currentThread();
			SocketConnection conn = t.getConenction();
			boolean result = false;
			
			try{
				if(conn.connect()){
					DataObjectPool pool = makeCommandPool(userinfo);
					conn.send(pool, ByteStream.GZIP_TAG, context);
//					sendRequest(conn, pool, ByteStream.GZIP_TAG, context);
					ByteStream bs = conn.receive(context);
					if( bs != null ) {
//						t.setByteStream(bs);
						receivedBytes += bs.getReceived();
						result = true;
					}else
						result = false;
				}else
					result = false;
			}catch(Exception e){
				e.printStackTrace();
				result = false;
			}
			
			return result;
		}
	}
	
	protected ConnectProcess process = createConnectProcess();

	protected ConnectProcess createConnectProcess() {
		return new ConnectProcess();
	}
	
	public SocketConnection getActiveConnection() { return winConnect; }
	public void setActiveConnection(SocketConnection conn) { winConnect = conn; }

	SocketConnection beginConnection(Context context, UserInfo userinfo) {
		ConnectionManager cman = ConnectionManager.getInstance();
		cman.createPool(userinfo);

		process.userinfo = userinfo;
		process.context = context;

		if(userinfo.impersonate.trim().length() > 0)
			ServerCommand.Category = "managerPDA";

		ThreadPool threadPool = new ThreadPool(process, this, cman, winConnect);
		threadPool.start();
		DataThread winner = (DataThread) threadPool.getWinner();

		cman.endSession();

		return winner == null ? null : winner.getConenction();
	}

	public boolean update(Context context, UserInfo userinfo, boolean clearTables)
			throws RuntimeException {
		Log.d(TAG,"updating.....");
		
		fireUpdate(UpdateStatus.BEGIN_UPDATE, 0);

//		ConnectionManager cman = ConnectionManager.getInstance();
//		cman.createPool(userinfo);
//
//		process.userinfo = userinfo;
//		process.context = context;
//
//		if(userinfo.impersonate.trim().length() > 0)
//			ServerCommand.Category = "managerPDA";
//
//		ThreadPool threadPool = new ThreadPool(process, this, cman, winConnect);
//		threadPool.start();
//		DataThread winner = (DataThread) threadPool.getWinner();
//
//		if (winner == null){
//			message = context.getString(R.string.cant_connect_server);
////			cman.endSession();
//			return false;
//		}
//		winConnect = winner.getConenction();
//		winConnect.setWin();
//		ByteStream stream = winner.getByteStream();

		SocketConnection winConnect = null;
		if(Features.ENCODE_CONNECTION) {
			Encryptor enc = new Encryptor();
			winConnect = enc.startSession(context, userinfo);

			if(winConnect != null) {
				DataObjectPool pool = makeCommandPool(userinfo);
				winConnect.send(pool, ByteStream.GZIP_TAG, context);
				ByteStream bs = winConnect.receive(context);
				if(bs != null) {
					receivedBytes += bs.getReceived();
				}
			}
		}
		if(winConnect == null) {
			winConnect = beginConnection(context, userinfo);
		}
		if(winConnect == null) {
			message = context.getString(R.string.cant_connect_server);
			return false;
		}
		winConnect.setWin();
		ByteStream stream = winConnect.getReceived();

		fireUpdate(UpdateStatus.ENDREQUEST_UPDATE, stream.getSize());
		
		if( clearTables )
			clearBase();

		boolean readWhileNotContinue = false;
		for(Hitching h : recieveHitch)
			h.prepareReading();
		
		do{
//			readWhileNotContinue = stream.isContinues();
			
			StreamReader reader = new StreamReader(recieveHitch);
			reader.setUpdateProcessListener(updateProcessListener);
			
			reader.read(stream);
			readWhileNotContinue = reader.isContinue;
			
			stream.close();
			if(reader.haveServerData()) {
				sendServerInfo(reader, winConnect, userinfo, context);
			}
			
			if (readWhileNotContinue){
//				sendRequest(winConnect, DoneCommand.dbPool((LoginData) userinfo), "", context);
				winConnect.send(DoneCommand.dbPool((LoginData) userinfo), "", context);
				stream = winConnect.receive(context);
				if( stream == null )
					break;
//				if (!stream.isContinues())
//					sendRequest(winConnect, ByeCommand.dbPool((LoginData) userinfo), "", context);
			}
			
			receivedBytes += stream.getReceived();
		}while(readWhileNotContinue);
		
		firePostUpdateWork();
		fireUpdate(UpdateStatus.END, 0);
		
//		sendRequest(winConnect, ByeCommand.dbPool((LoginData) userinfo), "", context);
		winConnect.send(ByeCommand.dbPool((LoginData) userinfo), "", context);
		winConnect.close();
//		cman.endSession();
		
		Log.d(TAG, "updated");
		
		boolean result = login.isOK();
		
		if(result && userinfo.impersonate.trim().length() > 0)
			login.saveDuration(context);
		
		return result;
	}

	protected void sendServerInfo(StreamReader reader, SocketConnection winConnect, UserInfo userInfo, Context context) {
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
			ByteStream outStream = winConnect.receive(context);
			
			if(outStream != null) {
				reader.read(outStream);
			}
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	protected void clearBase() {
		DataBaseManager.clearBase();
		DocTypeBase.checkTables();
	}
	
	public String getMessage() { return message.length() == 0 ? login.getMessage() : message; }
	public String getKind() { return login.getKind(); }

	protected void firePostUpdateWork()
	{
		if (postUpdateWorks != null)
			postUpdateWorks.run();
	}

	public void setPostUpdateWork(Runnable postUpdateWork)
	{
		this.postUpdateWorks = postUpdateWork;
	}
	
	public void recieve(UserInfo userinfo, boolean clearTables) throws RuntimeException
	{
		
	}
	
//	public void sendRequest(IOStream ioStream, DataObjectPool pool, String tag, Context context) throws RuntimeException
//	{
//		byte[] streamData = pool.toStreamData();
//		ByteStream byteStream = new ByteStream(streamData, context);
//		byteStream.send(ioStream.getOutputStream(), tag);
//		byteStream.close();
//	}

	protected DataObjectPool makeCommandPool(UserInfo userinfo) 
		throws RuntimeException  
	{
		DataObjectPool dataObjectPool = new DataObjectPool();
		
		for(Hitching hitching : sendHitch)
		{
			ServerCommand serverCommand = new ServerCommand((LoginData) userinfo);
			serverCommand.setCommandParams(hitching);
			
			dataObjectPool.add(serverCommand);
			
			if( hitching instanceof ReportHitching )
				((ReportHitching)hitching).addReport(dataObjectPool, recieveHitch);
		}
		
		return dataObjectPool;
	}
}