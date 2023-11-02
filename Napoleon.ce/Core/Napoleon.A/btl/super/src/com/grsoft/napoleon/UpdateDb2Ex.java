package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DocumentRestoreEx;
import com.grsoft.database.Hitching;
import com.grsoft.database.PotenzialOrgRestore;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.impl.AgentImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.ConfigImpl2Ex;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.SimpleMessageBox;

public class UpdateDb2Ex extends UpdateDbEx {
	Spinner spAgent;
	EditText edFrom;
	EditText edTill;
	CheckBox cbRecreateStory;
	Button btnAgent;
	CheckBox cbGenData;
	DateHandler dateHandlerFrom;
	DateHandler dateHandlerTill;
	CheckBox cbClearDB;
	CheckBox cbDocs;
	
	private static final int DIALOG_DATE_FROM_PICKER_ID = 1;
	private static final int DIALOG_DATE_TILL_PICKER_ID = 2;
	private CheckBox cbVisit;
	
	protected int getContentView() { return  R.layout.updatedbex; }
	
	@Override
	protected UserInfo getRcvUserInfo() {
		AgentImpl ai = (AgentImpl)spAgent.getSelectedItem();
		
		if (ai != null && ai.getData() != null){
			Agent a = ai.getData();
			return new LoginData(a.login, a.password, this);
		}else
			return null;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DATE_FROM_PICKER_ID:
				return dateHandlerFrom.createDialog();
			case DIALOG_DATE_TILL_PICKER_ID:
				return dateHandlerTill.createDialog();
		}
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected UserInfo getSndUserInfo() {
		ConfigImpl2Ex config = (ConfigImpl2Ex)ConfigManager.getConfig();
		return new LoginData(config.userlogin, config.userpassword, this);
	}
	
