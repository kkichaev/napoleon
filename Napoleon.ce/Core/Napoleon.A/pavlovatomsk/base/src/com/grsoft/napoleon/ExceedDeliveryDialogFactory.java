package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class ExceedDeliveryDialogFactory {
	public Dialog createDialog(Context context, OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.exceed_delivery_ask);
		builder.setPositiveButton(R.string.yes, listener);
		return builder.create();
	}
}
