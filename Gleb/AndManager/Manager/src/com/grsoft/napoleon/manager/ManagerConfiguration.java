package com.grsoft.napoleon.manager;

import static com.grsoft.util.Debug.dbgPrint;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.Division;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Util;

public class ManagerConfiguration extends Activity {

	private static final int EXPORT_DLG_ID = 0;
	private static final int IMPORT_DLG_ID = 1;

	public static void open(Context context) {
		Intent i = new Intent(context, ManagerConfiguration.class);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);
		update();
	}
	
	private void update() {
		EditText edIp = (EditText) findViewById(R.id.edIp);
		EditText edIp2 = (EditText) findViewById(R.id.edIp2);
		EditText edPort = (EditText) findViewById(R.id.edPort);
		EditText edLogin = (EditText) findViewById(R.id.edLogin);
		EditText edPassw = (EditText) findViewById(R.id.edPassw);
		
		CfgMgr config = (CfgMgr) ConfigManager.getConfig();
		
		edIp.setText(config.address);
		edIp2.setText(config.address2);
		edPort.setText(Integer.toString(config.port));
		edLogin.setText(config.login);
		edPassw.setText(config.passw);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting_opt_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.itExport:
			showDialog(EXPORT_DLG_ID);
			break;
		case R.id.itImport:
			showDialog(IMPORT_DLG_ID);
			break;
		case R.id.itClearBase:
			Path.clearDataDir();
			DataBaseManager.clearBase();
			DocTypeBase.checkTables();
			DbWriter.checkDBTable(AgentInfo.class);
			DbWriter.checkDBTable(AgentReportData.class);
			DbWriter.checkDBTable(Division.class);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case EXPORT_DLG_ID:
		case IMPORT_DLG_ID:
			return makeExportImportDlg(id);
		default:
			return super.onCreateDialog(id);
		}
	}


	private Dialog makeExportImportDlg(int type){
		final String EXPORT_TITLE = getString(R.string.save_setting);
		final String IMPORT_TITLE = getString(R.string.load_setting);
		final View view = View.inflate(this, R.layout.inputfilename, null);
		
		class OKClickListener implements OnClickListener{
			int dlgId;
			
			public OKClickListener(int dlgId){
				this.dlgId = dlgId;
			}
			
			@Override
			public void onClick(View v) {
				dismissDialog(dlgId);
			}
		}
		
		class ExportListener extends OKClickListener{
			
			public ExportListener() {
				super(EXPORT_DLG_ID);
			}
			
			public void onClick(View v) {
				super.onClick(v);
				save();
				EditText edInput = (EditText) view.findViewById(R.id.edInput);
				CheckBox cbBaseExport = (CheckBox) view.findViewById(R.id.cbExportBase);
				new ExportTask().execute(edInput.getText().toString(), 
						cbBaseExport.isChecked());
			};
		}
		
		class ImportListener extends OKClickListener{
			
			public ImportListener() {
				super(IMPORT_DLG_ID);
			}
			
			public void onClick(View v) {
				super.onClick(v);
				EditText edInput = (EditText) view.findViewById(R.id.edInput);
				new ImportTask().execute(edInput.getText().toString());
			};
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setView(view);
		builder.setTitle(type == EXPORT_DLG_ID ? EXPORT_TITLE : IMPORT_TITLE);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		
		CheckBox cbBaseExport = (CheckBox)view.findViewById(R.id.cbExportBase);
		if( type == IMPORT_DLG_ID )
			cbBaseExport.setVisibility(View.GONE);

		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(EXPORT_DLG_ID);
			}
		});
		
		Button btnOK = (Button) view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(type == EXPORT_DLG_ID ? 
				new ExportListener() : new ImportListener()); 
		
		return builder.create();
	}
	
	class ExportTask extends AsyncTask<Object, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Object... params) {
			try{
				String distFlolderName = (String) params[0];
				ConfigManager.save(ManagerConfiguration.this, distFlolderName);
				
				boolean baseShouldBeCopied = (Boolean) params[1];
				
				if (baseShouldBeCopied){
					File src = new File(Path.getDataBasePath());
					File sdcard = Environment.getExternalStorageDirectory();
					File dist = new File(new File(sdcard, distFlolderName), Path.BASE_NAME);
					Util.copy(src,dist);
				}
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if (result)
				Toast.makeText(ManagerConfiguration.this, 
					R.string.file_save_succs, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(ManagerConfiguration.this, 
					R.string.file_save_error, Toast.LENGTH_LONG).show();
		}
	}
	
	class ImportTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			try{
				ConfigManager.load(ManagerConfiguration.this);
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			
			if (result){
				ConfigManager.save();
				Toast.makeText(ManagerConfiguration.this, R.string.setting_has_been_read_succs, Toast.LENGTH_LONG).show();
				update();
			} else
				Toast.makeText(ManagerConfiguration.this, 
					R.string.setting_load_error, Toast.LENGTH_LONG).show();
		}
	}
	
	public void save() {
		EditText edIp = (EditText) findViewById(R.id.edIp);
		EditText edIp2 = (EditText) findViewById(R.id.edIp2);
		EditText edPort = (EditText) findViewById(R.id.edPort);
		EditText edLogin = (EditText) findViewById(R.id.edLogin);
		EditText edPassw = (EditText) findViewById(R.id.edPassw);
		
		try
		{
			int port = Integer.parseInt(edPort.getText().toString());
			Config config = ConfigManager.getConfig();
			
			config.address = edIp.getText().toString();
			config.address2 = edIp2.getText().toString();
			config.port = port;
			config.port2 = port;
			config.login = edLogin.getText().toString();
			config.passw = edPassw.getText().toString();
						
			ConfigManager.save();
		}
		catch(Exception exception)
		{
			dbgPrint(exception.getMessage());
		}
		
	}
	
	@Override
	protected void onStop() {
		save();	
		super.onStop();
	}
}
