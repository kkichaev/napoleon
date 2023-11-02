package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.AgentActivityHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentRcv;
import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.MessageStock;
import com.grsoft.util.Util;

public class UpdateDBEx extends UpdateDB {
	
	private static final String OPEN_BLOCKED = "open_blocked";
	boolean blocked = false;
	
	public static void openBlocked(Context c) {
		Intent i = new Intent(c, UpdateDBEx.class);
		i.putExtra(OPEN_BLOCKED, true);
		c.startActivity(i);
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> ret = super.getExported();
		ret.add(new AgentActivityHitching());
		return ret;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		blocked = (b==null) ? false : b.getBoolean(OPEN_BLOCKED, false);
		
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(OPEN_BLOCKED, blocked);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && blocked){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cb = (CheckBox) findViewById(R.id.cbRemains);
		cb.setChecked(false);

		cb = (CheckBox) findViewById(R.id.cbDebt);
		cb.setChecked(true);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(AgentRcv.class, "AgentsRcv"));
		return ret;
	}

	@Override
	protected UpdateProcess getUpdateProcess() {
		return new UpdateProcess(this) {
			protected boolean showRecievedMessage(final Runnable doAfterDialog) {
				Message[] receivedMessages = MessageStock.getNewMessage();

				if (receivedMessages.length == 0)
					return false;

				try {

					Context context = progressHelper.getContext();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle(R.string.message_input);
					View dialogView = View.inflate(context, R.layout.messagesex,
							null);
					ListView lvMessages = (ListView) dialogView
							.findViewById(R.id.lvMessages);

					class NewMessageAdapter extends BaseAdapter {
						private Message[] message;
						private Context context;

						public NewMessageAdapter(Context context,
								Message[] message) {
							this.message = message;
							this.context = context;
						}

						@Override
						public int getCount() {
							return message.length;
						}

						@Override
						public Object getItem(int arg0) {
							return message[arg0];
						}

						@Override
						public long getItemId(int arg0) {
							return 0;
						}

						@Override
						public View getView(int arg0, View arg1, ViewGroup arg2) {
							Message message = (Message) getItem(arg0);

							if (arg1 == null)
								arg1 = View.inflate(context,
										R.layout.msg_list_row, null);

							TextView tvDate = (TextView) arg1
									.findViewById(R.id.tvDate);
							tvDate.setText(Util.simpleDateFormat
									.format(message.date));

							TextView tvMessage = (TextView) arg1
									.findViewById(R.id.tvMessage);
							tvMessage.setText(message.message);

							return arg1;
						}
					}

					lvMessages.setAdapter(new NewMessageAdapter(context,
							receivedMessages));
					builder.setView(dialogView);
					builder.setCancelable(false);
					
					if (doAfterDialog != null)
						builder.setOnCancelListener(new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								doAfterDialog.run();
							}
						});

					final AlertDialog newMessagesDlg = builder.create();
					
					final Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
					btnOK.setEnabled(false);
					btnOK.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							newMessagesDlg.dismiss();
						}
					});
					
					Timer timer = new Timer();
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, 30);
					timer.schedule(new TimerTask(){

						@Override
						public void run() {
							btnOK.post(new Runnable() {
								
								@Override
								public void run() {
									btnOK.setEnabled(true);
								}
							});
						}
						
					}, cal.getTime());

					newMessagesDlg.show();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
		if (sp != null) {
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			int pos = 0;
			if (config.daysToRecreate > 0)
				pos = config.daysToRecreate / 7 - 1;
			else
				pos = config.monthsToRecreate + 2;
			
			sp.setSelection(pos, true);
		}
	}
	
	protected void saveSettings() {
		Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
		if (sp != null) {
			CfgNpl c = (CfgNpl) ConfigManager.getConfig();
			int pos = sp.getSelectedItemPosition();
			c.daysToRecreate = 0;
			c.monthsToRecreate = 0;
			
			if(pos < 3){
				c.daysToRecreate = (pos + 1) * 7;
			}else{
				pos = pos - 2;
				c.monthsToRecreate = pos;
			}
			ConfigManager.save();
		}
	}
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return new PriceHitching() {
			@SuppressLint("DefaultLocale")
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				PriceEx dobj = (PriceEx) rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.article.toUpperCase();
				dbProxy.insertRecord(dobj);
			}
		};
	}
}
