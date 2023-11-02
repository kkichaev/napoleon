package com.grsoft.napoleon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.FoldersMainAdapter.ViewData;
import com.grsoft.napoleon.chart.ChartActivity;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ConfigPhotoInitilizer;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.napoleon.util.WorkTimeListener;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.util.ReceiveRemnants;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DialogOwner;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.MenuActionHandler;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPreparedEvent;
import com.grsoft.util.Updater;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.itextpdf.text.BuildConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressLint("InflateParams")
public class Main extends BaseActivity implements Selector, DialogOwner {
	public static MenuPreparedEvent docMenuPrepared = new MenuPreparedEvent();
	public static MenuPreparedEvent mainMenuPrepared = new MenuPreparedEvent();
	private static final int REQUEST_SETTING_CODE = 100;

	public static List<String> ADD_PERMISSIONS = new ArrayList<>();

	protected ListView list;
	protected View btnLines;
	protected View btnDocFilter;
	protected View btnMode;
	protected View btnFind;
	protected View llFind;
	protected EditText edFind;
	protected LinesCountController linesController;
	protected OrgSumImpl orgSum = new OrgSumImpl();
	protected BaseAdapter solidMainAdapter;
	protected BaseAdapter foldersMainAdapter;
	protected FindOnClickListener findOnClickListener;
	protected final static int SOLID_VIEW = 0;
	protected final static int FOLDER_VIEW = 1;
	protected int mode = SOLID_VIEW; 
	protected final static String LIST_MODE = "ListMode";
	protected static final String PERIOD_TYPE = "period_type";
	protected static final int MONTH_TYPE = 1;
	protected Dialog activeDialog;
	protected static final int DLG_MAIN_MENU = 1;
	protected static final int DLG_DOC = 2;
	protected static final int DOC_SUM_DLG = 3;
	protected ArrayList<MenuHandler> mainMenu = null;
	protected ArrayList<MenuHandler> docMenu = null;
	protected ReceiveRemnants remnantsReceiver = null;
	protected View tvTotalSum;
	protected FindTextWatcher textWatcher;
	public static String orgInWork = "";
	private static final int PERMISSION_REQUEST = 0;

	protected Set<String> overdueDebtOrgs = new HashSet<>();
	
	protected int totalWeight = 0;
	
	public interface MainAdapter{
		void adjustView();
		void click(int position);
	}
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(getResourceID());
		inflateView();
		initData();
		initView();
		postInit();
		checkBatteryOptimization();
		checkApplicationPermission();

		((NapoleonAppBase)getApplication()).startMainService();
	}

	@Override
	public int getPrefValue(String name, int defValue) {
		if (name.equals(LinesOnClickListener.PREF_NAME))
			return LinesOnClickListener.VARIABLE_LINE_HEIGHT;
		return super.getPrefValue(name, defValue);
	}

	private void checkBatteryOptimization() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
				Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));

				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivity(intent);
				}
			}
		}
	}

	private void checkApplicationPermission(){
		if(Build.VERSION.SDK_INT >= 23) {
			List<String> pms = new ArrayList<>();
			pms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			pms.add(Manifest.permission.CALL_PHONE);
			pms.add(Manifest.permission.CAMERA);
			pms.add(Manifest.permission.READ_PHONE_STATE);

			pms.add(Manifest.permission.ACCESS_FINE_LOCATION);
			if (Build.VERSION.SDK_INT >= 29) {
				pms.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
			}

			pms.addAll(ADD_PERMISSIONS);

			for(String p : pms) {
				if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, pms.toArray(new String[]{}), PERMISSION_REQUEST);
				}
			}

//			if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//					ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//					ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
//					ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ){
//				ActivityCompat.requestPermissions(this, pms.toArray(new String[]{}), PERMISSION_REQUEST);
//			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
		if(rc == PERMISSION_REQUEST) {
			for(int i = 0; i < result.length; i++)
//				if (result[i] != PackageManager.PERMISSION_GRANTED) {
//					showDialog(R.id.permission_not_set_dialog);
//					break;
//				}else
				if (permissions[i].equals(Manifest.permission.CAMERA)){
					Config cfg = ConfigManager.getConfig();

					if (cfg.cameraHeight == 0){
						new ConfigPhotoInitilizer().init(cfg);
						ConfigManager.save();
					}
				}
		}
	}


	protected void postInit() {
		setAdapterMode();
		initMode();
	}

	protected void initMode() {
		SharedPreferences pref = getSharedPreferences(StartFromManager.PREFERENCE, Context.MODE_PRIVATE);
		boolean sfm = pref.getBoolean(StartFromManager.OPENSYNC, false); 
		
		if (sfm	&& Path.getAgentInfo().exists())
			openSync(pref);
//		else
//			clearImpersonate();
	}

