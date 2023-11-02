package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.UserPinData;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.ProgressManager;
import com.grsoft.view.KeypadHelper;
import com.grsoft.view.SimpleMessageBox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPinAcivity extends Activity {
	KeypadHelper keypadHelper = null;

	public static void open(Context c) {
		Intent i = new Intent(c, EnterPinAcivity.class);
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.enter_pin);
		
		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { doSync(); }
		});
		
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { checkPin();}
		});
		
		EditText ed = (EditText)findViewById(R.id.edPin);
		ed.setFocusable(false);
		ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if( arg1 ) {
				    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);					
				}
			}
		});
//		ed.setInputType(InputType.TYPE_NULL);
		
	    
		updateOK();
		keypadHelper = new KeypadHelper(this, R.id.edPin);
	}
	
	void updateOK() {
		int tryCount = PinChecker.getTryCount(this) + 1;
		int maxCount = PinChecker.getMaxTry();
		findViewById(R.id.btnOK).setEnabled(tryCount < maxCount);
	}
	
	protected void checkPin() {
		EditText ed = (EditText)findViewById(R.id.edPin);
		String hash = PinChecker.getHash(ed.getText().toString());
		if(hash.length() == 0)
			return;
		
		UserPinData upd = UserPinData.get();
		if(upd.pinHash.compareTo(hash) != 0) {
			int tryCount = PinChecker.getTryCount(this) + 1;
			int maxCount = PinChecker.getMaxTry();
			
			Toast.makeText(this, String.format("¬веден неверный код (%d из %d)", tryCount, maxCount), Toast.LENGTH_SHORT).show();
			
			ed.setText("");
			PinChecker.putTryCount(this, tryCount);
			findViewById(R.id.btnOK).setEnabled(tryCount < maxCount);
			return;
		}
		
		PinChecker.putTryCount(this, 0);
		finish();
	}

	protected void doSync() {
		new SyncPinProcess(this).execute((Void[])null);
	}

	void showMessage(final String errMsg) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				SimpleMessageBox meb = new SimpleMessageBox(getString(R.string.error), errMsg, EnterPinAcivity.this);
				meb.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.close), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				meb.show();
			}
		});
	}

	public void onExchangeResult(boolean res, String err) {
		if(res) {
			UserPinData upd = UserPinData.get();
			if(upd.resetPin > 0) {
				Registration.open(this);
				finish();
				return;
			}
			if(upd.authByPin == 0){
				PinChecker.putTryCount(this, 0);
				LoginActivity.open(this);
				finish();
			}
			updateOK();
		} else {
			showMessage(err);
		}
	}
	
	@Override
	public void onBackPressed() {
	}
}

class SyncPinProcess extends NetworkAsyncTask {
	
	EnterPinAcivity owner;
	
	public SyncPinProcess(EnterPinAcivity context) {
		super(new ProgressManager(context));
		((ProgressManager) this.progressHelper).setUpdateProcess(this);
		
		owner = context;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		onUpdate(UpdateStatus.BEGIN_UPDATE, 0);

		String errMessage = "";
		List<Hitching> rcvHitch = new ArrayList<Hitching>();
		rcvHitch.add(new RcvNewHitching(UserPinData.class));
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
