package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PinRegHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.PinRegAnswer;
import com.grsoft.dataobjects.UserPinData;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.SimpleMessageBox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Registration extends BaseActivity {
	
	static final int MIN_PIN_LENGTH = 4; 
	
	PinRegHitching regAnswer = new PinRegHitching();
	
	static public void open(Context context) {
		Intent i = new Intent(context, Registration.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.registration);
		
		CfgNpl  cfg = (CfgNpl) ConfigManager.getConfig();
		
		EditText ed;
		ed = (EditText)findViewById(R.id.edIP);
		ed.setText(cfg.address);
		
		ed = (EditText)findViewById(R.id.edPort);
		ed.setText(Integer.toString(cfg.port));

		ed = (EditText)findViewById(R.id.edLogin);
		ed.setText(cfg.login);

		ed = (EditText)findViewById(R.id.edPassw);
		ed.setText(cfg.passw);
		
		findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { doRegister(); }
		});
		
		if(ServerCommand.DeviceID == null)
			ServerCommand.DeviceID = "123456";
	}
	
	protected void doRegister() {
		CfgNpl  cfg = (CfgNpl) ConfigManager.getConfig();

		EditText ed;
		ed = (EditText)findViewById(R.id.edIP);
		cfg.address = ed.getText().toString();
		
		ed = (EditText)findViewById(R.id.edPort);
		try {
			cfg.port =  Integer.parseInt(ed.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ed = (EditText)findViewById(R.id.edLogin);
		cfg.login = ed.getText().toString();

		ed = (EditText)findViewById(R.id.edPassw);
		cfg.passw = ed.getText().toString();
		
		ConfigManager.save();
		
		ed = (EditText)findViewById(R.id.edPin);
		String pin = ed.getText().toString();
		if( pin.length() < MIN_PIN_LENGTH ) {
			showMessage(String.format("Длина ПИН-кода меньше минимальной. Введите, пожалуйста, %d или более символа", MIN_PIN_LENGTH), false);
			return;
		}
		
		RegisterProcess rp = new RegisterProcess(this, pin, regAnswer);
		rp.execute((Void[])null);
		
	}

	void showMessage(final String errMsg, final boolean doFinish) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				SimpleMessageBox meb = new SimpleMessageBox(doFinish ? "Информация" : getString(R.string.error), errMsg, Registration.this);
				meb.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.close), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(doFinish)
							finish();
					}
				});
				meb.show();
			}
		});
	}
	
	@Override public void onBackPressed() { }
	
	public void onExchangeResult(boolean res, String err) {
		if(res) {
			PinRegAnswer anw = regAnswer.getAnswer();
			if(anw != null && anw.registred > 0) {
				PinChecker.setIsRegistred(this);
				PinChecker.putTryCount(this, 0);
				showMessage(anw.message, true);
			} else {
				showMessage(anw == null ? "Ошибка при регистрации" : anw.message, false);
			}
		} else {
			showMessage(err, false);
		}
	}
}

class RegisterData extends DataObject {
	public String hash = "";;
}

class RegisterProcess extends NetworkAsyncTask {
	
	String pinData;
	Registration owner;
	PinRegHitching prHitching;
	
	public RegisterProcess(Registration context, String pinData, PinRegHitching prHitching) {
		super(new ProgressManager(context));
		((ProgressManager) this.progressHelper).setUpdateProcess(this);
		
		owner = context;
		this.pinData = pinData;
		this.prHitching = prHitching;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		RegisterData regData = new RegisterData();
		regData.hash = PinChecker.getHash(pinData);
		
		onUpdate(UpdateStatus.BEGIN_UPDATE, 0);

		List<Hitching> rcv = new ArrayList<Hitching>();
		rcv.add(new RcvNewHitching(UserPinData.class));
		rcv.add(prHitching);
		com.grsoft.database.ReportHitching rp = new com.grsoft.database.ReportHitching("register_pda", regData, rcv);
		
		String errMessage = "";
		List<Hitching> rcvHitch = new ArrayList<Hitching>();
		rcvHitch.add(rp);
		ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(rcvHitch);
		dataBaseUpdater.setUpdateProcessListenet(this);

		Config config = ConfigManager.getConfig();
		LoginData ld = new LoginData(config.login, config.passw, "", owner);
		
		boolean res = false;
		try {
			res = dataBaseUpdater.update(owner, ld, false); 
			if(!res)
				errMessage = dataBaseUpdater.getMessage();
		} catch (RuntimeException e) {
			e.printStackTrace();

			errMessage = e.getMessage();
			if (errMessage == null)
				errMessage = owner.getString(R.string.recieved_error);
		}

		onUpdate(UpdateStatus.END_OF_PROCESS, 0);
		owner.onExchangeResult(res, errMessage);
		return res;
	}
}
