package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.view.View;
import android.widget.BaseAdapter;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.WhPrice;
import com.grsoft.dataobjects.impl.GatherImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RawObject;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UserInfo;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.view.TimerMessageBox;

class SyncProcess extends NetworkAsyncTask{
	Context context;
	int traffic = 0;
	
	public SyncProcess(Context context, View control) {
		super(new SendProgressManager(context, control));
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean result = false;
		onUpdate(UpdateStatus.START_OF_PROCESS, 0);
		
		try	{
			Config config = ConfigManager.getConfig();
			UserInfo userInfo = new LoginData(config.login, config.passw, context);
			String errMessage = "";
			
			List<ObjectListener> objectsToSend = new ArrayList<ObjectListener>(); 
			objectsToSend.add(new GatherSnd());
			
			WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(objectsToSend, false);
			writeService.setUpdateProcessListenet(this);
			if( !writeService.write(context, userInfo)){
				errMessage = writeService.getMessage();
			}else{
				traffic += writeService.getSendedBytes();
			}
			
			if(errMessage.length() == 0) {
				ArrayList<Hitching> rcvHitch = new ArrayList<Hitching>();
				rcvHitch.add(new GatherRcv());
				rcvHitch.add(new RcvNewHitching(WhPrice.class, "WhPrice"));
				ReadServiceBase dataBaseUpdater =  RWServiceFactory.instance.createReadService(rcvHitch);
				dataBaseUpdater.setUpdateProcessListenet(this);			
				
				if( !dataBaseUpdater.update(context, userInfo, false) ){
					errMessage = dataBaseUpdater.getMessage();
				}else{
					traffic += dataBaseUpdater.getReceivedBytes();
				}
			}

			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			
			if(errMessage.length() > 0){
				result = false;
				showErrorMsg(errMessage, context);
			}else{
				result = true;
				onUpdateMessage(new TimerMessageBox("Информация", 
						"Синхронизация завершена\nТрафик: " + 
								Integer.toString((traffic + 512) / 1024) + " кБ", context));
			}
			
			return result;
		} catch(Exception exception){
			onUpdate(UpdateStatus.END_OF_PROCESS, 0);
			showErrorMsg(exception.getMessage(), context);
			exception.printStackTrace();
			
			return false;
		} 
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			NapoleonEx activity = (NapoleonEx) context;
			((BaseAdapter)activity.lvMainOrgs.getAdapter()).notifyDataSetChanged();
		}
			
	}
}

/**
 * Принимаем только новые накладные
 * @author 1111
 *
 */
class GatherRcv extends Hitching {
	SQLiteCursor cursor = null;
	public GatherRcv() {
		super(Gather.class, "Gather");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		try {
			String table = DataObjectInfo.getInstance().getTableName(Gather.class);
			String sql = "SELECT date from " + table + " where id=?";
			String[] keys = {""};
			cursor = (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(sql, keys);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if( cursor != null )
			cursor.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Gather dobj = (Gather) rawObject.createDataObject(dataObject);
		if( cursor != null ) {
			String[] keys = {dobj.id};
			cursor.setSelectionArguments(keys);
			cursor.requery();
			if( cursor.moveToNext() )
				return;
		}
		dbProxy.insertRecord(dobj);
	}
}

class GatherSnd extends Hitching implements ObjectExportListener{
	List<Long> list = new ArrayList<Long>();
	
	public GatherSnd() {
		super(Gather.class, "Complete");
		String where =  String.format("(([params] & (%d|%d|%d)) == %d)", 
				ParamState.ofExported, Gather.IN_WORK, Gather.COMPLEETE, Gather.COMPLEETE);
		
		List<Long> pref = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Gather.class), where, "");
		
		GatherImpl impl = new GatherImpl();
		for(long rowid: pref){
			if(impl.read(rowid)){
				if(impl.isComplete())
					list.add(rowid);
			}
		}
		
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			GatherImpl impl = new GatherImpl();
			impl.read(list.get(i));
			impl.getData().params |=  ParamState.ofExported;
			impl.write();
			impl.close();
		}
	}

	@Override
	public DataObject get(int i) {
		GatherImpl impl = new GatherImpl();
		impl.read(list.get(i));
		impl.close();
		return impl.getData();
	}
	
}