	protected UserInfo getGpsUserInfo() {
		ConfigImpl2Ex config = (ConfigImpl2Ex)ConfigManager.getConfig();
		return new LoginData(config.login, config.passw, this);
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		((CheckBox)findViewById(R.id.cbRecreateStory)).setText("Принять данные");
		
		btnAgent = (Button) findViewById(R.id.btnAgent);
		btnAgent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AgentUpdateProcess(UpdateDb2Ex.this).execute((Void[])null);
			}
		});

		spAgent = (Spinner) findViewById(R.id.spAgent);
		SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yyyy");
		
		Calendar calendar = Calendar.getInstance();
		edTill = (EditText) findViewById(R.id.edTill);
		Date till = calendar.getTime();
		edTill.setText(sdf.format(till));
		edTill.setInputType(InputType.TYPE_NULL);
		dateHandlerTill = new DateHandler((TextView)findViewById(R.id.edTill), till, DIALOG_DATE_TILL_PICKER_ID);
		
		calendar.add(Calendar.MONTH, -1);
		edFrom = (EditText) findViewById(R.id.edFrom);
		edFrom.setInputType(InputType.TYPE_NULL);
		Date from = calendar.getTime();
		edFrom.setText(sdf.format(from));
		dateHandlerFrom = new DateHandler((TextView)findViewById(R.id.edFrom), from, DIALOG_DATE_FROM_PICKER_ID);
		
		cbRecreateStory = (CheckBox) findViewById(R.id.cbRecreateStory);
		cbRecreateStory.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enableRecreateControl(isChecked);
				cbGenData.setChecked(isChecked);
			}
		});
		
		enableRecreateControl(false);
		AgentAdapter aa = new AgentAdapter(this);
		spAgent.setAdapter(aa);
		ConfigImpl2Ex config =  (ConfigImpl2Ex) ConfigManager.getConfig();
		
		int pos = -1;
		
		for(int i = 0; i < aa.getCount(); i++)
			if (((AgentImpl)aa.getItem(i)).getData().id.equals(config.userid)){
				pos = i;
				break;
			}
		
		if (pos != -1 && pos < aa.getCount())
			spAgent.setSelection(pos);
		
		cbGenData = (CheckBox) findViewById(R.id.cbGenData);
		cbGenData.setChecked(false);
		cbGenData.setVisibility(View.GONE);
		
		cbDocs = ((CheckBox)findViewById(R.id.cbDocs));
		cbDocs.setText("Отправить документы");
		
		cbClearDB = (CheckBox) findViewById(R.id.cbClearDB);
		cbVisit = (CheckBox) findViewById(R.id.cbVisit);
		cbClearDB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					enableRecreateControl(!isChecked);
				}
				
				cbGenData.setEnabled(!isChecked);
				cbDocs.setEnabled(!isChecked);
				cbVisit.setEnabled(!isChecked);
				cbRecreateStory.setEnabled(!isChecked);
				btnAgent.setEnabled(!isChecked);
			}
		});
	}

	public void enableRecreateControl(boolean enable) {
		spAgent.setEnabled(enable);
		edFrom.setEnabled(enable);
		edTill.setEnabled(enable);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		((AgentAdapter)spAgent.getAdapter()).close();
	}
	
	class AgentUpdateProcess extends UpdateProcess{

		public AgentUpdateProcess(Activity context) {
			super(context);
		}
		
		@Override
		protected void enableControlButton(boolean enabled) {
			super.enableControlButton(enabled);
			btnAgent.post( new EnableButton(btnAgent, enabled));
			
			if(enabled){
				spAgent.post(new Runnable() {
					
					@Override
					public void run() {
						((AgentAdapter)spAgent.getAdapter()).notyfyDataSetChanged();
					}
				});
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if(lock.tryLock()){
				try
				{
					enableControlButton(false);
					onUpdate(UpdateStatus.BEGIN_UPDATE, 0);
					
					ConfigImpl2Ex config = (ConfigImpl2Ex) ConfigManager.getConfig();
					UserInfo ui = new LoginData(config.login, 
							config.passw, activity);
					
					String errMessage = null;
					
					if (errMessage == null && !isCancelled()) {
						
						List<Hitching> rcvHitch = new ArrayList<Hitching>();
						rcvHitch.add(new RcvNewHitching(Agent.class, "Agents"));
						ReadService dataBaseUpdater =  (ReadService) RWServiceFactory.instance.createReadService(rcvHitch);
						dataBaseUpdater.setUpdateProcessListenet(this);
						
						if( !dataBaseUpdater.update(activity, ui, false) )
							errMessage = dataBaseUpdater.getMessage();
						else
							traffic += dataBaseUpdater.getReceivedBytes();
					}
					
					if (!isCancelled())
						onUpdate(UpdateStatus.END_OF_PROCESS, 0);
					
					if (!isCancelled()){
						if( errMessage != null ) {
							showErrorMsg(errMessage, activity);
							return false;
						} else {
							onFinishUpdate();
							
							SimpleMessageBox smb = new SimpleMessageBox("Информация", 
									"Синхронизация завершена\nТрафик: " + 
									Integer.toString((traffic + 512) / 1024) + " кБ", activity); 
							onUpdateMessage(smb);
							Thread.sleep(3000);
							smb.hide();
						}
					}
					
					return true;
				}
				catch(Exception exception)
				{
					SQLiteDatabase dataBase = DataBaseManager.getDataBase();
					
					if (dataBase.isDbLockedByCurrentThread()
							|| dataBase.isDbLockedByOtherThreads())
						dataBase.endTransaction();
					
					String message = exception.getMessage();
					if( message == null )
						message = "Ошибка при приеме";
					if (!isCancelled())
						showErrorMsg(message, activity);
					
					exception.printStackTrace();
					
					return false;
				}
				finally
				{
					enableControlButton(true);
					lock.unlock();
				}
			}else{
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(
								activity, Html.fromHtml("Невозможно выполинть синхронизацию. <br>" +
										"Пожалуйста, повторите операцию через 2 минуты...."), Toast.LENGTH_LONG)
								.show();
						
					}
				});
				
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			enableControlButton(true);
		}
	}
	
	@Override
	protected void onFinishUpdate() {
		super.onFinishUpdate();
		
		AgentImpl ai = (AgentImpl) spAgent.getSelectedItem();
		if (ai != null){
			Agent a = ai.getData();
			
			if (a != null){
				String userid = a.id;
				ConfigImpl2Ex cex = ((ConfigImpl2Ex)ConfigManager.getConfig());
				if (!cex.userid.equals(userid)){
					cex.userid = userid;
					cex.passw = a.password;
					cex.login = a.login;
					ConfigManager.save();
				}
			}
		}
	}
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = new ArrayList<Hitching>();
		try{
			Date from = Util.simpleDateFormat.parse(edFrom.getText().toString());
			Date till = Util.simpleDateFormat.parse(edTill.getText().toString());
			
			result.add(new DocumentRestoreEx(OrderDoc.instance(), from, till));
			result.add(new DocumentRestoreEx(RemnantsDoc.instance(), from, till));
			
			result.add(new PotenzialOrgRestore());
			result.add(new DocumentRestoreEx(QuestionDoc.instance(), from, till));
			result.add(new DocumentRestoreEx(VisitDoc.instance(),"VisitInfo", from, till));
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}

class AgentAdapter implements SpinnerAdapter{
	private Cursor<Agent> crAgent;
	private Context context;
	private DataSetObserver dsObserver;
	
	public AgentAdapter(Context context){
		crAgent = new Cursor<Agent>(new AgentImpl());
		this.context = context; 
	}
	
	public void close(){
		crAgent.close();
	}
	
	public void refresh(){
		crAgent.updateIds();
	}
	
	@Override
	public int getCount() {
		return crAgent.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		return crAgent.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		dsObserver = observer;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	public void notyfyDataSetChanged(){
		crAgent.updateIds();
		dsObserver.onChanged();
	}
	

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return getDropDownView(arg0, arg1, arg2);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		}
		
		AgentImpl agentImpl = (AgentImpl) getItem(position);
		if(agentImpl != null){
			TextView tvFirmaName = (TextView) convertView.findViewById(R.id.tvFirmaName);
			Agent a = agentImpl.getData();
			tvFirmaName.setText(String.format("(%s)%s", a.id, a.name));
		}
			
		return convertView;

	}
}
