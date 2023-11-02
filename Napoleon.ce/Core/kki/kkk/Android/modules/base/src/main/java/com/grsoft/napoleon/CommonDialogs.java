package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class CommonDialogs {
	public static Dialog createAskOpenGpsDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.gpsOffTitle);
		builder.setMessage(R.string.gpsOffMessage);
		builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
			}
		});
		return builder.create();
	}

	public static Dialog createAskForPermissionDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.gps_permission_dissallow);
		builder.setMessage(R.string.gps_permission_explain);
		builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent appSettingsIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
		           Uri.parse("package:" + context.getPackageName()));
				context.startActivity(appSettingsIntent);
			}
		});
		
		return builder.create();
	}
}
