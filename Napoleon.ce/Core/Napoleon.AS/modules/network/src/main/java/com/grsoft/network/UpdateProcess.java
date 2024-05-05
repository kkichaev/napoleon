package com.grsoft.network;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.Config;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo.ConnArg;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

public class UpdateProcess extends AsyncTask<Object, Void, Boolean> 
		implements UpdateProcessListener{
	public static Class<? extends UpdateProcess> processType = UpdateProcess.class;
	
	protected UserInfo userInfo;
	protected int traffic = 0;
	protected Context context;
	protected boolean doSilent;
	
	public static final String MESSAGE = "com.grsoft.network.updateprocess.message";
	public static final String TRAFFIC = "com.grsoft.network.updateprocess.traffic";
	public static final String STATUS = "com.grsoft.network.updateprocess.step";
	public static final String PROGRESS = "com.grsoft.network.updateprocess.progress";
	
	public static String UPDATE_PROCESS_ERROR = "com.grsoft.network.updateprocess.update_process_error";
	public static String UPDATE_PROCESS_RESULT = "com.grsoft.network.updateprocess.update_process_result";
	public static String UPDATE_PROCESS_STEP = "com.grsoft.network.updateprocess.update_process_step";
	
	
	public static class Params implements Parcelable{
		public List<Hitching> indata = new ArrayList<Hitching>();
		public List<Hitching> rcvdata = new ArrayList<Hitching>();
		public List<ObjectListener> outdata = new ArrayList<ObjectListener>();
		public List<ObjectListener> slicedata = new ArrayList<ObjectListener>();
		
		public String ip1 = "";
		public String ip2 = "";
		
		private final int DEFAULT_PORT = 8888;
		public int port1 = DEFAULT_PORT;
		public int port2 = DEFAULT_PORT;
		public int duration = 0;
		public String login = "";
		public String pass = "";
		public String impersonate = "";
		public String uuid = "";
		public String serverCode = "";
		public boolean sendPhotos = false;
		
		public Params(){}
		public Params(Parcel in) {
			readFromParcel(in);
		}

		public void setFrom(Config config) {
			login = config.login;
			pass = config.passw;

			impersonate = config.impersonate;

			uuid = config.uuid;
			serverCode = config.serverCode;

			ip1 = config.address;;
			ip2 = config.address2;;
			port1 = config.port;
			if(config.port2 != 0)
				port2 = config.port2;
		}

		private void readFromParcel(Parcel in) {
			in.readList(indata, indata.getClass().getClassLoader());
			in.readList(outdata, outdata.getClass().getClassLoader());
		
			ip1 = in.readString();
			ip2 = in.readString();
			port1 = in.readInt();
			port2 = in.readInt();
			duration = in.readInt();
			login = in.readString();
			pass = in.readString();
			impersonate = in.readString();
			sendPhotos = (in.readInt() > 0);
		}
		
		@Override
		public int describeContents() {	return 0; }
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeList(indata);
			dest.writeList(outdata);
			
			dest.writeString(ip1);
			dest.writeString(ip2);
			dest.writeInt(port1);
			dest.writeInt(port2);
			dest.writeInt(duration);
			dest.writeString(login);
			dest.writeString(pass);
			dest.writeString(impersonate);
			dest.writeInt(sendPhotos ? 1 : 0);
		}
		
		public static final Creator<Params> CREATOR = new Creator<Params>() {
            public Params createFromParcel(Parcel source) {
                return new Params(source);
            }
            public Params[] newArray(int size) {
                return new Params[size];
            }
        };
	}

	public static UpdateProcess createProcess(Context context) {
		try {
			Constructor<? extends UpdateProcess> cns = processType
					.getConstructor(Context.class);
			return cns.newInstance(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public UpdateProcess(Context context) {
		this(context, false);
	}

	public UpdateProcess(Context context, boolean doSilent) {
		this.context = context;
		this.doSilent = doSilent;
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;

		if (params != null && params.length > 0){
			Params p = (Params)params[0];
			
			if (p != null)
				result = updateProcess(p);
		}
		
		return result;
	}

	protected void broadcastError(String msg){
		if(context != null && !doSilent){
			Intent intent = new Intent(UPDATE_PROCESS_ERROR);
			intent.putExtra(MESSAGE, msg);
			context.sendBroadcast(intent);
		}
	}
	
	protected void broadcastResult(int traffic){
		if(context != null && !doSilent){
			Intent intent = new Intent(UPDATE_PROCESS_RESULT);
			intent.putExtra(TRAFFIC, (traffic + 512) / 1024);
			context.sendBroadcast(intent);
		}
	}
	
	private LoginData getLogin(Params p){
		LoginData result = new LoginData(p.login, p.pass, p.impersonate, context, p.uuid, p.serverCode);
		ConnArg con1 = new ConnArg();
		con1.address = p.ip1;
		con1.port = p.port1;
		ConnArg con2 = new ConnArg();
		con2.address = p.ip2;
		con2.port = p.port2;
		result.setConnArg(con1, con2);
		
		return result;
	}
	
	private boolean updateProcess(Params arg) {
		boolean result = false;
		fireUpdate(UpdateStatus.BEGIN_UPDATE);
		
		try {
			if(arg != null){
				userInfo = getLogin(arg);
	
				String errMessage = null;
	
				if (!isCancelled() ) {
					List<ObjectListener> docs  = arg.outdata;
	
					if (docs != null && docs.size() > 0) {
						WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(docs);
						
						for(Hitching r : arg.rcvdata)
							writeService.addRecieveHitch(r);
						
						writeService.setUpdateProcessListenet(this);
	
						if (!writeService.write(context, userInfo))
							errMessage = writeService.getMessage();
						else 
							traffic += writeService.getSendedBytes();
					}
					
					if(arg.sendPhotos) {
						List<CreateDocDataObject> phDocs = DocTypeBase.getPhotoDocs();
						if(phDocs.size() > 0) {
							VisitSendHelper vsh = new VisitSendHelper();
							if(!vsh.send(context, userInfo, phDocs, this)) {
								errMessage = vsh.getError();
							} else {
								traffic += vsh.getTraffic();
							}
						}
					}
				}
				
				if (errMessage == null && !isCancelled()) {
					List<ObjectListener> docs  = arg.slicedata;
	
					while (docs != null && docs.size() > 0) {
						WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(docs);
						
						for(Hitching r : arg.rcvdata)
							writeService.addRecieveHitch(r);
						
						writeService.setUpdateProcessListenet(this);
	
						if (!writeService.write(context, userInfo))
							errMessage = writeService.getMessage();
						else 
							traffic += writeService.getSendedBytes();
						
						docs = getNextSlice(arg.slicedata);
					}
				}
				
				if(errMessage == null)
					sendComplete();
	
				if (errMessage == null && !isCancelled()) {
					List<Hitching> rcvHitch = arg.indata;
					rcvHitch.addAll(arg.rcvdata);
					
					ReadServiceBase dataBaseUpdater = RWServiceFactory.instance.createReadService(rcvHitch);
					dataBaseUpdater.setUpdateProcessListenet(this);
					
					if (!dataBaseUpdater.update(context, userInfo, false))
						errMessage = dataBaseUpdater.getMessage();
					else 
						traffic += dataBaseUpdater.getReceivedBytes();
				}
	
				if (!isCancelled()) {
					if (errMessage != null) {
						broadcastError(errMessage);
						result = false;
					} else {
						broadcastResult(traffic);
						result = true;
					}
				}
			}
		} catch (Exception exception) {
			broadcastError(exception.getMessage());
			exception.printStackTrace();
			result = false;
		}
		finally{
			endTransaction();
			onPostExecuteWork(result);
		}
		
		return result;
	}

	private List<ObjectListener> getNextSlice(List<ObjectListener> input) {
		List<ObjectListener> result = new ArrayList<ObjectListener>();
		
		for(ObjectListener o : input) {
			if(o instanceof SliceHitching) {
				((SliceHitching)o).fetch();
			
				if(o instanceof DocExportListener && ((DocExportListener)o).getDocuments().getCount() > 0)
					result.add(o);
				else if (o instanceof ObjectExportListener && ((ObjectExportListener)o).size() > 0)
					result.add(o);
			}
		}
		
		return result;
	}

	protected void sendComplete() {}

	protected void endTransaction() {
		SQLiteDatabase dataBase = DataBaseManager.getDataBase();
	
		if(dataBase.inTransaction())
			try{
				dataBase.setTransactionSuccessful();
				dataBase.endTransaction();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	private void fireUpdate(UpdateStatus status){
		onUpdate(status, 0);
	}

	@Override
	protected void onPreExecute() {}

	protected void onPostExecuteWork(Boolean result) {}

	@Override
	public void onUpdate(UpdateStatus status, int progress) {
		if(context != null && !doSilent){
			Intent intent = new Intent(UPDATE_PROCESS_STEP);
			intent.putExtra(STATUS, status.name());
			intent.putExtra(PROGRESS, progress);
			context.sendBroadcast(intent);
		}
	}
}