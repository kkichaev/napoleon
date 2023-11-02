/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   21/04/2011   creating
 */
package com.grsoft.napoleon;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.SettingActivity;

/***
 * Настройки внешнего вида и поведеия
 * @author kki
 *
 */
public class BehaviorSettingW extends SettingActivity {
	public static final String TAG = "BehaviorSetting";
//	private CheckBox cbVibrate;
	protected CfgNplW config;
	private CheckBox cbAllowRotateScreen;
	protected CheckBox cbAutostart;
	protected CheckBox cbAutostartAsService;
    protected Spinner spPrintSrc;
    protected CheckBox cbScriptOff;
    protected CheckBox cbVisitToDel;
    protected Spinner spVisitToDel;
    protected Spinner spMonthRecreate;
    protected EditText edPrezentPath;
    private SelectFolderAdapter adapter = new SelectFolderAdapter(this);
    
    private static final int SELECT_FOLDER_DLG = R.id.select_folder_dlg;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewID());
		
//		cbVibrate = (CheckBox) findViewById(R.id.cbVibrate);
		cbAllowRotateScreen = (CheckBox) findViewById(R.id.cbAllowRotateScreen);
		cbAutostart = (CheckBox) findViewById(R.id.cbAutostart);
		edPrezentPath = (EditText) findViewById(R.id.edPrezentPath);
		
		if(edPrezentPath != null && (Features.PRESENTATION_ON_SDCARD || Features.CAN_CHANGE_PRESENT_FOLDER)){
			edPrezentPath.setInputType(InputType.TYPE_NULL);
			edPrezentPath.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog(SELECT_FOLDER_DLG);
				}
			});
		}
		
		cbAutostart.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (cbAutostartAsService != null)
					cbAutostartAsService.setEnabled(isChecked);
			}
		});
		
		cbAutostartAsService = (CheckBox) findViewById(R.id.cbAutostartAsService);
		spPrintSrc = (Spinner)findViewById(R.id.spPrintSrc);
		
		findViewById(R.id.llPrintSource).setVisibility(Features.PRINT_MODULE ? View.VISIBLE : View.GONE);
		
		cbScriptOff = (CheckBox)findViewById(R.id.cbScriptOff);
		cbScriptOff.setVisibility(Features.USER_CAN_SCRIPT_OFF ? View.VISIBLE : View.GONE);
		
		cbVisitToDel = (CheckBox)findViewById(R.id.cbVisitToDel);
		spVisitToDel = (Spinner)findViewById(R.id.spVisitToDel);

		spMonthRecreate = (Spinner)findViewById(R.id.spMonthRecreate);
		
		init();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SELECT_FOLDER_DLG)
			return adapter.createSelectFolderDlg(edPrezentPath);
		else
			return super.onCreateDialog(id);
	}
	
	protected int getContentViewID() {
		return R.layout.behavior_setting;
	}
	
	@Override
	public void save() {
//		config.vibration = cbVibrate.isChecked();
		config.allowRotateScreen = cbAllowRotateScreen.isChecked();
		config.isAutostart = cbAutostart.isChecked();
		config.isService = cbAutostartAsService.isChecked();
		
		if (Features.PRINT_MODULE)
			config.printSource = ((String) spPrintSrc.getSelectedItem());
		
//		if (Features.USER_CAN_SCRIPT_OFF)
//			config.scriptOff = cbScriptOff.isChecked();
		
		if (cbVisitToDel != null && spVisitToDel != null && cbVisitToDel.isChecked()){
			config.day_to_del_visit = Integer
					.parseInt((String)spVisitToDel.getSelectedItem());
		}else
			config.day_to_del_visit = 0;
		
		applayRecreatePeriod();
		
		if((Features.PRESENTATION_ON_SDCARD || Features.CAN_CHANGE_PRESENT_FOLDER) && edPrezentPath != null)
			config.presentpath = edPrezentPath.getText().toString().trim();

		ConfigManager.save();
	}

	protected void applayRecreatePeriod() {
		if (spMonthRecreate != null)
			config.monthsToRecreate = Integer
					.parseInt((String)spMonthRecreate.getSelectedItem());
		else
			config.monthsToRecreate = 1;
	}
	
	@Override
	public void update() {
		init();
	}

	protected void init() {
		config = (CfgNplW) ConfigManager.getConfig();
//		cbVibrate.setChecked(config.vibration);
		cbAllowRotateScreen.setChecked(config.allowRotateScreen);
		cbAutostart.setChecked(config.isAutostart);
		
		if (config.isAutostart)
			cbAutostartAsService.setChecked(config.isService);
		else
			cbAutostartAsService.setEnabled(false);
		
		if (Features.PRINT_MODULE){
			Adapter a = spPrintSrc.getAdapter();
			
			if (a != null){
				int len = a.getCount();
				String ps = config.printSource;
				
				for(int i = 0; i < len; i++)
					if (a.getItem(i).equals(ps)){
						spPrintSrc.setSelection(i, true);
						break;
					}
			}
		}
		
//		if (Features.USER_CAN_SCRIPT_OFF)
//			cbScriptOff.setChecked(config.scriptOff);
		
		if (cbVisitToDel != null && spVisitToDel != null){
			
			cbVisitToDel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					spVisitToDel.setEnabled(isChecked);
				}
			});
			
			if (((CfgNplW)config).day_to_del_visit == 0)
					spVisitToDel.setEnabled(false);
			else{
				cbVisitToDel.setChecked(true);
				Adapter vda = spVisitToDel.getAdapter();
				
				if (vda != null){
					for(int i = 0; i < vda.getCount(); i ++){
						if(vda.getItem(i).toString()
								.equals(Integer.toString(((CfgNplW)config).day_to_del_visit))){
							spVisitToDel.setSelection(i,  true);
							break;
						}
					}
				}
			}
		}
		
		initRecreatePeriod();
		
		if((Features.PRESENTATION_ON_SDCARD || Features.CAN_CHANGE_PRESENT_FOLDER) && edPrezentPath != null){
			findViewById(R.id.tvPrezentPath).setVisibility(View.VISIBLE);
			edPrezentPath.setVisibility(View.VISIBLE);
			edPrezentPath.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog(SELECT_FOLDER_DLG);
				}
			});
			
			edPrezentPath.setText(config.presentpath);
		}
	}

	protected void initRecreatePeriod() {
		if(spMonthRecreate != null){
			Adapter vda = spMonthRecreate.getAdapter();
			
			if (vda != null){
				for(int i = 0; i < vda.getCount(); i ++){
					if(vda.getItem(i).toString()
							.equals(Integer.toString(((CfgNplW)config).monthsToRecreate))){
						spMonthRecreate.setSelection(i,  true);
						break;
					}
				}
			}
		}
	}

	@Override
	public int getName() {
		return R.string.general;
	}

	@Override
	public int getIcon() {
		return R.drawable.setting_behavior;
	}
}