//	protected void clearImpersonate() {
//		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
//		cfg.impersonate = "";
//		ConfigManager.save();
//	}

	protected void openSync(SharedPreferences pref) {
		Editor ed = pref.edit();
		ed.putBoolean(StartFromManager.OPENSYNC, false);
		ed.apply();
		UpdateDBW.openSync(this);
	}

	protected void initData() {
		linesController = createLinesClick().getController();
		solidMainAdapter = createSolidMainAdapter();
		foldersMainAdapter = createFoldersMainAdapter();
		foldersMainAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override public void onChanged() {
				changedFoldersAdapter();
			}
		});
		findOnClickListener = createFindOnClickListener();
		mode = getPrefValue(LIST_MODE, SOLID_VIEW);
		textWatcher = new FindTextWatcher(edFind, list);
	}

	protected void changedFoldersAdapter() {
		refreshDocSum(DocType.getCurDoc());
	}

	protected FindOnClickListener createFindOnClickListener() { return new FindOnClickListener(edFind, list, llFind);	}

	protected BaseAdapter createSolidMainAdapter() { return new SolidMainAdapter(this); }
	protected BaseAdapter createFoldersMainAdapter() { return new FoldersMainAdapter(this); }
	
	protected LinesOnClickListener createLinesClick() { return new LinesOnClickListener(list, (ImageView) btnLines, this, true); }

	private void initView() {
		list.setAdapter(solidMainAdapter);
		list.setDividerHeight(0);
		list.setOnItemClickListener(onItemListClick());
		
		registerForContextMenu(list);
		btnMode.setOnClickListener(createModeClick());
		llFind.setVisibility(View.GONE);
		tvTotalSum.setOnLongClickListener(createTotalSumLongClick());
		btnFind.setOnClickListener(findOnClickListener);
		edFind.addTextChangedListener(textWatcher);

		View v = findViewById(R.id.btnDelFind);
		if( v != null )
			v.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { edFind.setText(""); }
			});

		if(Features.TRACE_WEEK_INDEX) {
			View fcv = findViewById(R.id.tvFirstColumnCaption);
			if(fcv != null) {
				fcv.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View arg0) {
						showDialog(R.id.trace_wi_dialog);
						return false;
					}
				});
			}
		}
	}

	protected OnLongClickListener createTotalSumLongClick() {
		return new OnLongClickListener() { 
			@Override
			public boolean onLongClick(View v) {
				if (DocType.getCurDoc() == DebtDoc.instance())
					return false;
				showDialog(DOC_SUM_DLG);
				return true;
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( mainMenu == null )
			mainMenu = createMainMenuList();
		
		for (MenuHandler h : mainMenu) {
			MenuItem i = menu.add(h.name);
			h.initMenu(this, i);
		}
		
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
	
	protected OnItemClickListener onItemListClick() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Adapter a = list.getAdapter();
				if(a != null && a instanceof MainAdapter)
					((MainAdapter)a).click(position);
			}};
	}

	public void openOrg(Org org, int pos){
		DocumentsW.open(this, org);
		orgInWork = org.id;
	}
	
	private OnClickListener createModeClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchListMode();
				findOnClickListener.resetFilter();
				setAdapterMode();
				if(findOnClickListener != null)
					findOnClickListener.resetFilter();
			}
		};
	}

	public void switchListMode() {	mode ^= FOLDER_VIEW; }
	
	protected void setAdapterMode(){
		BaseAdapter adapter = solidMainAdapter;
		
		if(mode == FOLDER_VIEW)
			adapter = foldersMainAdapter;
		
		if(adapter instanceof MainAdapter)
			((MainAdapter)adapter).adjustView();
		
		list.setAdapter(adapter);
		setPrefValue(LIST_MODE, mode);
	}

	private void inflateView() {
		list = (ListView) findViewById(R.id.lvMainOrgs);
		btnLines = findViewById(R.id.btnLines);
		btnMode = findViewById(R.id.btnMode);
		edFind = (EditText) findViewById(R.id.edFind);
		llFind = findViewById(R.id.llFind);
		btnDocFilter = findViewById(R.id.btnDocFilter);
		tvTotalSum = findViewById(R.id.tvTotalSum);
		btnFind = findViewById(R.id.btnFind);
	}

	protected int getResourceID() { return R.layout.main; };
	protected int getSolidRowID() { return R.layout.main_list_row; }
	protected int getFolderRowID() { return R.layout.main_list_row; }
	protected int getStopBkg() { return R.drawable.list_grey_selector; }
	
	protected void setOrgBackground(int pos, Org org, View v) {
		if( org != null && org.isStopList())
			v.setBackgroundResource(getStopBkg());
		else 
			v.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);		
	}
	
	protected DocFilterOnClickListener createDocFilter() { return new DocFilterOnClickListener(this); }
	
	public View getSolidMainView(Org org, int pos,  View view){
		if (view == null)
			view = View.inflate(this, getSolidRowID(), null);
		
		if(org != null){
			setOrgBackground(pos, org, view);
			drawOrg(org, view);
			View ivFolder = view.findViewById(R.id.ivFolder);
			if(ivFolder != null)
				ivFolder.setVisibility(View.GONE);
		}
		
		return view;
    }
	
	long countOrgSum(List<String> ids) {
		long sum = 0;
		if( OrgSumImpl.periodSum != null) {
			for(String id : ids) {
				Long s = OrgSumImpl.periodSum.get(id);
				if(s != null)
					sum += s;
			}
		} else {
			OrgSum os = orgSum.getData();
			for(String id : ids) {
				os.id = id;
				if(orgSum.read())
					sum += os.sum;
			}
		}
		
		return sum;
	}
	
	public View getFolderMainView(View view, int pos, ViewData data){
		if (view == null)
			view = View.inflate(this, getFolderRowID(), null);
		
		setOrgBackground(pos, null, view);
		
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
		
		if(tv != null){
			tv.setText(data.name);
			linesController.prepareTextView(tv);
			tv.setTextColor(Color.BLACK);
		}
		
		tv = (TextView)view.findViewById(R.id.tvOrgSum);
		
		if(tv != null){
			long sum = countOrgSum(data.ids);
			String text = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			text += "\n" + Integer.toString(data.ids.size());
			tv.setText(text);
			tv.setTextColor(getResources().getColor(R.color.grey));
		}
		
		return view;
	}

	protected void drawOrg(Org org, View view) {
		DocType.getCurDoc().setMainView(view, linesController, org, orgSum);

		if (org.isPotencial())
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(Color.GRAY);
		
		if(Features.MARK_OVERDUE_DEBTS && overdueDebtOrgs.contains(org.id)) {
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(Color.RED);
		}
	}
	
	protected void adjustViewForDocType(DocType docType) {
		DocType.setCurDoc(docType, true);
		ImageButton btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setImageResource(docType.getResurce2Id());
		refreshDocSum(docType);
		
		BaseAdapter adapter = ((BaseAdapter)list.getAdapter());
		
		if(adapter != null){
			if(adapter instanceof MainAdapter)
				((MainAdapter)adapter).adjustView();
			
			adapter.notifyDataSetChanged();
			
			if (adapter instanceof BaseMainAdapter) {
				if (orgInWork.trim().length() > 0) {
					final int pos = ((BaseMainAdapter)adapter).getPos(orgInWork);

					list.post(new Runnable() {
					    @Override
					    public void run() {
					    	list.requestFocusFromTouch();
							list.setSelection(pos);
					    }
					});
				}
			}
		}

		int res = docType.getDocTitle();
		if (res != -1)
			setTitle(res);
	}

	protected void showRouteMap() {
		OrgFolders of = ((FoldersMainAdapter) foldersMainAdapter).currentFolder();

		if (of != null) {
			ArrayList<String> ids = new ArrayList<String>();

			for (OrgFolderItem i : ((FoldersMainAdapter) foldersMainAdapter).currentFolder().items)
				ids.add(i.name);

			MapActivity.open(this, ids);
		}
	}

	public void openReports() {
		ChartActivity.open(this);
	}

	public void openDistance() {
		DistanceActivity.open(this);
	}

	public boolean isGlobusAvail() {
		return mode == FOLDER_VIEW && !((FoldersMainAdapter)foldersMainAdapter).isTopLevel();
	}

	protected void refreshDocSum(DocType docType) {
		OrgSumImpl.periodSum = null;
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		int cur_type = pref.getInt(PERIOD_TYPE, 0);
		if (docType == DebtDoc.instance()) cur_type=0;
		totalWeight = 0;
		if(cur_type > 0){
			updateTotalSum(getDocSumByPeriod(docType, cur_type), totalWeight);
		}else
			updateTotalSum(OrgSumImpl.docSum(docType.getName(), getCurrentOrgs()), totalWeight);
	}
	

	HashSet<String> getCurrentOrgs() {
		HashSet<String> ret = new HashSet<String>();
		
		ListAdapter a = list.getAdapter();
		if( a instanceof FoldersMainAdapter) {
			FoldersMainAdapter fma = (FoldersMainAdapter)a;
			OrgFolders of = fma.currentFolder();
			if( of != null )
				for(OrgFolderItem ofi : of.items)
					ret.add(ofi.name);
		}
		
		return ret;
	}
	
	protected void processDocSumDocument(Document<?> d) {}
	
	@SuppressWarnings("rawtypes")
	protected long getDocSumByPeriod(DocType docType, int period){
		OrgSumImpl.periodSum = new HashMap<String, Long>();
		long result = 0;
		String where = makePeriodWhere(docType, period);
		com.grsoft.napoleon.documents.DocList list = docType.docList(null, null, where);
		
		HashSet<String> ids = getCurrentOrgs();
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			
			if(Features.SHOW_WEIGHT_IN_MAIN_FORM && d instanceof OrderImplBase)
				totalWeight += ((OrderImplBase)d).weight(); 

			long s = d.sum();
			processDocSumDocument(d);
			
			if(ids.size() == 0 || ids.contains(d.getId()))
				result += s;
			
			long si = 0;
			if(OrgSumImpl.periodSum.containsKey(d.getId()))
				si = OrgSumImpl.periodSum.get(d.getId());
			
			OrgSumImpl.periodSum.put(d.getId(), s + si);
		}
		
		list.close();
		
		return result;
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
	
	@Override
	protected void onResume() {
		super.onResume();

		orgSum.close();
		Adapter a = list.getAdapter();
		
		if(a != null && a instanceof BaseMainAdapter)
			((BaseMainAdapter)a).reload();
		
		initScripting();
		btnDocFilter.setOnClickListener(createDocFilter());
		adjustViewForDocType(DocType.getCurDoc());
		
		if( Features.COST_MANAGER != null )
			Features.COST_MANAGER.initCost(this);

		if(Features.START_STOP) {
			String orgId = WorkTimeListener.getWorkOrg(this);
			if(orgId.length() > 0) {
				openDocumentsFormStartStop(orgId);
			}
		}

		if(Features.MARK_OVERDUE_DEBTS) {
			overdueDebtOrgs.clear();

			SQLiteDatabase db = DataBaseManager.getDataBase();

			String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
			String sql = "SELECT id FROM " + table + " WHERE paydate < ? and sumD <> 0 GROUP BY id";

			Date curDate = new Date();
			String[] args = { Long.toString(curDate.getTime()) };

			try {
				Cursor c = db.rawQuery(sql, args);
				while( c.moveToNext() )
					overdueDebtOrgs.add(c.getString(0));
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void openDocumentsFormStartStop(String orgId) {
		OrgImpl oi = new OrgImpl();
		oi.getData().id = orgId;
		oi.read();
		oi.close();
		Documents.open(this, oi.getData());
	}
	
	protected void initScripting() {
		DocType cd = DocType.getCurDoc();
		if( Features.SCRIPT_DOC && ScriptDefImpl.canScripting() ) {
			DocType scriptDoc = ScriptDoc.instance();
			if( cd != scriptDoc && !cd.outOfScript() && ScriptDefImpl.docInScript.contains(cd) == false  ) {
				DocType.setCurDoc(scriptDoc);
			}
		}else if (cd.equals(ScriptDoc.instance()))
			defDocType();
	}

	private void defDocType() {
		((NapoleonAppBase)getApplication()).setDefDocType();
	}

	public void resetFind(){
		if(findOnClickListener != null)
			findOnClickListener.resetFilter();
	}

	@Override
	public void selectedType(DocType newDocType) {
		DocType curDoc = DocType.getCurDoc();
		if( newDocType != null && (curDoc == null  || newDocType.equals(curDoc) == false) )
			adjustViewForDocType(newDocType);
	}

	@Override
	public void setActiveDialog(Dialog dlg) { activeDialog = dlg; }
	
	@Override
	public void onBackPressed() {
		if(Features.SHOW_EXIT_WARNING)
			Toast.makeText(this, R.string.ask_to_exit, Toast.LENGTH_LONG).show();
		else
			super.onBackPressed();
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
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.trace_wi_dialog) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("Расчет текущей недели");
			ab.setMessage("Cur Week");
			ab.setPositiveButton(android.R.string.ok, null);
			return ab.create();
		}else if (id == R.id.permission_not_set_dialog)
			return createPermissionNotSetDlg();

		else if (id == DLG_MAIN_MENU)
			return createMainMenuDlg();
		else if (id == DLG_DOC)
			return createDocMenuDlg();
		else if (id == DOC_SUM_DLG)
			return createDocSumDlg();

		else
			return null;
	}

	private Dialog createPermissionNotSetDlg() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Необходимо установить разрешения");
		ab.setMessage("В настройках установите все разрешения для программы!");
		ab.setCancelable(false);
		ab.setPositiveButton("Настройки", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package",Main.this.getPackageName(), null);
				intent.setData(uri);
				startActivityForResult(intent, REQUEST_SETTING_CODE);
			}
		});
		return ab.create();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SETTING_CODE)
			checkApplicationPermission();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.trace_wi_dialog) {
			if(foldersMainAdapter instanceof FoldersMainAdapter)
				((AlertDialog)dialog).setMessage(Html.fromHtml(((FoldersMainAdapter)foldersMainAdapter).getWeekIndexTrace()));
		}
		super.onPrepareDialog(id, dialog);
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
	
	protected ArrayList<MenuHandler> createMainMenuList() {
		mainMenu = new ArrayList<MenuHandler>();
		
		mainMenu.add(new MenuHandler(getString(R.string.setting), new Runnable() {			
			@Override public void run() { Setting.open(Main.this); }
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
				@Override public void run() { PotenzialOrg.open(Main.this); }
			}));

		mainMenu.add(new MenuHandler(getString(R.string.about), new Runnable() {			
			@Override public void run() { showAbout(Main.this); }
		}));

		mainMenu.add(new MenuHandler(getString(R.string.exit), new Runnable() {	@Override public void run() { exit();	}}));

		if (BuildConfig.BUILD_TYPE.equals("market")) {
			mainMenu.add(new MenuActionHandler(getString(R.string.distanceactivity_title),
					new Runnable() {@Override public void run() { openDistance(); }	}, R.drawable.ic_speed)
			);
		}

		mainMenu.add(new MenuActionHandler(getString(R.string.show_route_menu_hint), new Runnable() {
					@Override public void run() { openReports(); } }, R.drawable.ic_reports)
		);

		mainMenu.add(new MenuActionHandler(getString(R.string.reports_menu_hint),
						new Runnable() {
							@Override
							public void run() {
								showRouteMap();
							}
						},
						R.drawable.globus) {
					@Override
					public void initMenu(Context context, MenuItem item) {
						super.initMenu(context, item);
						item.setVisible(isGlobusAvail());
					}
				});

		mainMenuPrepared.menuPrepared(mainMenu, Main.this);

		return mainMenu;
	}
	
	protected void updateRemnants() {
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
	
	protected Dialog createDocMenuDlg() {
		if (docMenu == null)
			docMenu = createDocMenuList();
				
		return createMenuDlg(getString(R.string.docs), docMenu);
	}
	
	protected ArrayList<MenuHandler> createDocMenuList() {
		docMenu = new ArrayList<MenuHandler>();

		docMenu.add(new MenuHandler(getString(R.string.doc_list), new Runnable() {			
			@Override public void run() { DocList.open(Main.this); }
		}));
		
		docMenu.add(new MenuHandler(getString(R.string.dlv_doc_list), new Runnable() {			
			@Override public void run() { DlvDocList.open(Main.this); }
		}));
		
		docMenu.add(new MenuHandler(getString(R.string.msg_list), new Runnable() {			
			@Override public void run() { Messages.open(Main.this); }
		}));
		
		docMenu.add(new MenuHandler(getString(R.string.price_list), new Runnable() {			
			@Override public void run() { Warehouse.open(Main.this); }
		}));
		
		if(Features.REPORT_REQUEST)
			docMenu.add(new MenuHandler(getString(R.string.report_list), new Runnable() {			
				@Override public void run() { ReportList.open(Main.this); }
			}));

		docMenu.add(new MenuHandler(getString(R.string.task_list), new Runnable() {
			@Override public void run() { TaskListView.open(Main.this); }
		}));

//		if(Features._362)
//			docMenu.add(new MenuHandler(getString(R.string.order_report), new Runnable() {
//				@Override public void run() { OrderList.open(Main.this); }
//			}));
			
		docMenuPrepared.menuPrepared(docMenu, Main.this);
		return docMenu;
	}
	
	protected void openUpdateActivity() { UpdateDBW.open(Main.this);	}
	
	protected void doSync() {
		if(Features.CHECK_UNCOMPLETE_SCRIPTS && ScriptImpl.hasUncomplete()){
			Toast.makeText(this, R.string.has_uncomplete_scripts, Toast.LENGTH_LONG).show();
			openScriptList();
		}else	
			openUpdateActivity();
	}
	
	private void openScriptList() { ScriptsList.open(this); }
	
	public static void showAbout(final Activity owner) 
	{
        View messageView = owner.getLayoutInflater().inflate(R.layout.about, null, false);
        //TextView tvWhatNews = (TextView) messageView.findViewById(R.id.tvWhatNews);
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
        
//        if(tvWhatNews != null){
//        	tvWhatNews.setVisibility(View.VISIBLE);
//        	tvWhatNews.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//					WhatNews.open(v.getContext());
//				}
//			});
//        }
	}

	void askClearBase() {
		AlertDialog.Builder b = new AlertDialog.Builder(Main.this);
		b.setTitle("Внимание");
		b.setMessage(Html.fromHtml("<b>Программа работает от имени руководителя.</b><br/>Хотите продолжить работать или очистить базу?"));
		b.setNegativeButton("Очистить базу", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
				cfg.impersonate = "";
				cfg.login = "";
				cfg.passw = "";
				ConfigManager.save();

				DataBaseManager.clearBase();
				doExit();
			}
		});

		b.setPositiveButton("Продолжиить", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doExit();
			}
		});

		Dialog d = b.create();
		d.show();
	}

	void doExit() {
		finish();
		((NapoleonAppBase)getApplication()).exit();
	}


	protected void exit() {
		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		if(cfg.impersonate.length() > 0) {
			askClearBase();
			return;
		}
		doExit();
	}
	
	protected Dialog createDocSumDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		int ct = pref.getInt(PERIOD_TYPE, 0);
		
		builder.setSingleChoiceItems(getResources().getStringArray(R.array.doc_sum_by_period), ct, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						updateDocView(which);						
						dialog.dismiss();
					}
				});
		
		builder.setTitle(R.string.doc_sum_by_period_title);
		return builder.create();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		int pos = ((AdapterContextMenuInfo)menuInfo).position;
		Adapter a = list.getAdapter();
		
		if(a != null && a instanceof BaseMainAdapter){
			BaseMainAdapter bma = (BaseMainAdapter)a;
			Org o = bma.getOrg(pos);
			
			if(o != null){
				menu.add(0, R.id.itShowMap, menu.size(), R.string.show_on_map);
				
				if (o.isPotencial()){
					menu.add(0, R.id.itEdit, menu.size(), R.string.edit);
					menu.add(0, R.id.itVisit, menu.size(), R.string.visit);
				}
			}
		}
	}
	
	protected void openMap(Org o){
		try {
			String address = o.address;
			String uri = String.format("geo:0,0?q=%s", address );
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	protected void orgEdit(String id){
		OrgImpl org = new OrgImpl();
		if(org.read("id", id)){
			PotenzialOrg.open(this, org.getRowid(), true);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		int pos = ((AdapterContextMenuInfo)menuInfo).position;
		Adapter a = list.getAdapter();
		
		if(a != null && a instanceof BaseMainAdapter){
			BaseMainAdapter bma = (BaseMainAdapter)a;
			Org o = bma.getOrg(pos);
			
			if(o != null){
				int id = item.getItemId(); 
				if (id == R.id.itShowMap)
					openMap(o);
				else if (id == R.id.itEdit)
					orgEdit(o.id);
				else if (id == R.id.itVisit)
					openOrg(o, pos);
			}
		}
		
		return true;
	}

	protected void updateDocView(int which) {
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		int cur_type = pref.getInt(PERIOD_TYPE, 0);
		
		if(cur_type != which){
			Editor edit = pref.edit();
			edit.putInt(PERIOD_TYPE, which);
			edit.commit();
			refreshDocSum(DocType.getCurDoc());
			BaseAdapter adapter = ((BaseAdapter)list.getAdapter());
			if(adapter != null)
				adapter.notifyDataSetChanged();
		}
	}

	public void onAdapterViewAdjusted() {
		invalidateOptionsMenu();
	}
}
