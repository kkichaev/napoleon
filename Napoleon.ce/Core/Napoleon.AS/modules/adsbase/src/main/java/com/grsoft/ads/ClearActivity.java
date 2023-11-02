package com.grsoft.ads;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ClearActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	public static Class<? extends Activity> activity = ClearActivity.class;
	
	private View btnClear;
	private CheckBox cbPictures;
	private CheckBox cbAttach;
	private View tvWarning;
	
	public static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.cleardata);
		
		btnClear = findViewById(R.id.btnClear);
		cbPictures = findViewById(R.id.cbPictures);
		cbAttach = findViewById(R.id.cbAttach);
		tvWarning = findViewById(R.id.tvWarning);
		
		btnClear.setOnClickListener(this);
		cbPictures.setOnCheckedChangeListener(this);
		cbAttach.setOnCheckedChangeListener(this);
		tvWarning.setVisibility(View.GONE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return createWaitDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.btnClear)
			clearData();
	}

	private void clearData() {
		ClearProcess.Params arg = new ClearProcess.Params();
		arg.pictures = cbPictures.isChecked();
		arg.attachments = cbAttach.isChecked();
		new ClearProcess(this).execute(new ClearProcess.Params[] {arg});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		tvWarning.setVisibility(cbPictures.isChecked() || cbAttach.isChecked() ? View.VISIBLE : View.GONE);
	}
}
