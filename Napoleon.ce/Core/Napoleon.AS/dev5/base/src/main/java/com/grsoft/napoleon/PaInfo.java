package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.PaImpl;
import com.grsoft.napoleon.printsources.PaSource;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PaInfo extends BaseActivity {
	private static final int WAIT_FOR_PRINT_DLG = 1;
	
	PaImpl paImpl = new PaImpl();
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, PaInfo.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.painfo);
		
		if (paImpl.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID))){
			((TextView) findViewById(R.id.tvNumber)).setText(paImpl.getData().number);
			((TextView) findViewById(R.id.tvData)).setText(Util.simpleDateFormat.format(paImpl.getData().date));
			((TextView) findViewById(R.id.tvPeriod)).setText(Util.simpleDateFormat.format(paImpl.getData().period));
			((TextView) findViewById(R.id.tvSum)).setText(Util.IntToScaleStr(paImpl.getData().sum, Consts.SUM_SCALE));
			
			((ImageButton)findViewById(R.id.btnPrint)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SelectPrinFormDlg.createPrintForm((Activity)v.getContext(), 
							new PaSource(paImpl), WAIT_FOR_PRINT_DLG, "pa",
							new Runnable() {
								
								@Override
								public void run() {
									finish();
								}
							});
				}
			});
		}
	}
	
	void save() {
		TextView tv = (TextView)findViewById(R.id.tvNumber);
		if( tv instanceof EditText) {
			paImpl.getData().number = ((EditText)tv).getText().toString();
			paImpl.write();
			paImpl.close();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			save();
			finish();
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
	}
}
