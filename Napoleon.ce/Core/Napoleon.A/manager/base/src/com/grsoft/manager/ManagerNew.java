package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.ManagerAgentHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.Division;
import com.grsoft.dataobjects.ReportOnAgentForDatesParams;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.LoginData;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.Util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ManagerNew extends DrawerActivity implements OnItemClickListener {
	public static Class<? extends Activity> activity = ManagerNew.class;
	private ListView list;
	private ManagerNewAdapter adapter;
	private SwipeRefreshLayout swipeRefresh;
	private static final int PERMISSION_REQUEST = 0;

	public static void open(Context context){
		Intent i = new Intent(context, activity);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));
		
		list = (ListView) findViewById(R.id.list);
		swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.refresh);
		
		adapter = new ManagerNewAdapter(this);
		adapter.load(new Date());
		adapter.updateSummaryView(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				doSync();
			}
		});
		
		registerForContextMenu(list);
		
		startService();
		checkApplicationPermission();
	}

	private void checkApplicationPermission(){
		if(Build.VERSION.SDK_INT >= 23) {
			List<String> pms = new ArrayList<String>();
			pms.add(Manifest.permission.ACCESS_FINE_LOCATION);
			pms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			pms.add(Manifest.permission.CALL_PHONE);
			pms.add(Manifest.permission.CAMERA);
			pms.add(Manifest.permission.READ_PHONE_STATE);

			for(String p : pms) {
				if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, pms.toArray(new String[]{}), PERMISSION_REQUEST);
				}
			}
		}
	}

	private void startService() {
		Intent intent = new Intent(this, ManagerService.class);
		startService(intent);
	}

	@Override protected int getLayoutID() { return R.layout.manager_new; }
	
	@Override
	protected void postSyncUpdate() {
		reloadData();
		swipeRefresh.setRefreshing(false);
	}

	private void reloadData() {
		adapter.load(getDate());
		adapter.updateSummaryView(this);
		adapter.notifyDataSetChanged();
	}

	@Override protected String getActionBarTitle() { return getString(R.string.works); }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AgentReportData d = (AgentReportData) parent.getItemAtPosition(position);
		Date date = getDate();
		SyncDetail.sync(this, createUpdateCtrl(this, date, d.id), d.id, date, true);
	}
	
	private UpdateCtrl createUpdateCtrl(final Activity activity, final Date date, final String userid) {
		return new UpdateCtrl() {
			@Override public void onFinish(boolean result) {
				if( result )
					AgentRouteNew.open(activity, userid, date, AgentRouteNew.MAP_VIEW_TYPE);
			}
			@Override public void updateCtrl(boolean enabled) {} };
	}
	
	@Override
	protected void initHitchings(List<Hitching> list) {
		List<Hitching> repResult = new ArrayList<Hitching>();

		repResult.add(new ManagerAgentHitching());
		repResult.add(new RcvNewHitching(Division.class, "Division"));
		repResult.add(new Hitching(AgentReportData.class, "TypeName"));
		
		Date start = Util.resetTime(getDate());
		Calendar c = Calendar.getInstance();
		c.setTime(start);
		c.add(Calendar.DATE, 1);
		Date finish = c.getTime();
//		c.add(Calendar.MONTH, -1);
//		c.add(Calendar.DATE, -1);
//		start = c.getTime();
		list.add(new ReportHitching(getSummaryReportName(), new ReportOnAgentForDatesParams(start, finish), repResult));
	}

	protected String getSummaryReportName() {
		return "summary";
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.maincontextmenu, menu);
		
		MenuItem i = menu.findItem(R.id.itMessage);
		
		if( i != null)
			i.setVisible(false);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		Adapter a = list.getAdapter();
		AgentReportData ai = (AgentReportData) a.getItem(info.position);

		int id = item.getItemId(); 

		if (id == R.id.itNapoleon){
			runNapoleon(this, ai.id);
			return true;
		}else
			return false;
	}
	
	
	private void runNapoleon(ManagerNew context, String id) {
		showDialog(R.id.wait_dlg);
		
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			
			@Override
			public void run() {
				dismissDialog(R.id.wait_dlg);
			}
		}, 1000 * 30);
		
		CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
		LoginData ld = new LoginData(cfg.login, cfg.passw, cfg.impersonate, context);
		StringBuilder sb = new StringBuilder();
		sb.append(cfg.login).append(";").append(cfg.passw).append(";")
				.append(cfg.address).append(";").append(cfg.address2)
				.append(";").append(cfg.port).append(";").append(id).append(";").append(ld.getDuration());

		Intent intent = new Intent("com.grsoft.napoleon.StartFromManager");
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		intent.setType("text/plain");
		intent.setPackage("com.grsoft.napoleon");
		
		context.sendBroadcast(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.permission_block_dlg)
			return createPermissionBlockDlg();
		else if (id == R.id.wait_dlg)
			return createWaitDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}


	private Dialog createPermissionBlockDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.permission_dlg_text);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", getPackageName(), null);
				intent.setData(uri);
				startActivity(intent);
			}
		});
		
		builder.setCancelable(false);
		return builder.create();
	}
}
