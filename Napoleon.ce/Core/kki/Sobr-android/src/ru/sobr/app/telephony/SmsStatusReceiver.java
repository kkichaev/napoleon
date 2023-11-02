package ru.sobr.app.telephony;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import ru.sobr.app.R;

public class SmsStatusReceiver extends BroadcastReceiver {
	public static final String SUPPRES_TOAST = "suppres_toast";

	public void onReceive(Context context, Intent intent) {

		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals(SobrGsm.ACTION_SMS_SENT)) {

			if (!intent.getBooleanExtra(SUPPRES_TOAST, false)) {
				if (getResultCode() == Activity.RESULT_OK) {
					Toast.makeText(context, R.string.sms_sent,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, R.string.sms_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}
