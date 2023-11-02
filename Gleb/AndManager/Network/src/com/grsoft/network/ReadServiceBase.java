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
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.DataThread;
import com.grsoft.util.RunnableArgs;
import com.grsoft.util.ThreadPool;

public class ReadServiceBase{
	private static final String TAG = "ReadService"; 
	private Runnable postUpdateWorks = null;
	LoginHitching login = new LoginHitching();
	
	protected UpdateProcessListener updateProcessListener;
	protected List<? extends Hitching> sendHitch;
	protected List<Hitching> recieveHitch;
	protected String message = "";
	
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
		private static final String TAG = "ConnectProcess";
		public UserInfo userinfo;
		
		@Override
		public Object run() {
			DataThread t = (DataThread) Thread.currentThread();
			SocketConnection conn = ConnectionManager.getInstance().get(t.getIndex()); 
			boolean result = false;
			
			try{
				if(conn.connect()){
					DataObjectPool pool = makeCommandPool(userinfo);
					sendRequest(conn, pool, ByteStream.GZIP_TAG);
					ByteStream bs = ByteStream.receive(conn.getInputStream());
					
					if( bs != null ) {
						t.setByteStream(bs);
						boolean isContinues = bs.isContinues();
						Log.d(TAG, String.format("Stream is continues: %s", Boolean.toString(isContinues)));
						if (!isContinues)
							sendRequest(conn, ByeCommand.dbPool((LoginData) userinfo), "");
						
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
	
	public boolean update(Context context, UserInfo userinfo, boolean clearTables)
			throws RuntimeException {
		Log.d(TAG,"updating.....");
		
		fireUpdate(UpdateStatus.BEGIN_UPDATE, 0);
		final int THREAD_COUNT = 2;
		process.userinfo = userinfo;
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
		
		fireUpdate(UpdateStatus.ENDREQUEST_UPDATE, stream.getSize());
		
		if( clearTables )
			clearBase();

		boolean readWhileNotContinue = false;
		for(Hitching h : recieveHitch)
			h.prepareReading();
		
		do{
			readWhileNotContinue = stream.isContinues();
			
			StreamReader reader = new StreamReader(recieveHitch);
			reader.setUpdateProcessListener(updateProcessListener);
			reader.read(stream);
		
			if (readWhileNotContinue){
				sendRequest(cman.get(winner.getIndex()), DoneCommand.dbPool((LoginData) userinfo), "");
				stream = ByteStream.receive(cman.get(winner.getIndex()).getInputStream());
				if( stream == null )
					break;
				if (!stream.isContinues())
					sendRequest(cman.get(winner.getIndex()), ByeCommand.dbPool((LoginData) userinfo), "");
			}
			
			receivedBytes += stream.getReceived();
		}while(readWhileNotContinue);
		
		firePostUpdateWork();
		fireUpdate(UpdateStatus.END, 0);
		
		cman.get(winner.getIndex()).close();
		cman.endSession();
		
		Log.d(TAG, "updated");
		return login.isOK();
	}

	protected void clearBase() {
		DataBaseManager.clearBase();
		DocTypeBase.checkTables();
	}
	
	public String getMessage() { return message.length() == 0 ? login.getMessage() : message; }

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
	
	public void sendRequest(IOStream ioStream, 
			DataObjectPool pool, String tag) throws RuntimeException 
	{
		byte[] streamData = pool.toStreamData();
		ByteStream byteStream = new ByteStream(streamData);
		byteStream.send(ioStream.getOutputStream(), tag);
	}

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