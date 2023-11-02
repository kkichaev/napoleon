package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.FormatObjectList;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.ObjectExchange;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;

public class ExchangeService {
	private int sendedBytes = 0;
	
	DataObject object;
	String objName;
	String command;
	Context context;
	
	String response;
	long result = -1;
	
	public ExchangeService(Context context, 
			DataObject object, String objName, String command) {
		this.object = object;
		this.objName = objName;
		this.command = command;
		this.context = context;
	}
	
	public boolean doExchange(UserInfo userInfo, CfgNplW config, UpdateProcessListener listener) {
		boolean ret = false;
		
		String addr = (config.onLineIP == 0) ? config.address : config.address2;
		int port = (config.onLineIP == 0) ? config.port : config.port2;
		
		SocketConnection sc = new SocketConnection(addr, port);
		try {
			if( listener != null )
				listener.onUpdate(UpdateStatus.START_OF_PROCESS, 0);

			ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
			serverCommand.setCommandParams(ObjectExchange.OBJECTS_COMMAND, command);
			DataObjectPool pool = new DataObjectPool();
			pool.add(serverCommand);
			pool.add(objName, new ExchangeObject(objName, object));
			
			// connect
			sc.connect();

			// send object
			ByteStream byteStream = new ByteStream(pool.toStreamData(), context);
			sendedBytes += byteStream.send(sc.getOutputStream(), ByteStream.GZIP_TAG);
			byteStream.close();
			
			if( listener != null )
				listener.onUpdate(UpdateStatus.ENDREQUEST_SEND, 1);

			// receive object
			ByteStream bs = ByteStream.receive(sc.getInputStream(), context);

			// send bye
			ByteStream bye = new ByteStream(ByeCommand.dbPool((LoginData) userInfo).toStreamData(), context);
			bye.send(sc.getOutputStream(), "");
			bye.close();

			if( listener != null )
				listener.onUpdate(UpdateStatus.STEP_SEND, 2);
			
			if(bs == null) {
				response = context.getString(R.string.server_isnot_responded);
			} else {
				sendedBytes += bs.getReceived();
				read(bs);
				bs.close();
			}

			if( listener != null )
				listener.onUpdate(UpdateStatus.END, 3);
		} catch(RuntimeException e) {
			response = e.getInnerException().getMessage();
		}finally{
			sc.close();
		}
		return ret;
	}

	private void read(ByteStream bs) throws RuntimeException {
		List<Hitching> list = new ArrayList<Hitching>();
		ServerAnswerHitching serverAnswer = new ServerAnswerHitching();
		ExchangeHitching eh = new ExchangeHitching(object, objName);
		
		list.add(serverAnswer);
		list.add(eh);
		
		StreamReader reader = new StreamReader(list);
		reader.read(bs);
		
		if( serverAnswer.IsOK() ) {
			response = eh.getMessage();
			result = eh.getResult();
		} else {
			response = serverAnswer.getMessage();
		}
	}
	
	public String getResponse() { return response; }
	public int getResult() { return (int)result; }
	public int getSendedBytes() { return sendedBytes; }
}

class ExchangeObject extends FormatObjectList {
	public ExchangeObject(String name, DataObject object) throws RuntimeException {
		format = createFormat(object);
		format.add(new StringFormat(ObjectExchange.SERV_RESPONSE));
		format.add(new NumberFormat(ObjectExchange.SERV_RESULT, 1));
		appendObject(object);
	}
}

class ExchangeHitching extends Hitching {
	DataObject object;
	String message;
	long result;
	
	public ExchangeHitching(DataObject object, String objName) {
		super(object.getClass(), objName);
		
		this.object = object;
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		rawObject.setDataObject(object);
		
		Member m;
		m = rawObject.getMember(ObjectExchange.SERV_RESPONSE);
		if( m != null )
			message = m.getValue().toString();
		
		m = rawObject.getMember(ObjectExchange.SERV_RESULT);
		if( m != null )
			result = (Long)m.getValue();
	}
	
	@Override public void onEnd() { }
	@Override public void onStart() { }
	
	public String getMessage() { return message; }
	public int getResult() { return (int)result; } 
}
