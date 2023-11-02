/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Наполеон
 *
 * kki   10/11/2010   creating
 */
package com.grsoft.napoleon;

import static com.grsoft.util.Debug.dbgPrint;
import static com.grsoft.util.Util.GrServerColorToSystem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.FilterCmp;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.DateStampFormat;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.util.ReceiveRemnants;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.DialogOwner;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.MainExceptionHandler;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPreparedEvent;
import com.grsoft.util.NapoleonServiceW;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Updater;
import com.grsoft.view.RegDurationActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Napoleon extends RegDurationActivity
	implements Selector, DialogOwner
{	
//	public static final int DLG_WARNING_IF_ORG_IN_STOP_LIST = 0;
	protected  static final int DLG_MAIN_MENU = 1;
	static final int DLG_DOC = 2;
	final int DOC_SUM_DLG = 3;
	protected static final String PERIOD_TYPE = "period_type";
	private static final int MONTH_TYPE = 1;

	private ImageButton btnLines;
	protected LinesCountController linesController;
	public ListView lvMainOrgs;
	protected ListViewMode listViewMode = null; 
	protected final String LIST_MODE = "ListMode";
	protected EditText edFind;
	
	protected BaseAdapter mainOrgsAdapter;
	protected BaseAdapter orgFoldersAdapter;
	protected FindOnClickListener findOnClickListener;
	OrgSumImpl os = new OrgSumImpl();
	
	public static Class<? extends Service> serviceType = NapoleonServiceW.class; 
	public static MenuPreparedEvent docMenuPrepared = new MenuPreparedEvent();
	public static MenuPreparedEvent mainMenuPrepared = new MenuPreparedEvent();
	
	public static DocType prevDocType = null;
	
	private Dialog activeDialog;
	private IShared shared;
	private static final String LOGIN_PREF = "LoginData";
	private static final String DURATION_KEY = "Duration";
	protected TextView tvTotalSum;
	protected FindTextWatcher textWatcher;
	
	@Override
	protected void onStop() {
		super.onStop();
		close();
	}
	
	void close() {
		if (mainOrgsAdapter != null && mainOrgsAdapter instanceof MainOrgsAdapter)
			((MainOrgsAdapter)mainOrgsAdapter).close();
		
		if (orgFoldersAdapter != null && orgFoldersAdapter instanceof OrgFoldersAdapter)
			((OrgFoldersAdapter)orgFoldersAdapter).close();
		
		os.close();
	}
	
	protected int getRowResourceID() { return R.layout.main_list_row; }
	
	protected int getResourceID() { return R.layout.main; }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Toast.makeText(this, "Запуск программы Наполеон", Toast.LENGTH_LONG).show();
		super.onCreate(savedInstanceState);
		setContentView(getResourceID());
		
		dbgPrint("Main activity is ready");
		
		if (!Features.IS_MARKET_VERSION)
			Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this, Path.SHARED_FOLDER));
		
		initView();
		lvMainOrgs = (ListView) findViewById(R.id.lvMainOrgs);
		lvMainOrgs.setDividerHeight(0);
		tvTotalSum = (TextView) findViewById(R.id.tvTotalSum);
		
		if(Features.DOC_SUM_BY_PERIOD)
			tvTotalSum.setOnLongClickListener(new OnLongClickListener() { 
				@Override
				public boolean onLongClick(View v) {
					if (DocType.getCurDoc() == DebtDoc.instance())
						return false;
					showDialog(DOC_SUM_DLG);
					return true;
				}
			});
		
		try
		{
			mainOrgsAdapter = getMainOrgAdapter();
			orgFoldersAdapter = getOrgFoldersAdapter();
			
			setListMode(ListViewMode.parseInt(getPrefValue(LIST_MODE, ListViewMode.ORG_LIST.val)));
		} catch(Exception e) { e.printStackTrace(); }
		
		ivGoUp = (ImageView) findViewById(R.id.ivGoUp);
		ivGoUp.setVisibility(View.GONE);
		
		init();
		
		adjustViewForDocType(DocType.getCurDoc());
		
		Intent intent = new Intent(this, serviceType);
		startService(intent);
		dbgPrint("Main activity configure end");
	
		registerForContextMenu(findViewById(R.id.lvMainOrgs));
	
		try {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if( tm != null )
				ServerCommand.DeviceID = tm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SharedPreferences pref = getSharedPreferences(
				StartFromManager.PREFERENCE, Context.MODE_PRIVATE);
		if (pref.getBoolean(StartFromManager.OPENSYNC, false)
				&& Path.getAgentInfo().exists()) {
			Editor ed = pref.edit();
			ed.putBoolean(StartFromManager.OPENSYNC, false);
			ed.commit();
			UpdateDBW.openSync(this);
		}

		if (ConfigManager.getConfig().impersonate.trim().length() > 0) {
			Intent shared = new Intent("com.grsoft.napoleon.ExchangeService");
			bindService(shared, conn, Context.BIND_AUTO_CREATE);
		}
	}
	
	protected void initView() {}

	public void init() {
		lvMainOrgs.setOnItemClickListener(getItemOnClickListner());
		
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				lvMainOrgs, btnLines, Napoleon.this, true);
		linesController = linesOnClickListener.getController();
		
		findViewById(R.id.tvFirstColumnCaption).setOnClickListener(new FirstColumnCaptionOnClickListener());
		
		findViewById(R.id.btnMode).setOnClickListener(new OnClickListenerToNotify()
		{
			@Override
			public void onClick(View v) {  
				super.onClick(v);
				setTopLevelForTableHeader();
				findOnClickListener.resetFilter();
				switchListMode();
			}
		});
		
		llFind = (LinearLayout)findViewById(R.id.llFind);
		edFind = (EditText) findViewById(R.id.edFind);
		textWatcher = new FindTextWatcher(edFind, lvMainOrgs);
		edFind.addTextChangedListener(textWatcher);
		View v = findViewById(R.id.btnDelFind);
		if( v != null )
			v.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					edFind.setText("");
				}
			});
		
		findOnClickListener = createFindOnClickListener();
		btnFind = (ImageButton) findViewById(R.id.btnFind);
		btnFind.setOnClickListener(findOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( mainMenu == null )
			mainMenu = createMainMenuList();
		
		for (MenuHandler h : mainMenu)
			menu.add(h.name);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		for(MenuHandler h : mainMenu)
			if(h.name.equals(item.getTitle())){
				h.handler.run();
				break;
			}
		
		return true;
	}

	protected FindOnClickListener createFindOnClickListener(){
		return new FindOnClickListener(edFind, lvMainOrgs, llFind);
	}
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		return new OrgFoldersAdapter();
	}

	protected BaseAdapter getMainOrgAdapter() throws IllegalAccessException,
			InstantiationException {
		return new MainOrgsAdapter(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)	{
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		DateStampFormat.utc = Calendar.getInstance().getTimeZone();
		
		findViewById(R.id.btnDocFilter).setOnClickListener(createDocFilter());

		if( Features.COST_MANAGER != null )
			Features.COST_MANAGER.initCost(this);

		// обновить данные после синхронизации
		if (mainOrgsAdapter != null && mainOrgsAdapter instanceof Refresh)
			((Refresh)mainOrgsAdapter).refresh();
		
		if (orgFoldersAdapter != null && orgFoldersAdapter instanceof Refresh)
			((Refresh)orgFoldersAdapter).refresh();

		if( llFind != null )
			llFind.setVisibility(View.GONE);

		if( prevDocType != null ) {
			DocType.setCurDoc(prevDocType);
			prevDocType = null;
		}
		
		DocType curDocType = DocType.getCurDoc();
		if( Features.SCRIPT_DOC && ScriptDefImpl.canScripting() ) {
			DocType scriptDoc = ScriptDoc.instance();
			if( curDocType != scriptDoc ) {
				DocType.setCurDoc(scriptDoc);
				curDocType = scriptDoc;
			}
		}else if (curDocType.equals(ScriptDoc.instance())){
			setDefaultDocType();
		}
		
		adjustViewForDocType(DocType.getCurDoc());
		updateModeImage();
	}

	protected void setDefaultDocType() {
		DocType.setCurDoc(OrderDoc.instance());
	}

	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this);
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
	}
	
	public void updateModeImage(){
		ImageButton btnMode = (ImageButton) findViewById(R.id.btnMode);
		btnMode.setImageResource((listViewMode == ListViewMode.ROUTE_LIST ) ? R.drawable.clients : R.drawable.route);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		View view = ((AdapterContextMenuInfo)menuInfo).targetView;
		Object tag =  view.getTag();
		if( tag instanceof OrgFolders )
			return;
		
		Long rowid = (Long) tag;
		
		if (rowid != null ) {
			menu.add(R.string.show_on_map);
			if( isPotencialOrg(rowid)){
				menu.add(R.string.edit);
				menu.add(R.string.visit);
			}
		}
	}
	
	protected boolean isPotencialOrg(long rowid){
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.read(rowid);
		orgImpl.close();
		
		return orgImpl.getData().isPotencial();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		CharSequence title = item.getTitle();
		Long rid = (Long)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		if( title.equals(getString(R.string.show_on_map))) {
			OrgImpl org = new OrgImpl();
			org.read(rid);
			org.close();
			
			try {
				String address = org.getData().address;
				String uri = String.format("geo:0,0?q=%s", address );
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} else if(title.equals(getString(R.string.edit)))
			PotenzialOrg.open(this, rid, true);
		else if (title.equals(getString(R.string.visit)))
			DocumentsW.open(this, rid, true);
		
		return true;
	}
	
	ReceiveRemnants remnantsReceiver = null;
	void updateRemnants() {
		if( remnantsReceiver == null ) {
			remnantsReceiver = new ReceiveRemnants(this, new ReceiveRemnants.TaskDoneHandler() {
				@Override public void finish(NetworkAsyncTask task) {
					if( remnantsReceiver == task )
						remnantsReceiver = null;
				}

				@Override
				public void start() {
				}
			});
			remnantsReceiver.execute((Void[])null);
		}
	}
	
	ArrayList<MenuHandler> mainMenu = null;
	ArrayList<MenuHandler> docMenu = null;
	protected LinearLayout llFind;
	protected ImageButton btnFind;
	protected ImageView ivGoUp;
	
	protected ArrayList<MenuHandler> createDocMenuList() {
		docMenu = new ArrayList<MenuHandler>();

		docMenu.add(new MenuHandler(getString(R.string.doc_list), new Runnable() {			
			@Override public void run() { DocList.open(Napoleon.this); }
		}));
		
		docMenu.add(new MenuHandler(getString(R.string.msg_list), new Runnable() {			
			@Override public void run() { Messages.open(Napoleon.this); }
		}));
		
		docMenu.add(new MenuHandler(getString(R.string.price_list), new Runnable() {			
			@Override public void run() { Warehouse.open(Napoleon.this); }
		}));
		
		if(Features.REPORT_REQUEST)
			docMenu.add(new MenuHandler(getString(R.string.report_list), new Runnable() {			
				@Override public void run() { ReportList.open(Napoleon.this); }
			}));
		
		docMenuPrepared.menuPrepared(docMenu, Napoleon.this);
		return docMenu;
	}
	
	protected ArrayList<MenuHandler> createMainMenuList() {
		mainMenu = new ArrayList<MenuHandler>();
		
		mainMenu.add(new MenuHandler(getString(R.string.setting), new Runnable() {			
			@Override public void run() { Setting.open(Napoleon.this); }
		}));
		
		if( Features.RECIEVE_REMNANTS_IN_MAIN_MENU ) {
			mainMenu.add(new MenuHandler(getString(R.string.recieve_remains), new Runnable() {			
				@Override public void run() { updateRemnants(); }
			}));
		}
		
		mainMenu.add(new MenuHandler(getString(R.string.sync), new Runnable() {			
			@Override public void run() { doSync(); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.docs), new Runnable() {			
			@Override public void run() { showDialog(DLG_DOC); }
		}));

		if(Features.POTENZIAL_ORG)
			mainMenu.add(new MenuHandler(getString(R.string.add_org), new Runnable() {			
				@Override public void run() { PotenzialOrg.open(Napoleon.this); }
			}));

		mainMenu.add(new MenuHandler(getString(R.string.about), new Runnable() {			
			@Override public void run() { showAbout(Napoleon.this); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.exit), new Runnable() {	@Override public void run() { exit();	}}));
		mainMenuPrepared.menuPrepared(mainMenu, Napoleon.this);
		return mainMenu;
	}
	
	protected Dialog createMainMenuDlg(){
		if( mainMenu == null )
			mainMenu = createMainMenuList();
		
		return createMenuDlg(getString(R.string.menu), mainMenu);
	}
	
	protected Dialog createMenuDlg(String title, final ArrayList<MenuHandler> items){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
						
		int idx = 0;
		CharSequence[] titles = new CharSequence[items.size()];
		
		for(MenuHandler mh : items)
			titles[idx++] = mh.name;
		
		builder.setItems(titles, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which){
					items.get(which).handler.run();
			}
		});
		
		return builder.create();
	}
	
	@Override
	public void selectedType(DocType newDocType) {
		DocType curDoc = DocType.getCurDoc();
		if( newDocType != null && (curDoc == null  || newDocType.equals(curDoc) == false) )
			adjustViewForDocType(newDocType);
	}
	
	protected void adjustViewForDocType(DocType docType) {
		DocType.setCurDoc(docType, true);
		
		ImageButton btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setImageResource(docType.getResurceId());

//		close();
		refreshDocSum(docType);
		DocType.getCurDoc().viewOpened(this);
		
		BaseAdapter adapter = ((BaseAdapter)lvMainOrgs.getAdapter());
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	protected void refreshDocSum(DocType docType) {
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		int cur_type = pref.getInt(PERIOD_TYPE, 0);
		if (docType == DebtDoc.instance()) cur_type=0;		
		if(cur_type > 0){
			updateTotalSum(getDocSumByPeriod(docType, cur_type), 0);
		}else
			updateTotalSum(OrgSumImpl.docSum(docType.getName()), 0);
	}
	
	protected String makePeriodWhere(DocType docType, int period) { 
		StringBuilder where = new StringBuilder();
		
		if(period > 0){
			long begin, end;
			Calendar calendar = Calendar.getInstance();
			if(period == MONTH_TYPE) {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				begin = calendar.getTime().getTime();
				
				calendar.add(Calendar.MONTH, 1);
				end = calendar.getTime().getTime() - 1000; // отнимим 1 секунду от нового месяца
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MILLISECOND, 99);
				end = calendar.getTime().getTime();

				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				begin = calendar.getTime().getTime();
			}
			
			String fieldName = (docType.isCreatable()) ? "created" : "date";
			where.append(fieldName).append(" >= ").append(begin)
				.append(" and ").append(fieldName).append(" <= ").append(end);
		}

		return where.toString();
	}
	
	protected long getDocSumByPeriod(DocType docType, int period){
		long result = 0;
		String where = makePeriodWhere(docType, period);
		com.grsoft.napoleon.documents.DocList list = docType.docList(null, null, where);
		
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			result += d.sum();
		}
		
		list.close();
		
		return result;
	}
	
	public void switchListMode() {
		switch (listViewMode) {
			case ORG_LIST : setListMode(ListViewMode.ROUTE_LIST); break;
			case ROUTE_LIST: setListMode(ListViewMode.ORG_LIST); break;
		}
	}
	
	protected void setListMode(ListViewMode mode){
		if (listViewMode == mode)
			return;
		
		switch(mode) {
			case ORG_LIST : lvMainOrgs.setAdapter((BaseAdapter)mainOrgsAdapter); break;
			case ROUTE_LIST: lvMainOrgs.setAdapter(orgFoldersAdapter); break;
		}
		
		if( listViewMode != null )
			setPrefValue(LIST_MODE, mode.val);
		listViewMode = mode;
		updateModeImage();
		
		if(findOnClickListener != null)
			findOnClickListener.resetFilter();
	}

	public void setGoUpVisibility(boolean visible) {
		ImageView ivGoUp = (ImageView) findViewById(R.id.ivGoUp);
		ivGoUp.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_MAIN_MENU:
			return createMainMenuDlg();
		case DLG_DOC:
			return createDocMenuDlg();
		case DOC_SUM_DLG:
			return createDocSumDlg();
			
		default: return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case DOC_SUM_DLG:
			prepareDocSumDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}
	
	private Dialog createDocSumDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(
				getResources().getStringArray(R.array.doc_sum_by_period), 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
						int cur_type = pref.getInt(PERIOD_TYPE, 0);
						
						if(cur_type != which){
							Editor edit = pref.edit();
							edit.putInt(PERIOD_TYPE, which);
							edit.commit();
							refreshDocSum(DocType.getCurDoc());
						}
						
						dialog.dismiss();
					}
				});
		
		builder.setTitle(R.string.doc_sum_by_period_title);
		return builder.create();
	}
	
	private void prepareDocSumDlg(Dialog dialog) {
		ListView lv = ((AlertDialog)dialog).getListView();
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		lv.setItemChecked(pref.getInt(PERIOD_TYPE, 0), true);
	}

	protected void setOrgBackground(int pos, OrgImpl org, View v) {
		if( org != null && org.getData().isStopList())
			v.setBackgroundResource(getStopBkg());
		else {
			v.setBackgroundResource(pos % 2 != 0 ? 
										R.drawable.even_row_selector :
										R.drawable.list_selector);		
		}
	}

	protected int getStopBkg() { return R.drawable.list_grey_selector; }
	
	protected void setFirstColumnCaption(String caption) {
		((TextView) findViewById(R.id.tvFirstColumnCaption)).setText(caption);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( keyCode == KeyEvent.KEYCODE_MENU ) {
			showDialog(DLG_MAIN_MENU);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if(Features.SHOW_EXIT_WARNING)
			Toast.makeText(this, R.string.ask_to_exit, 
				Toast.LENGTH_LONG).show();
	}
	
	protected OnItemClickListener getItemOnClickListner() { return new OrglListOnClickListener(); }
	
	class RouteFilter implements FilterCmp
	{
		OrgImpl orgImpl = new OrgImpl();
		
		public void close() {
			orgImpl.close();
		}

		@Override
		public boolean compareTo(DataObject dataObject, String filter)
		{
			OrgFolderItem ofi = (OrgFolderItem) dataObject;
			orgImpl.getData().id = ofi.name;
			if (!orgImpl.read())
				return false;
			
			return orgImpl.getData().srchName.contains(filter.toUpperCase());
		}
		
	}
	
	public static void showAbout(final Activity owner) 
	{
        View messageView = owner.getLayoutInflater().inflate(R.layout.about, null, false);
        TextView tvLink = (TextView) messageView.findViewById(R.id.tvLink);
        AlertDialog.Builder builder = new AlertDialog.Builder(owner);
        builder.setView(messageView);
        builder.create();
        final AlertDialog dialog = builder.show();
        
        if(Features.LINKS_DISSALLOW){
	        tvLink.setEnabled(false);
	        tvLink.setMovementMethod(null);
        }
        
        tvLink.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						dialog.dismiss();
					}
				}).start();
			}
		});
        
        messageView.findViewById(R.id.btnCheckUpdates).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				new Updater(){
					protected void onPreExecute() {
						Toast.makeText(v.getContext(), R.string.check_updating,
								Toast.LENGTH_SHORT).show();
					};
					
					protected void onPostExecute(Boolean result) {
						if(!result)
							Toast.makeText(v.getContext(), R.string.update_not_found,
									Toast.LENGTH_SHORT).show();
					};
					
				}.execute(v.getContext());
			}
    		}
    	);
	}

	class OrglListOnClickListener implements OnItemClickListener
	{
		/***
		 * Текущая организация, на которую кликнули
		 * испольуется для запуска Dialog.open
		 * для того что бы можно было открыть Document 
		 * после вывода предупреждение для стоп-листа
		 * т.к Activity.showDialog(int, Bundle) 
		 * поддерживается только с API 8. 
		 */
		protected OrgImpl clickedOrg;
		
		public OrglListOnClickListener() {}
		

		public void resumeClick() {
			if (clickedOrg != null)
				DocumentsW.open(Napoleon.this, clickedOrg.getData());
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			Object tag = arg1.getTag();

			if (tag != null){
				if (tag instanceof OrgFolders)
					openOrgFolder(arg0, tag);
				else if( isPotencialOrg((Long)tag) )
					openPotencialOrg((Long)tag);
				else
					openOrgDocs(arg1, tag);
			}
		}
		
		protected void openPotencialOrg(long rowid){
			DocumentsW.open(Napoleon.this, rowid, true);
		}

		protected void openOrgDocs(View arg1, Object tag) {
			OrgImpl oi = new OrgImpl();			
			if (tag != null &&  oi.read((Long)tag)){
				openOrg(oi);
			}			
			oi.close();
		}
		
		/**
		 * При переопределении надо сохранить организацию из параметра в clickedOrg
		 * @param oi - организация на которую нажали
		 */
		protected void openOrg(OrgImpl oi) {
			DocType dt = DocType.getCurDoc();
			if(dt == OrderDoc.instance() && oi.getData().isStopList()){
				clickedOrg = oi;
//				showDialog(DLG_WARNING_IF_ORG_IN_STOP_LIST);
//			} else
			}
				DocumentsW.open(Napoleon.this, oi.getData());
		}

		protected void openOrgFolder(AdapterView<?> arg0, Object tag) {
			OrgFolders orgsFoldersImpl = (OrgFolders) tag;
			OrgFoldersAdapter adapter = (OrgFoldersAdapter)arg0.getAdapter();
			
			if (adapter.isTopLevel()){
				setGoUpVisibility(true);
			}
			setFirstColumnCaption(orgsFoldersImpl.name);

			adapter.itemsMode(orgsFoldersImpl);
			adapter.notifyDataSetChanged();
		}
	}
	
	protected void drawOrg(OrgImpl oi, View view) {
		DocType.getCurDoc().setMainView(view, linesController, oi, os);

		if (oi.getData().isPotencial())
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(Color.GRAY);
	}
	
    protected View getMainView(OrgImpl orgImpl, int arg0,  View view, ViewGroup arg2){
		if (view == null)
			view = View.inflate(this, getRowResourceID(), null);
		
		if(orgImpl != null){
			setOrgBackground(arg0, orgImpl, view);
			view.setTag(orgImpl.getRowid());
			drawOrg(orgImpl, view);
			ImageView ivFolder = (ImageView) view.findViewById(R.id.ivFolder);
			
			if(ivFolder != null)
				ivFolder.setVisibility(View.GONE);
		}
		return view;
    }
    
    protected String getOrgReadingFields() { return "name,id,address,color,flags"; }
    
	class MainOrgsAdapter extends DataBaseAdapter<Org> implements FilterAdapter, Refresh
	{
		
		public MainOrgsAdapter(Context context) throws IllegalAccessException, InstantiationException 
		{
			this(context, "name");
		}
		
		protected MainOrgsAdapter(Context context, String order) {
			this(context, order, null);
		}
		
		protected MainOrgsAdapter(Context context, String order, String where) {
			super(context, new OrgImpl(), where, order);
			cursor.current().setReadingFields(getOrgReadingFields());
		}
		
		public void close() {
			cursor.close();
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			OrgImpl orgImpl = (OrgImpl)cursor.get(arg0);
			return getMainView(orgImpl, arg0, arg1, arg2);
		}

		protected void setBackground(int arg0, OrgImpl orgImpl, View view) {
			setOrgBackground(arg0, orgImpl, view);
		}

		@Override
		public void applyFilter(String value)
		{
			if (value.length() == 0){
				resetFilter();
				return;
			}
			
			super.applyFilter(getFilterStr(value));
		}

		protected String getFilterStr(String value) { return "srchName LIKE '%" + value.toUpperCase() + "%'"; }
		
		public void refresh() {
			cursor.updateIds();
		}

		@Override
		public void resetFilter()
		{
			super.resetFilter();
		}
	}
	
	class OrgFoldersAdapter extends BaseAdapter implements FilterAdapter, Refresh {
		protected OrgFoldersTree tree = createOrgFoldersTree();
		RouteFilter routeFilter = createRouteFilter();

		protected RouteFilter createRouteFilter() {
			return new RouteFilter();
		}
		
		protected OrgFoldersTree createOrgFoldersTree() { return new OrgFoldersTree(); }
		
		public void close() {
			routeFilter.close();
			tree.close();
		}
		
		public List<OrgFolderItem> getTodayItems() {
			return tree.getTodayItems();
		}
		
		@Override
		public int getCount()
		{
			return tree.getCount();
		}

		@Override
		public Object getItem(int position)
		{
			return tree.getItem(position);
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
				convertView = View.inflate(Napoleon.this, getRowResourceID(), null);

			OrgImpl org = tree.getOrg(position);
			
			setOrgBackground(position, org, convertView);
			
			convertView.setTag(tree.makeTag(position));
			String firstClmnText = tree.getFirstColumnText(position);
			
			if( org == null ) {
				TextView tvOrgName = (TextView)convertView.findViewById(R.id.tvOrgName);
				linesController.prepareTextView(tvOrgName);
				
				TextView tvOrgSum = (TextView)convertView.findViewById(R.id.tvOrgSum);
				
				tvOrgName.setTextColor(GrServerColorToSystem(tree.getTextColor(position)));
				tvOrgName.setText(firstClmnText);
				tvOrgSum.setText(tree.getSecondColumnText(position));			
			} else
				drawOrg(org, convertView);
			
			ImageView ivFolder = (ImageView) convertView.findViewById(R.id.ivFolder);

			if (isTopLevel() && !tree.isFiltered()){
				ivFolder.setImageResource(tree.isToday(position) ? R.drawable.folder_open : R.drawable.folder);
				ivFolder.setVisibility(View.VISIBLE);
			}else
				ivFolder.setVisibility(View.GONE);
			
			return convertView;
		}

		public void itemsMode(OrgFolders currentOrgFolders)
		{
			tree.currentOrgFolder = currentOrgFolders;
		}
		
		public void routeMode()
		{
			tree.currentOrgFolder = null;
		}
		
		public OrgFolders currentFolder() { return tree.currentOrgFolder; }
		
		public void refreshCurrentFolder() {
			if(tree.currentOrgFolder != null)
				for (OrgFolders io : tree.orgFolders) {
					if(io.name.equals(tree.currentOrgFolder.name)){
						tree.currentOrgFolder = io;
						break;
					}
				}
		}
		
		public boolean isTopLevel()
		{
			return tree.currentOrgFolder == null;
		}

		@Override
		public void applyFilter(String value)
		{
			tree.applyFilter(routeFilter, value);	
			super.notifyDataSetChanged();
		}
		
		public void refresh() {
			tree.resetFilter();
		}

		@Override
		public void resetFilter()
		{
			notifyDataSetChanged();
		}
		
		@Override
		public void notifyDataSetChanged() {
			resetFilterProcess();
			super.notifyDataSetChanged();
		}
		
		protected void resetFilterProcess(){
			tree.resetFilter();
		}
	}
	
	public void setTopLevelForTableHeader(){
		Adapter adapter = lvMainOrgs.getAdapter();
		
		if (adapter instanceof OrgFoldersAdapter)
		{
			OrgFoldersAdapter orgFoldersAdapter = (OrgFoldersAdapter)adapter;
			if (!orgFoldersAdapter.isTopLevel())
			{
				orgFoldersAdapter.routeMode();
				orgFoldersAdapter.notifyDataSetChanged();
				String caption = getString(R.string.caption);
				setFirstColumnCaption(caption);
				findOnClickListener.resetFilter();
				setGoUpVisibility(false);
			}
		}
	}
	
	class FirstColumnCaptionOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			setTopLevelForTableHeader();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (activeDialog != null){
			try{
				activeDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		if( remnantsReceiver != null ) {
			remnantsReceiver.cancel(false);
			remnantsReceiver = null;
		}
	}

	protected Dialog createDocMenuDlg() {
		if (docMenu == null)
			docMenu = createDocMenuList();
				
		return createMenuDlg(getString(R.string.docs), docMenu);
	}

	@Override
	public void setActiveDialog(Dialog dlg) {
		activeDialog = dlg;
	}

	protected void openUpdateActivity() {
		UpdateDBW.open(Napoleon.this);
	}

	protected void exit() {
		if (!((CfgNplW)ConfigManager.getConfig()).isAutostart) {
			Intent intent = new Intent(Napoleon.this, serviceType);
			boolean stopped = Napoleon.this.stopService(intent);
			Log.d(Consts.D_TAG, "Service has been stopped:" + Boolean.toString(stopped));
		}
		finish();
	}

	protected void doSync() {
		if(Features.CHECK_UNCOMPLETE_SCRIPTS && ScriptImpl.hasUncomplete()){
			Toast.makeText(this, R.string.has_uncomplete_scripts, Toast.LENGTH_LONG).show();
			openScriptList();
		}else	
			openUpdateActivity();
	}

	private void openScriptList() { ScriptsList.open(this); }

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			shared = IShared.Stub.asInterface(service);

			try {
				SharedPreferences prf = getSharedPreferences(LOGIN_PREF,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor e = prf.edit();
				int duration = shared.getDuration();
				e.putInt(DURATION_KEY, duration);
				e.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};
}

enum ListViewMode 
{
	ORG_LIST (0),
	ROUTE_LIST(1);
	
	public int val;
	ListViewMode( int val)
	{
		this.val = val;
	}
	
	public static ListViewMode parseInt(int val)
	{
		for (ListViewMode mode :values())
			if (val == mode.val)
				return mode;
		
		return null;
	}
};

interface Refresh{
	void refresh();
}
