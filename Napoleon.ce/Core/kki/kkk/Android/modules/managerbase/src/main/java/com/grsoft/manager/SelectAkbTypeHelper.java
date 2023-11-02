package com.grsoft.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class SelectAkbTypeHelper extends SelectHelper{
	private AkbSelectListener akbSelectListener;
	private int sel = 0;
	private Context context;
	private static String PREF_NAME = "SelectAkbTypeHelper.SharedPreferences";
	
	public interface AkbSelectListener{
		void onAkbSelect(int type);
	}
	
	public SelectAkbTypeHelper(Context context){
		this.context = context;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		sel = pref.getInt(PREF_NAME, 0);
	}
	
	public Dialog createDialog(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setSingleChoiceItems(R.array.akbTypes, sel, this);
		return builder.create();
	}
	
	@Override
	public void setControl(TextView view) {
		super.setControl(view);
		updateControl(sel);
	}
	
	@Override
	protected void applySelect(int which) {
		super.applySelect(which);
		sel = which;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = pref.edit();
		ed.putInt(PREF_NAME, which);
		ed.commit();
		updateControl(which);
		fireAkbSelectListener(which);
	}
	
	private void updateControl(int which){
		TextView tv = (TextView) getControl();
		tv.setText(tv.getContext().getResources().getStringArray(R.array.akbTypes)[which]);
	}
	
	public void setAkbSelectListener(AkbSelectListener listener){
		akbSelectListener = listener;
	}
	
	private void fireAkbSelectListener(int type){
		if (akbSelectListener != null)
			akbSelectListener.onAkbSelect(type);
	}
	
	public int getSelType(){ return sel; }
}
