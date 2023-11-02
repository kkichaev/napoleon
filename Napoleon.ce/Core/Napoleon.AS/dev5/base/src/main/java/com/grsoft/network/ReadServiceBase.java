/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Класс обеспечивающий чтения объектов из сети
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;


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
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.exception.UploadException;
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
	
	public void setUpdateProcessListenet(UpdateProcessListener listener) {
		updateProcessListener = listener;
	}
	
	protected void fireUpdate(UpdateStatus status, int progress) {
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

	public boolean update(Context context, UserInfo userinfo, boolean clearTables) throws RuntimeException {
		Log.d(TAG,"updating.....");

		fireUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_UPDATE, 0);
		ConnectionHelper.Result cres = ConnectionHelper.getConnection(userinfo);
		if(cres.error != null) {
			message = cres.error;
			return false;
		}

		if(cres.connection == null) {
			message = context.getString(R.string.cant_connect_server);
			return false;
		}

		boolean res = false;
		SocketConnection connect = null;
		try {
			connect = cres.connection;
			DataObjectPool pool = makeCommandPool(userinfo);
			sendRequest(connect, pool, ByteStream.GZIP_TAG, context);
			ByteStream stream = ByteStream.receive(connect.getInputStream(), context);
			if(stream != null) {
				receivedBytes += stream.getReceived();
				fireUpdate(UpdateProcessInfo.UpdateStatus.ENDREQUEST_UPDATE, stream.getSize());

				if( clearTables )
					clearBase();

				boolean readWhileNotContinue;
				for(Hitching h : recieveHitch)
					h.prepareReading();

				do{
					StreamReader reader = new StreamReader(recieveHitch);
					reader.setUpdateProcessListener(updateProcessListener);

					reader.read(stream);
					readWhileNotContinue = reader.isContinue;

					stream.close();
//                    if(reader.haveServerData()) {
//                        sendServerInfo(reader, connect, userinfo, context);
//                    }

					if (readWhileNotContinue){
						sendRequest(connect, DoneCommand.dbPool((LoginData) userinfo), "", context);
						stream = ByteStream.receive(connect.getInputStream(), context);
						if( stream == null )
							break;
					}
					receivedBytes += stream.getReceived();
				}while(readWhileNotContinue);

				firePostUpdateWork();
				fireUpdate(UpdateProcessInfo.UpdateStatus.END, 0);

				sendRequest(connect, ByeCommand.dbPool((LoginData) userinfo), "", context);

				Log.d(TAG, "updated");
				res = login.isOK();
			} else {
				message = context.getString(R.string.cant_connect_server);
			}
		} catch (Exception e) {
			message = e.getLocalizedMessage();
		}

		if(connect != null)
			connect.close();
		return res;
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
	
	public void sendRequest(IOStream ioStream, DataObjectPool pool, String tag, Context context) throws RuntimeException 
	{
		byte[] streamData = pool.toStreamData();
		ByteStream byteStream = new ByteStream(streamData, context);
		byteStream.send(ioStream.getOutputStream(), tag);
		byteStream.close();
	}

	protected DataObjectPool makeCommandPool(UserInfo userinfo) throws RuntimeException {
		DataObjectPool dataObjectPool = new DataObjectPool();
		
		for(Hitching hitching : sendHitch) {
			ServerCommand serverCommand = new ServerCommand((LoginData) userinfo);
			serverCommand.setCommandParams(hitching);

			try {
				dataObjectPool.add(serverCommand);
				if (hitching instanceof ReportHitching) {
					((ReportHitching) hitching).addReport(dataObjectPool, recieveHitch);
				}
			} catch (UploadException ue) {
				throw new RuntimeException(ue);
			}
		}
		
		return dataObjectPool;
	}
}