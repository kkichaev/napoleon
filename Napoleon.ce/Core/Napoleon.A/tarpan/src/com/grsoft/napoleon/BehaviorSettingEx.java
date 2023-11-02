package com.grsoft.napoleon;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.ConfigManager;

public class BehaviorSettingEx extends BehaviorSetting {
	private static final int SELECT_FOLDER_DLG = R.id.select_folder_dlg;
	public static final String SETING_NAME = "BehaviorSettingEx";
	public static final String REMNANTS_SHOW = "remnants_show";
	public static final String ALLOW_ADD_ORG = "allow_add_org";
	public static final String TIME_FROM_HOUR = "time_from_hour";
	public static final String TIME_FROM_MIN = "time_from_min";
	public static final String TIME_TO_HOUR = "time_to_hour";
	public static final String TIME_TO_MIN = "time_to_min";
	public static final String PREZENT_PATH = "PREZENT_PATH";
	public static final String BUH_KEY = "buh_nam";
	public static final int DEF_HOUR_FROM = 9;
	public static final int DEF_MIN_FROM = 0;
	public static final int DEF_HOUR_TO = 18;
	public static final int DEF_MIN_TO = 0;
	CheckBox cbRemnants;
	CheckBox cbAddOrg;
	EditText edPrezentPath;
	private Spinner spNavType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getContentViewID() {
		return R.layout.behavior_settingex;
	}

	@Override
	public void save() {

		config.isAutostart = cbAutostart.isChecked();
		config.isService = cbAutostartAsService.isChecked();

		if (Features.PRINT_MODULE)
			config.printSource = ((String) spPrintSrc.getSelectedItem());

//		if (Features.USER_CAN_SCRIPT_OFF)
//			config.scriptOff = cbScriptOff.isChecked();

		if (cbVisitToDel != null && spVisitToDel != null && cbVisitToDel.isChecked()) {
			config.day_to_del_visit = Integer.parseInt((String) spVisitToDel.getSelectedItem());
		} else
			config.day_to_del_visit = 0;

		if (spMonthRecreate != null)
			config.monthsToRecreate = Integer.parseInt((String) spMonthRecreate
					.getSelectedItem());
		else
			config.monthsToRecreate = 1;

		ConfigManager.save();

		Editor ed = getApplication().getSharedPreferences(SETING_NAME,
				Context.MODE_PRIVATE).edit();
		ed.putBoolean(REMNANTS_SHOW, cbRemnants.isChecked());
		ed.putBoolean(ALLOW_ADD_ORG, cbAddOrg.isChecked());
		ed.putString(PREZENT_PATH, edPrezentPath.getText().toString().trim());
		ed.commit();

		if (cbRemnants.isChecked()) {
			if (!DocTypeBase.docTypes.contains(RemnantsDoc.instance()))
				DocTypeBase.addType(RemnantsDoc.instance());
		} else
			DocTypeBase.removeType(RemnantsDoc.instance());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SELECT_FOLDER_DLG:
			return adapter.createSelectFolderDlg(edPrezentPath);
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	protected void init() {
		super.init();
		cbRemnants = (CheckBox) findViewById(R.id.cbRemnants);
		cbAddOrg = (CheckBox) findViewById(R.id.cbAddOrg);
		edPrezentPath = (EditText) findViewById(R.id.edPrezentPath);

		SharedPreferences pref = getApplication().getSharedPreferences(
				SETING_NAME, Context.MODE_PRIVATE);
		cbRemnants.setChecked(pref.getBoolean(REMNANTS_SHOW, false));
		cbAddOrg.setChecked(pref.getBoolean(ALLOW_ADD_ORG, false));

		edPrezentPath.setInputType(InputType.TYPE_NULL);
		edPrezentPath.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(SELECT_FOLDER_DLG);
			}
		});
		
		edPrezentPath.setText(pref.getString(PREZENT_PATH, "/sdcard/napoleon/prezent"));

	}

	private SelectFolderAdapter adapter = new SelectFolderAdapter(this);

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SELECT_FOLDER_DLG:
			prepareSelectFolderDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private void prepareSelectFolderDlg(Dialog dialog) {
		if (edPrezentPath.getText().toString().trim().length() > 0) {
			File f = new File(edPrezentPath.getText().toString());

			if (f != null && f.isDirectory())
				adapter.setFolder(f);
		}
	}
}
